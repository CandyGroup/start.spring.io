package io.spring.start.site.extension.dependency.mybatisplus;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.squareup.javapoet.*;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.start.site.extension.ddl.DDLProjectDescription;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.text.CaseUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.spring.start.site.extension.dependency.FileCopyUtil.copy;
import static javax.lang.model.element.Modifier.PUBLIC;

public class MyBatisPlusSourceCodeContributor implements ProjectContributor {

    private static final String DEFAULT_INDENT = "    ";

    private static final List<String> SUPPORTED_DB_TYPES = Arrays.asList("mysql", "postgresql");

    private final DDLProjectDescription description;

    public MyBatisPlusSourceCodeContributor(DDLProjectDescription description) {
        this.description = description;
    }

    @Override
    @SneakyThrows
    public void contribute(Path projectRoot) {
        Set<String> dependencies = description.getRequestedDependencies().keySet();
        String dbType;
        if (!StringUtils.hasText(dbType = determineDbType(dependencies))) {
            return;
        }
        Files.createDirectories(projectRoot.resolve("src/main/resources/mappers/"));
        copy(description, projectRoot, "classpath:mybatisplus/typehandler/**", "typehandler");

        String ddl = description.getDdl();
        if (!StringUtils.hasText(ddl)) {
            return;
        }
        List<SQLStatement> stmtList = SQLUtils.parseStatements(ddl, dbType);
        List<Table> tables = stmtList.parallelStream()
                .filter(sqlStatement -> SQLCreateTableStatement.class.isAssignableFrom(sqlStatement.getClass()))
                .map(e -> toTableDefinition((SQLCreateTableStatement) e))
                .collect(Collectors.toList());

        WebApplicationType applicationType = determineApplicationType(dependencies);
        if (applicationType == WebApplicationType.SERVLET) {
            copy(description, projectRoot, "classpath:mybatisplus/web/**", "web");
            copy(description, projectRoot, "classpath:mybatisplus/argument/**", "argument");
            copy(description, projectRoot, "classpath:mybatisplus/config/WebMvcConfiguration.java", "config");
        }

        stmtList.parallelStream()
                .filter(sqlStatement -> SQLCommentStatement.class.isAssignableFrom(sqlStatement.getClass()))
                .map(e -> (SQLCommentStatement) e)
                .forEach(statement -> {
                    String comment = ((SQLCharExpr) statement.getComment()).getValue().toString();
                    if (statement.getType() == SQLCommentStatement.Type.TABLE) {
                        tables.parallelStream()
                                .filter(e -> statement.getOn().getName().getSimpleName().equals(e.getTableName()))
                                .forEach(e -> e.setComment(comment));
                    } else {
                        SQLPropertyExpr expr = (SQLPropertyExpr) statement.getOn().getExpr();
                        tables.parallelStream()
                                .filter(table -> expr.getOwnernName().equals(table.getTableName()))
                                .forEach(e -> e.getColumns().stream()
                                        .filter(c -> expr.getName().equals(c.getColumnName()))
                                        .forEach(c -> c.setComment(comment)));

                    }
                });

        tables
                .parallelStream()
                .forEach(table -> {
                    writeDomain(table, projectRoot);
                    writeMapper(table, projectRoot);
                    writeService(table, projectRoot);
                    if (applicationType == WebApplicationType.SERVLET) {
                        writeController(table, projectRoot);
                    }
                });
    }

    private Table toTableDefinition(SQLCreateTableStatement sqlStatement) {
        Table table = new Table();
        table.setSchema(sqlStatement.getSchema());
        table.setTableName(sqlStatement.getName().getSimpleName());
        table.setTypeName(CaseUtils.toCamelCase(sqlStatement.getName().getSimpleName(), true, '_'));
        Optional.ofNullable(sqlStatement.getComment()).ifPresent(c -> table.setComment(c.toString()));

        sqlStatement.getTableElementList()
                .stream()
                .map(tableElement -> (SQLColumnDefinition) tableElement)
                .forEach(col -> {
                    Column column = new Column();
                    column.setColumnName(col.getNameAsString());
                    column.setFieldName(CaseUtils.toCamelCase(col.getNameAsString(), false, '_'));
                    column.setType(SqlTypeMapper.getType(col.getDataType().getName()));
                    Optional.ofNullable(col.getComment()).ifPresent(c -> column.setComment(c.toString()));
                    if (col.getConstraints().stream().anyMatch(c -> SQLColumnPrimaryKey.class.isAssignableFrom(c.getClass()))) {
                        column.setPrimaryKey(true);
                    }
                    table.getColumns().add(column);
                });

        return table;
    }

