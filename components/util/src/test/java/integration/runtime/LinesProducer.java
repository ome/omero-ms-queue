package integration.runtime;

import java.util.function.Consumer;

public class LinesProducer {

    public static final String[] lines = new String[] { "1", "2", "3" };
    public static final String RequestWriteDelay = "1";
    public static final int WriteDelay = 5000;

    static boolean hasWriteDelayRequest(String[] args) {
        return args != null
            && args.length > 0
            && RequestWriteDelay.equals(args[0]);
    }

    static void delayIfRequested(String[] args) throws InterruptedException {
        if (hasWriteDelayRequest(args)) Thread.sleep(WriteDelay);
    }

    public static void main(String[] args) throws Exception {
        delayIfRequested(args);
        for (int k = 0; k < lines.length; ++k) {
            Consumer<String> printer = k % 2 == 0 ? System.out::println
                                                  : System.err::println;
            printer.accept(lines[k]);
            delayIfRequested(args);
        }
    }

}
