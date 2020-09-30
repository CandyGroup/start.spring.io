package io.spring.start.site.extension.dependency.mybatisplus;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Table implements Serializable {

    private static final long serialVersionUID = -2609072945437829046L;

    private String typeName;
    private String schema;
    private String tableName;
    private String comment;
    private List<Column> columns = new ArrayList<>();

    public String generateFullTableName() {
        if (schema == null || schema.length() == 0) {
            return tableName;
        }
        return schema + "." + tableName;
    }
}
