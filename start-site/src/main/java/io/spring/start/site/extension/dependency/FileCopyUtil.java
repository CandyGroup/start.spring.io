package io.spring.start.site.extension.dependency;

import io.spring.initializr.generator.project.ProjectDescription;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileCopyUtil {

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    @SneakyThrows
    public static void copy(ProjectDescription description, Path projectRoot, String templateFilePath, String packageName) {
        Path output = description.getBuildSystem().getMainSource(projectRoot, description.getLanguage()).getSourcesDirectory()
                .resolve(description.getPackageName().replace(".", "/"))
                .resolve(packageName);
        Files.createDirectories(output);
        Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(templateFilePath);
        for (Resource resource : resources) {
            Path path = resource.getFile().toPath();
            byte[] content = new String(Files.readAllBytes(path)).replace("packageName", description.getPackageName()).getBytes();
            Path filePath = output.resolve(path.getFileName());
            Files.createFile(filePath);
            Files.write(filePath, content);
        }
    }
}
