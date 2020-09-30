package io.spring.start.site.extension.build.maven;

import io.spring.initializr.generator.buildsystem.maven.*;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

@ConditionalOnBuildSystem(MavenBuildSystem.ID)
public class MavenProjectPropertyCustomizer implements BuildCustomizer<MavenBuild> {

    @Override
    public void customize(MavenBuild build) {
        build.properties()
                .property("project.build.sourceEncoding", "UTF-8")
                .property("project.reporting.outputEncoding", "UTF-8")
                .property("maven.compiler.source", "${java.version}")
                .property("maven.compiler.target", "${java.version}")
                .property("maven.compiler.compilerVersion", "${java.version}")
                .property("git-commit-plugin.version", "4.0.2")
                .property("dockerfile-maven-plugin.version", "1.4.3")
                .property("maven-resources-plugin.version", "3.2.0")
                .property("maven-deploy-plugin.version", "2.8.2")
                .property("docker.repository", "${project.artifactId}")
                .property("docker.tag", "${project.version}");

        MavenProfileContainer mavenProfileContainer = build.profiles();
        MavenProfile dev = mavenProfileContainer.id("dev");
        dev.activation().activeByDefault(true);
        dev.properties().property("spring.profiles.active", "dev").property("docker.tag", "dev");
        mavenProfileContainer.id("test").properties().property("spring.profiles.active", "test").property("docker.tag", "test");
        mavenProfileContainer.id("prod").properties().property("spring.profiles.active", "prod").property("maven.test.skip", "true");

        MavenPluginContainer plugins = build.plugins();
        plugins.add("pl.project13.maven", "git-commit-id-plugin", builder -> builder
                .version("${git-commit-plugin.version}")
                .execution("revision", executionBuilder -> executionBuilder.goal("revision"))
                .configuration(configurationBuilder -> configurationBuilder.add("failOnNoGitDirectory", "false").add("generateGitPropertiesFile", "true"))
        );
        plugins.add("org.apache.maven.plugins", "maven-resources-plugin", builder -> builder
                .version("${maven-resources-plugin.version}")
                .execution("default-resources", executionBuilder -> executionBuilder
                        .phase("validate")
                        .goal("copy-resources")
                        .configuration(configurationBuilder -> configurationBuilder.add("outputDirectory", "target/classes")
                                .add("useDefaultDelimiters", "false")
                                .configure("delimiters", b -> b.add("delimiter", "#"))
                                .configure("resources", b -> b
                                        .configure("resource", bb -> bb.add("directory", "src/main/resources/").add("filtering", "true").configure("includes", bbb -> bbb.add("include", "*.yml")))
                                        .configure("resource", bb -> bb.add("directory", "src/main/resources/").add("filtering", "false").configure("includes", bbb -> bbb.add("include", "*.yml")))
                                )))
                .execution("docker-resources", executionBuilder -> executionBuilder
                        .phase("validate")
                        .goal("copy-resources")
                        .configuration(configurationBuilder -> configurationBuilder.add("outputDirectory", "target").configure("resources", b -> b.configure("resource", bb -> bb.add("directory", "src/main/docker/").add("filtering", "false"))))));
        plugins.add("com.spotify", "dockerfile-maven-plugin", builder -> builder
                .version("${dockerfile-maven-plugin.version}")
                .execution("docker", executionBuilder -> executionBuilder
                        .phase("deploy")
                        .goal("build")
                        .goal("tag")
                        .goal("push")
                        .configuration(b -> b.add("repository", "${docker.repository}").add("tag", "${docker.tag}").add("contextDirectory", "${project.build.directory}"))));
        plugins.add("org.apache.maven.plugins", "maven-deploy-plugin", builder -> builder
                .version("${maven-deploy-plugin.version}")
                .configuration(configurationBuilder -> configurationBuilder.add("skip", "true")));

    }
}
