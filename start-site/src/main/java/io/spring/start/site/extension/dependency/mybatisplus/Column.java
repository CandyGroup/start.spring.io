package io.spring.start.site.extension.dependency.mybatisplus;

import lombok.Data;

import java.io.Serializable;

@Data
public class Column implements Serializable {

    private static final long serialVersionUID = -4932907469012364567L;

    private boolean primaryKey = false;
    private String fieldName;
    private String columnName;
    private Class<?> type;
    private String comment;

}
