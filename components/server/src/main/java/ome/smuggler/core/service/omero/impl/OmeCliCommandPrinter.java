package ome.smuggler.core.service.omero.impl;

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static util.sequence.Streams.pruneNull;
import static util.sequence.Streams.zip;

import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import util.object.Pair;
import util.runtime.CommandBuilder;

/**
 * Pretty-prints an {@link OmeCliCommandBuilder OME CLI command}, masking the
 * values of selected options if desired.
 */
public class OmeCliCommandPrinter {

    /**
     * The string used to replace the value of an option to mask.
     */
    public static final String mask = "•";

    private final CommandBuilder command;

    /**
     * Creates a new instance.
     * @param command the command to pretty-print.
     * @throws NullPointerException if the {@code command} is {@code null}.
     */
    public OmeCliCommandPrinter(CommandBuilder command) {
        requireNonNull(command, "command");
        this.command = command;
    }

    private Stream<Pair<Integer, String>> indexTokens() {
        Stream<Integer> ix = IntStream.iterate(0, i -> i + 1).mapToObj(x -> x);
        return zip(ix, command.tokens());
    }

    private Set<Integer> findIndexesToMask(Set<String> optionsToMask) {
        return indexTokens().filter(p -> optionsToMask.contains(p.snd()))
                            .map(p -> p.fst() + 1)
                            .collect(toSet());
    }

    private String maskIfNeeded(Pair<Integer, String> p, Set<Integer> ix) {
        return ix.contains(p.fst()) ? mask : p.snd();
    }

    private String printMasking(Set<Integer> indexes) {
        return indexTokens().map(p -> maskIfNeeded(p, indexes))
                            .collect(joining(" "));
    }

    /**
     * Pretty-prints the command to a string.
     * @return the command as a string.
     */
    public String print() {
        return printMasking(emptySet());
    }

    /**
     * Pretty-prints the command to a string, masking the value of each
     * specified option.
     * @param optionName name of an option whose value has to be masked when
     *                   printing; if no list is given, nothing will be masked.
     * @return the command as a string.
     */
    public String printMasking(String...optionName) {
        Set<String> optionsToMask = pruneNull(optionName).collect(toSet());
        return printMasking(findIndexesToMask(optionsToMask));
    }

    /**
     * Pretty-prints the command to a string, masking the token at the
     * specified index.
     * @param index index of a token to mask when printing; if no list is given,
     *              nothing will be masked.
     * @return the command as a string.
     */
    public String printMasking(Integer...index) {
        Set<Integer> ixs = pruneNull(index).collect(toSet());
        return printMasking(ixs);
    }

}
