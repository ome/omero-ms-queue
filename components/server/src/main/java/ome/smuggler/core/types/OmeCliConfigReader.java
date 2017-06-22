package ome.smuggler.core.types;

import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.io.FileOps;
import util.runtime.jvm.ClassPathLocator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.convert.RawConfigValues.toDuration;
import static util.string.Strings.isNullOrEmpty;

/**
 * Implements {@link OmeCliConfigSource} by reading configuration values as
 * specified by {@link OmeCliConfig} and provide type-safe access to said
 * values.
 */
public class OmeCliConfigReader implements OmeCliConfigSource {

    private static Path jarPath(OmeCliConfig config) {
        String rawPath = config.getOmeCliJarPath();
        if (isNullOrEmpty(rawPath)) {
            return defaultJarPath(config.getOmeCliJarPrefix());
        } else {
            return Paths.get(rawPath);
        }
    }

    private static Path defaultJarPath(String omeCliJarPrefix) {
        String prefix = isNullOrEmpty(omeCliJarPrefix) ?
                        OmeCliConfig.DefaultOmeCliJarPrefix :
                        omeCliJarPrefix;
        return ClassPathLocator.findBase(OmeCliConfigReader.class)
                               .map(p -> find(p, prefix))
                               .orElseThrow(err("cannot locate main jar"));
    }

    private static Path find(Path thisJar, String prefix) {
        return FileOps.listChildFiles(thisJar.getParent())
                      .filter(p -> { 
                          String fileName = p.getFileName().toString();
                          return fileName.startsWith(prefix) 
                              && fileName.endsWith(".jar");
                       })
                      .findFirst()
                      .orElseThrow(err(thisJar, prefix));
    }

    private static Supplier<RuntimeException> err(String msg) {
        return () -> new RuntimeException(msg);
    }

    private static Supplier<RuntimeException> err(Path thisJar, String prefix) {
        String msg = String.format(
                "cannot locate OME CLI jar matching: %s%s%s*.jar", 
                thisJar.getParent(), File.separator, prefix);
        return err(msg);
    }
    
    private final Path omeCliJar;
    private final Duration sessionKeepAliveInterval;

    /**
     * Creates a new instance to read the given configuration.
     * @param config the raw configuration values.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public OmeCliConfigReader(OmeCliConfig config) {
        requireNonNull(config, "config");

        omeCliJar = jarPath(config);
        sessionKeepAliveInterval =
                toDuration(config.getSessionKeepAliveInterval(),
                           OmeroDefault.SessionKeepAliveInterval);
    }

    @Override
    public Path omeCliJar() {
        return omeCliJar;
    }

    @Override
    public Duration sessionKeepAliveInterval() {
        return sessionKeepAliveInterval;
    }

}
