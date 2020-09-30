package io.spring.start.site.extension.ddl;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;

import java.util.Map;

public class DDLProjectGenerationController extends ProjectGenerationController<DDLProjectRequest> {

    public DDLProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                          ProjectGenerationInvoker<DDLProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
    }

    @Override
    public DDLProjectRequest projectRequest(Map<String, String> headers) {
        DDLProjectRequest request = new DDLProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }

}