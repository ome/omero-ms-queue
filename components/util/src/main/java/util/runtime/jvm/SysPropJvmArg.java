package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static util.object.Either.left;
import static util.object.Either.right;
import static util.string.Strings.isNullOrEmpty;

import java.util.Map;
import java.util.stream.Stream;

import util.object.Either;
import util.object.Pair;
import util.runtime.BaseProgramArgument;

/**
 * A Java system property argument to pass to the JVM.
 */
public class SysPropJvmArg extends BaseProgramArgument<Pair<String, String>> {

    /**
     * Converts the given properties into a stream of system property arguments,
     * filtering out properties having a {@code null} or empty key or having a
     * {@code null} value.
     * @param <K> key type.
     * @param <V> value type.
     * @param props the key-value pairs to convert.
     * @return the converted props.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <K, V> Stream<SysPropJvmArg> toJvmArguments(Map<K, V> props) {
        requireNonNull(props, "props");
        return props.entrySet()
                    .stream()
                    .filter(e -> e.getKey() != null &&
                                 !isNullOrEmpty(e.getKey().toString()) &&
                                 e.getValue() != null)
                    .map(e -> new SysPropJvmArg(e.getKey().toString(), 
                                                e.getValue().toString()));
    }
    
    /**
     * Creates a new instance.
     */
    public SysPropJvmArg() {
        super();
    }
    
    /**
     * Sets the payload of this argument to be the specified key-value pair.
     * @param arg the key-value pair.
     * @throws IllegalArgumentException if the key is {@code null} or empty or
     * if the value is {@code null}.
     */
    public SysPropJvmArg(Pair<String, String> arg) {
        super(arg);
    }
    
    /**
     * Sets the payload of this argument to be the specified key-value pair.
     * @param key the property key.
     * @param value the property value.
     * @throws IllegalArgumentException if the key is {@code null} or empty or
     * if the value is {@code null}.
     */
    public SysPropJvmArg(String key, String value) {
        set(key, value);
    }
    
    @Override
    protected Either<String, Pair<String, String>> validate(
            Pair<String, String> p) {
        if (isNullOrEmpty(p.fst())) return left("missing key");
        if (p.snd() == null) return left("null value");
        return right(p);
    }
    
    @Override
    protected Stream<String> tokenize(Pair<String, String> arg) {
        String token = String
                      .format("-D%s=%s", arg.fst(), arg.snd());
        return Stream.of(token);
    }
    
    /**
     * Sets the payload of this argument to be the specified key-value pair.
     * @param key the property key.
     * @param value the property value.
     * @throws IllegalArgumentException if the key is {@code null} or empty or
     * if the value is {@code null}.
     */
    public void set(String key, String value) {
        set(new Pair<>(key, value));
    }
    
}