    @SneakyThrows
    private void writeController(Table table, Path projectRoot) {
        String controllerPackageName = description.getPackageName() + ".web";
        String servicePackageName = description.getPackageName() + ".service";
        String domainPackageName = description.getPackageName() + ".domain";

        TypeSpec classBuilder = TypeSpec.classBuilder(table.getTypeName() + "Controller")
                .addModifiers(PUBLIC)
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("path", "$S", "/api/" + table.getTableName().replaceAll("_", "-") + "s").addMember("produces", "$T.APPLICATION_JSON_VALUE", MediaType.class).build())
                .superclass(ParameterizedTypeName.get(ClassName.get(controllerPackageName, "AbstractCrudController"), ClassName.get(servicePackageName, table.getTypeName() + "Service"), ClassName.get(domainPackageName, table.getTypeName())))
                .addMethod(MethodSpec.constructorBuilder().addModifiers(PUBLIC).addParameter(ClassName.get(servicePackageName, table.getTypeName() + "Service"), "service").addCode("super(service);").build())
                .build();

        JavaFile javaFile = JavaFile.builder(controllerPackageName, classBuilder).indent(DEFAULT_INDENT).build();
        javaFile.writeTo(this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage()).getSourcesDirectory());
    }

    @SneakyThrows
    private void writeService(Table table, Path projectRoot) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(table.getTypeName() + "Service")
                .addModifiers(PUBLIC)
                .addAnnotation(Service.class)
                .superclass(ParameterizedTypeName.get(ClassName.get(ServiceImpl.class), ClassName.get(description.getPackageName() + ".mapper", table.getTypeName() + "Mapper"), ClassName.get(description.getPackageName() + ".domain", table.getTypeName())));

        JavaFile javaFile = JavaFile.builder(description.getPackageName() + ".service", classBuilder.build()).indent(DEFAULT_INDENT).build();
        javaFile.writeTo(this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage()).getSourcesDirectory());
    }

    @SneakyThrows
    private void writeMapper(Table table, Path projectRoot) {
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(table.getTypeName() + "Mapper")
                .addModifiers(PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(BaseMapper.class), ClassName.get(description.getPackageName() + ".domain", table.getTypeName())));

        JavaFile javaFile = JavaFile.builder(description.getPackageName() + ".mapper", classBuilder.build()).indent(DEFAULT_INDENT).build();
        javaFile.writeTo(this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage()).getSourcesDirectory());
    }

    @SneakyThrows
    private void writeDomain(Table table, Path projectRoot) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(table.getTypeName())
                .addModifiers(PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(AnnotationSpec.builder(TableName.class).addMember("value", "$S", table.generateFullTableName()).build());
        Optional.ofNullable(table.getComment()).ifPresent(c -> classBuilder.addJavadoc(CodeBlock.of(c + "\n")));

        table.getColumns().forEach(c -> {
            FieldSpec.Builder builder = FieldSpec.builder(c.getType(), c.getFieldName(), javax.lang.model.element.Modifier.PRIVATE);
            Optional.ofNullable(c.getComment()).ifPresent(comment -> builder.addJavadoc(CodeBlock.of(comment + "\n")));
            if (c.isPrimaryKey()) {
                builder.addAnnotation(AnnotationSpec.builder(TableId.class).addMember("type", "$T.AUTO", IdType.class).build());
            }
            if ("createdAt".equals(c.getFieldName())) {
                builder.addAnnotation(AnnotationSpec.builder(TableField.class).addMember("fill", "$T.INSERT", FieldFill.class).build());
            }
            if ("updatedAt".equals(c.getFieldName())) {
                builder.addAnnotation(AnnotationSpec.builder(TableField.class).addMember("fill", "$T.INSERT_UPDATE", FieldFill.class).build());
            }
            String typeHandler = SqlTypeMapper.getTypeHandler(c.getType());
            if (typeHandler != null) {
                builder.addAnnotation(AnnotationSpec.builder(TableField.class).addMember("typeHandler", "$T.class", ClassName.get(description.getPackageName() + ".typehandler", typeHandler)).build());
            }
            classBuilder.addField(builder.build());
        });

        JavaFile javaFile = JavaFile.builder(description.getPackageName() + ".domain", classBuilder.build()).indent(DEFAULT_INDENT).build();
        javaFile.writeTo(this.description.getBuildSystem().getMainSource(projectRoot, this.description.getLanguage()).getSourcesDirectory());
    }

    private WebApplicationType determineApplicationType(Set<String> dependencies) {
        if (dependencies.contains("web")) {
            return WebApplicationType.SERVLET;
        }
        if (dependencies.contains("webflux")) {
            return WebApplicationType.REACTIVE;
        }
        return WebApplicationType.NONE;
    }

    private String determineDbType(Set<String> keySet) {
        return SUPPORTED_DB_TYPES.stream()
                .filter(keySet::contains)
                .findFirst()
                .orElse(null);
    }

}
