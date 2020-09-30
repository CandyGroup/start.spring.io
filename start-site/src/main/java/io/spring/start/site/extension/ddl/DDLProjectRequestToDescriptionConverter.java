package io.spring.start.site.extension.ddl;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.project.DefaultProjectRequestToDescriptionConverter;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;

public class DDLProjectRequestToDescriptionConverter implements ProjectRequestToDescriptionConverter<DDLProjectRequest> {

    @Override
    public ProjectDescription convert(DDLProjectRequest request, InitializrMetadata metadata) {
        DDLProjectDescription description = new DDLProjectDescription();
        new DefaultProjectRequestToDescriptionConverter().convert(request, description, metadata);
        description.setDdl(request.getDdl());
        return description;
    }

}