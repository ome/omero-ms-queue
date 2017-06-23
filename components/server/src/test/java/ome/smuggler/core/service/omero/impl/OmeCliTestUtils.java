package ome.smuggler.core.service.omero.impl;

import util.runtime.CommandBuilder;

import java.net.URI;
import java.util.Arrays;

import static ome.smuggler.core.types.ValueParserFactory.omeroUri;

public class OmeCliTestUtils {

    public static URI omeroServer() {
        return omeroUri("somehost", "1234").getRight();
    }

    public static String sessionKey() {
        return "sessionKey";
    }

    public static String commandName(CommandBuilder cli) {
        String[] whole = cli.tokens().toArray(String[]::new);
        return whole[3];
    }

    public static String[] commandArgs(CommandBuilder cli) {
        String[] whole = cli.tokens().toArray(String[]::new);
        return Arrays.copyOfRange(whole, 4, whole.length);
    }

}
