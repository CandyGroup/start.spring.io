package io.spring.start.site.extension.dependency.prometheus;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

public class PrometheusBuildCustomizer implements BuildCustomizer<Build> {

    public static final String PROMETHEUS_ID = "prometheus";

    @Override
    public void customize(Build build) {
        build.dependencies().add(PROMETHEUS_ID);
    }
}
