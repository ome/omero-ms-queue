package util.runtime;

import java.util.function.Consumer;

public class LinesProducer {

    public static final String[] lines = new String[] { "1", "2", "3" };
    public static final int WriteDelay = 5000;

    public static void main(String[] args) throws Exception {
        Thread.sleep(WriteDelay);
        for (int k = 0; k < lines.length; ++k) {
            Consumer<String> printer = k % 2 == 0 ? System.out::println
                                                  : System.err::println;
            printer.accept(lines[k]);
            Thread.sleep(WriteDelay);
        }
    }

}
