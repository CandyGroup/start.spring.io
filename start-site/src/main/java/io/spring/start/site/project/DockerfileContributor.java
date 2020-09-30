package io.spring.start.site.project;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DockerfileContributor implements ProjectContributor {

    private static final String DOCKER_DIRECTORY = "src/main/docker/";

    private final ProjectDescription projectDescription;

    public DockerfileContributor(ProjectDescription projectDescription) {
        this.projectDescription = projectDescription;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path dockerDirectory = projectRoot.resolve(DOCKER_DIRECTORY);
        Files.createDirectories(dockerDirectory);

        Files.write(dockerDirectory.resolve(".dockerignore"), ("**/*\n!*.jar\n!*.war").getBytes());

        String jvmVersion = projectDescription.getLanguage().jvmVersion().replace("1.", "");
        Files.write(dockerDirectory.resolve("Dockerfile"), ("FROM openjdk:" + ("11".equals(jvmVersion) ? "11-jre-slim" : (jvmVersion + "-alpine")) + "\n" +
                "ENV LANG=zh_CN.UTF-8 LC_ALL=zh_CN.UTF-8 TZ=Asia/Shanghai\n" +
                "CMD java ${JAVA_OPTS} -XX:+PrintCommandLineFlags -XX:AutoBoxCacheMax=20000 -Djava.security.egd=file:/dev/./urandom -jar /app.jar\n" +
                "ADD *.jar /app.jar\n").getBytes());
    }
}
