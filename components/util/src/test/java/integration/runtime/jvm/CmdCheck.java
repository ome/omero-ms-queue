package integration.runtime.jvm;

public class CmdCheck {
    
    // separates args when dumped.
    public static final String ArgSeparator = "|"; 
    
    // args = key-value pairs used to set sys props; e.g.
    //      "some key" "some value" k2 v2
    public static void main(String[] args) {
        dumpClassPathToStderr();
        dumpArgsToStdout(args);
        boolean ok = checkProps(args);
        
        System.exit(ok ? 0 : 1);
    }
    
    static void dumpClassPathToStderr() {
        String cp = System.getProperty("java.class.path");
        System.err.print(cp);
    }
    
    static void dumpArgsToStdout(String[] args) {
        for (String a : args) {
            System.out.print(a + ArgSeparator);
        }
    }
    
    static boolean checkProps(String[] args) {
        for (int k = 0; k < args.length / 2; ++k) {
            String value = System.getProperty(args[2 * k]);
            
            if (value == null || !value.equals(args[2 * k + 1])) {
                return false;
            }
        }
        return true;
    }
    
}
