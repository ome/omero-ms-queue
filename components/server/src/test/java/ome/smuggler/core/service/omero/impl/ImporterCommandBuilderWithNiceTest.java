package ome.smuggler.core.service.omero.impl;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import ome.smuggler.core.types.ImportInputTest;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import util.runtime.BaseProgramArgument;


@RunWith(Theories.class)
public class ImporterCommandBuilderWithNiceTest {

    @DataPoints
    public static String[] niceCommands = array(
            "nice -n 10", "start /belownormal /wait /b"
    );

    private static ImporterCommandBuilder newBuilder(String niceCommand) {
        return new ImporterCommandBuilder(
                OmeCliConfigBuilder.config(),
                ImportInputTest.makeNew(),
                ImporterCommandBuilderTest.dummyImportPath(),
                new BaseProgramArgument<>(niceCommand));
    }

    @Theory
    public void niceCommandComesFirst(String niceCommand) {
        String wholeCommand = newBuilder(niceCommand).toString();

        assertNotNull(wholeCommand);
        assertTrue(wholeCommand.startsWith(niceCommand + " "));
    }

}
