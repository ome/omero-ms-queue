package util.runtime.jvm;

import static util.sequence.Arrayz.asList;
import static util.sequence.Arrayz.hasNulls;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import util.runtime.CommandBuilder;
import util.runtime.CommandLineBuilder;
import util.runtime.ProgramArgument;


/**
 * Base class to build commands to invoke a JVM.
 * @see JarCmdBuilder
 * @see MainClassCmdBuilder
 */
public abstract class JvmCmdBuilder extends CommandLineBuilder {

    protected final List<CommandBuilder> sysProps;
    protected final List<CommandBuilder> appArgs;
    
    protected JvmCmdBuilder(ProgramArgument<Path> jvmPath) {
        super(jvmPath);
        sysProps = new ArrayList<>();
        appArgs = new ArrayList<>();
    }
    
    /**
     * Appends the specified properties to the existing list of JVM system 
     * property arguments. Order is preserved.
     * @param ps the properties to add.
     * @return itself for use in fluent API style.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public JvmCmdBuilder addProp(SysPropJvmArg...ps) {
        if (ps == null || hasNulls(ps)) {  // hasNull is false if ps is null or zero len
            throw new NullPointerException("null elements");
        }
        sysProps.addAll(asList(ps));
        return this;
    }
    
    /**
     * Appends the specified properties to the existing list of JVM system 
     * property arguments.
     * Filters out properties having a {@code null} or empty key or having a
     * {@code null} value.
     * @param <K> key type.
     * @param <V> value type.
     * @param props the properties to add.
     * @return itself for use in fluent API style.
     * @throws NullPointerException if the arguments is {@code null}.
     */
    public <K, V> JvmCmdBuilder addProps(Map<K, V> props) {
        Stream<SysPropJvmArg> ps = SysPropJvmArg.toJvmArguments(props);
        return addProp(ps.toArray(SysPropJvmArg[]::new));
    }
    
    /**
     * Appends the current system properties to the existing list of JVM system 
     * property arguments.
     * Filters out properties having a {@code null} or empty key or having a
     * {@code null} value.
     * @return itself for use in fluent API style.
     */
    public JvmCmdBuilder addCurrentSysProps() {
        return addProps(System.getProperties());
    }
    
    /**
     * Appends to the existing list of application arguments, i.e. what to pass
     * to the {@code main} method. Order is preserved.
     * @param ps the arguments to add.
     * @return itself for use in fluent API style.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public JvmCmdBuilder addApplicationArgument(CommandBuilder...ps) {
        if (ps == null || hasNulls(ps)) {  // hasNull is false if ps is null or zero len
            throw new NullPointerException("null elements");
        }
        appArgs.addAll(asList(ps));
        return this;
    }
    
}
