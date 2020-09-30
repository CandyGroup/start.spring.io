package io.spring.start.site.extension.ddl;

import io.spring.initializr.web.project.WebProjectRequest;

public class DDLProjectRequest extends WebProjectRequest {

    private String ddl;

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }
}