package ome.smuggler.run;

import java.io.IOException;
import java.util.List;

import util.error.Exceptions;
import ome.smuggler.config.data.SpringBootAppPropsFile;


/**
 * Run this class redirecting {@code stdout} to 
 * {@code config/application.properties} to generate the file. 
 * This way we can keep all config data in Java and avoid common issues.
 * <pre>
 * java -jar build/libs/ome-smuggler-*.jar ome.smuggler.run.SpringBootPropsGen \
 * {@literal >} src/main/resources/config/application.properties
 *</pre>
 */
public class SpringBootPropsGen implements RunnableApp {

    private void dumpProps() throws IOException {
        new SpringBootAppPropsFile()
            .defaultReadConfig()
            .findFirst()
            .get()
            .store(System.out, "Spring Boot Properties");
    }
    
    /**
     * Dumps Java props to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        try {
            dumpProps();
        } catch (IOException e) {
            Exceptions.throwAsIfUnchecked(e);  // see note at bottom of file
        }
    }

}
/* NOTE. The ghostly Lambda Jamda.
 * The night was sultry in Jakarta and the mist was thick. Jay Sleuth was still
 * out on the streets, still clueless about Lambda's whereabouts, following a 
 * weak lead from a bad snitch who had reported the lightning appearance of a
 * silhouette resembling Lambda's slender body:
 * 
 *     public void run(List<String> appArgs) {
 *         Exceptions.unchecked(this::dumpProps);
 *     }
 * 
 * But Jay knew this would not compile because of the emptiness of Java's void.
 * So Lambda had to be somewhere else, possibly here:
 * 
 *     public void run(List<String> appArgs) {
 *         Exceptions.unchecked(() -> { dumpProps(); return 0; });
 *     }
 * 
 * So Jay set a breakpoint in dumpProps, fired the debugger and...nothing, nada,
 * zilch, zero! The breakpoint was never hit, stdout remained quiet, and Lambda
 * vanished, never to be seen again. 
 */
