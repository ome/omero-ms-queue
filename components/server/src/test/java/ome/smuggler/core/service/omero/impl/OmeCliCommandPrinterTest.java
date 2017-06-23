package ome.smuggler.core.service.omero.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.asList;
import static ome.smuggler.core.service.omero.impl.OmeCliCommandPrinter.mask;

import org.junit.Test;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;

public class OmeCliCommandPrinterTest {

    private static ListProgramArgument<String> cmd(String...tokens) {
        return new ListProgramArgument<>(asList(tokens));
    }

    private static String print(CommandBuilder cmd) {
        return new OmeCliCommandPrinter(cmd).print();
    }

    private static String print(CommandBuilder cmd, String...optToMask) {
        return new OmeCliCommandPrinter(cmd).printMasking(optToMask);
    }

    private static String print(CommandBuilder cmd, Integer...ixToMask) {
        return new OmeCliCommandPrinter(cmd).printMasking(ixToMask);
    }

    @Test
    public void noMasking() {
        String actual = print(cmd("-my", "dirty", "secret"));
        String expected = "-my dirty secret";
        assertThat(actual, is(expected));
    }

    @Test
    public void maskOneOptionWhenEvenNumberOfTokens() {
        String actual = print(cmd("-my", "dirty secret"), "-my");
        String expected = "-my " + mask;
        assertThat(actual, is(expected));
    }

    @Test
    public void maskOneOptionWhenOddNumberOfTokens() {
        String actual = print(cmd("-my", "dirty", "secret"), "-my");
        String expected = "-my " + mask + " secret";
        assertThat(actual, is(expected));
    }

    @Test
    public void maskManyOptionsWhenEvenNumberOfTokens() {
        String actual = print(cmd("-o", "1",
                                  "-my", "dirty secret",
                                  "-o", "2",
                                  "?", "maskme",
                                  "-o", "3"), "-my", "?");
        String expected = "-o 1 -my " + mask + " -o 2 ? " + mask + " -o 3";
        assertThat(actual, is(expected));
    }

    @Test
    public void maskManyOptionsWhenOddNumberOfTokens() {
        String actual = print(cmd("-o", "1", "2",
                                  "-my", "dirty secret",
                                  "-o", "2",
                                  "?", "maskme"), "-my", "?");
        String expected = "-o 1 2 -my " + mask + " -o 2 ? " + mask;
        assertThat(actual, is(expected));
    }

    @Test
    public void maskOneIndexWhenEvenNumberOfTokens() {
        String actual = print(cmd("-my", "dirty secret"), 1);
        String expected = "-my " + mask;
        assertThat(actual, is(expected));
    }

    @Test
    public void maskOneIndexWhenOddNumberOfTokens() {
        String actual = print(cmd("-my", "dirty", "secret"), 1);
        String expected = "-my " + mask + " secret";
        assertThat(actual, is(expected));
    }

    @Test
    public void maskManyIndexesWhenEvenNumberOfTokens() {
        String actual = print(cmd("-o", "1",
                                  "-my", "dirty secret",
                                  "-o", "2",
                                  "?", "maskme",
                                  "-o", "3"), 3, 7);
        String expected = "-o 1 -my " + mask + " -o 2 ? " + mask + " -o 3";
        assertThat(actual, is(expected));
    }

    @Test
    public void maskManyIndexesWhenOddNumberOfTokens() {
        String actual = print(cmd("-o", "1", "2",
                                  "-my", "dirty secret",
                                  "-o", "2",
                                  "?", "maskme"), 4, 8);
        String expected = "-o 1 2 -my " + mask + " -o 2 ? " + mask;
        assertThat(actual, is(expected));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullCmd() {
        new OmeCliCommandPrinter(null);
    }

}
