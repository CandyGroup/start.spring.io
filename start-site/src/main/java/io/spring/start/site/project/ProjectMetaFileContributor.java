package io.spring.start.site.project;

import io.spring.initializr.generator.project.contributor.MultipleResourcesProjectContributor;

/**
 * @author jy
 */
public class ProjectMetaFileContributor extends MultipleResourcesProjectContributor {

    public ProjectMetaFileContributor() {
        super("classpath:project");
    }
}
