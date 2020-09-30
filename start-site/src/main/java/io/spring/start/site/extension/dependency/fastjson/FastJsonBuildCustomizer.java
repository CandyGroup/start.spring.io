package io.spring.start.site.extension.dependency.fastjson;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

public class FastJsonBuildCustomizer implements BuildCustomizer<Build> {

    public static final String FASTJSON_ID = "fastjson";

    @Override
    public void customize(Build build) {
        build.dependencies().add(FASTJSON_ID);
    }
}
