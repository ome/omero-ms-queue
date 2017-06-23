package ome.smuggler.core.convert;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static util.sequence.Arrayz.asList;
import static util.string.Strings.isNullOrEmpty;
import static util.string.Strings.requireString;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.core.types.Nat;
import ome.smuggler.core.types.PositiveN;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;

/**
 * Utility methods to convert raw config values to values of more suitable 
 * types.
 */
public class RawConfigValues {

    public static PositiveN toPositiveN(Long configValue,
                                        PositiveN defaultValue) {
        return configValue == null || configValue == 0 ?
                defaultValue :
                PositiveN.of(configValue);  // NB throws if value <= 0
    }

    public static Path toPath(String path) {
        requireString(path, "path");
        return Paths.get(path);
    }
    
    public static Duration toDuration(Long minutes) {
        PositiveN v = PositiveN.of(minutes);  // NB throws if minutes <= 0
        return Duration.ofMinutes(v.get());
    }
    
    public static Duration toDuration(Long minutes, Duration defaultValue) {
        return Optional.ofNullable(minutes)
                       .map(RawConfigValues::toDuration)
                       .orElse(defaultValue);
    }
    
    public static List<Duration> toDurationList(Long[] minutes) {
        return Stream.of(minutes == null ? new Long[0] : minutes)
                     .map(RawConfigValues::toDuration)
                     .collect(collectingAndThen(
                                 toList(), Collections::unmodifiableList));
    }
    
    public static URI toURI(String scheme, String host, int port) {
        requireString(scheme, "scheme");
        requireString(host, "host");
        Nat p = Nat.of(port);  // NB throws if port <= 0
        
        String raw = String.format("%s://%s:%s", scheme, host, p);
        return URI.create(raw);  // NB throws if not valid
    }

    public static CommandBuilder toCommand(String cmd) {
        if (isNullOrEmpty(cmd)) {
            return CommandBuilder.empty();
        }
        String[] xs = cmd.trim().split("\\s");
        return new ListProgramArgument<>(asList(xs));
    }
    /* NOTE. Dumbass parser. Will not handle quotes properly; e.g.
     *
     *      toCommand("  pgm \"this is one arg\"  ") ~~>
     *               ["pgm", "\"this", "is", "one", "arg\""]
     *
     * But that's all we need for now as we only use it for e.g.
     * - "nice -n 10"
     * - "start /belownormal /wait /b"
     */
}
