package io.spring.start.site.extension.ddl;

import io.spring.initializr.generator.project.MutableProjectDescription;

public class DDLProjectDescription extends MutableProjectDescription {

    private String ddl;

    DDLProjectDescription() {
    }

    DDLProjectDescription(DDLProjectDescription source) {
        super(source);
        this.ddl = source.getDdl();
    }

    @Override
    public DDLProjectDescription createCopy() {
        return new DDLProjectDescription(this);
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

}
