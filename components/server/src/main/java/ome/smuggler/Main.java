package ome.smuggler;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import ome.smuggler.run.AppRunner;

/**
 * Program's entry point; delegates all the work to {@link AppRunner}.
 */
@SpringBootApplication  // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Main {

    public static void main(String[] args) {
        AppRunner runner = new AppRunner();
        runner.launch(args);
    }

}
