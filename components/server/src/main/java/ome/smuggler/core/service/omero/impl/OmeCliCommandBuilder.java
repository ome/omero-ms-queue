package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;
import static util.runtime.jvm.JvmCmdFactory.java;
import static util.sequence.Arrayz.asList;

import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.core.types.OmeCliConfigSource;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.jvm.JarJvmArg;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Template class for OMERO CLI command builders.
 */
public abstract class OmeCliCommandBuilder implements CommandBuilder {

    protected static ListProgramArgument<String> arg(String...tokens) {
        return new ListProgramArgument<>(asList(tokens));
    }
    
    protected static <T> ListProgramArgument<String> optionalArg(
            String argName, Optional<T> argValue) {
        return argValue.map(Object::toString)
                       .map(n -> arg(argName, n))
                       .orElse(arg());
    }
    
    
    protected final OmeCliConfigSource config;
    
    protected OmeCliCommandBuilder(OmeCliConfigSource config) {
        requireNonNull(config, "config");
        
        this.config = config;
    }

    protected abstract String commandName();

    protected abstract CommandBuilder assembleArguments(JvmCmdBuilder bareCommand);

    protected JarJvmArg jarFile() {
        return new JarJvmArg(config.omeCliJar());
    }

    protected CommandBuilder assembleCommand() {
        JvmCmdBuilder bareCommand = java(jarFile())
                                   .addApplicationArgument(arg(commandName()));
        return assembleArguments(bareCommand);
    }

    @Override
    public Stream<String> tokens() { 
        return assembleCommand().tokens();
    }

    @Override
    public String toString() {
        return new OmeCliCommandPrinter(this).print();
    }

}
