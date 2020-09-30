package io.spring.start.site.project;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ApplicationYamlContributor implements ProjectContributor {

    private static final String RESOURCES_DIRECTORY = "src/main/resources/";

    private final ProjectDescription projectDescription;

    public ApplicationYamlContributor(ProjectDescription projectDescription) {
        this.projectDescription = projectDescription;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path resourceDir = projectRoot.resolve(RESOURCES_DIRECTORY);
        Files.createDirectories(resourceDir);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        Yaml yaml = new Yaml(options);

        Map<String, Object> config = new LinkedHashMap<>();
        Map<String, Object> prodConfig = new LinkedHashMap<>();

        Map<String, Object> springConfig = new LinkedHashMap<>();
        springConfig.put("application", Collections.singletonMap("name", projectDescription.getArtifactId()));
        springConfig.put("profiles", Collections.singletonMap("active", "#spring.profiles.active#"));
        config.put("spring", springConfig);

        Set<String> dependencies = projectDescription.getRequestedDependencies().keySet();
        if (dependencies.contains("mybatis-plus")) {
            Map<String, Object> mybatisConfig = new LinkedHashMap<>();
            mybatisConfig.put("global-config", Collections.singletonMap("banner", false));
            mybatisConfig.put("type-aliases-package", projectDescription.getPackageName() + ".domain");
            mybatisConfig.put("configuration", Collections.singletonMap("default-enum-type-handler", "org.apache.ibatis.type.EnumOrdinalTypeHandler"));
            mybatisConfig.put("mapper-locations", "classpath*:/mappers/*.xml");
            mybatisConfig.put("type-handlers-package", projectDescription.getPackageName() + ".typehandler");
            config.put("mybatis-plus", mybatisConfig);
        }
        if (dependencies.contains("actuator")) {
            Map<String, Object> managementConfig = new LinkedHashMap<>();
            managementConfig.put("endpoints", Collections.singletonMap("web", Collections.singletonMap("exposure", Collections.singletonMap("include", "*"))));
            managementConfig.put("endpoint", Collections.singletonMap("health", Collections.singletonMap("show-details", "always")));
            managementConfig.put("metrics", Collections.singletonMap("tags", Collections.singletonMap("application", "${spring.application.name}")));
            config.put("management", managementConfig);
            prodConfig.put("management", Collections.singletonMap("endpoints", Collections.singletonMap("web", Collections.singletonMap("exposure", Collections.singletonMap("include", Arrays.asList("info", "health", "prometheus"))))));
        }

        yaml.dump(config, new FileWriter(resourceDir.resolve(getFileName(null)).toFile()));
        yaml.dump(prodConfig, new FileWriter(resourceDir.resolve(getFileName("prod")).toFile()));
        Files.createFile(resourceDir.resolve(getFileName("test")));
    }

    private String getFileName(String profile) {
        return "application" + (profile == null ? "" : ("-" + profile)) + ".yml";
    }
}
