package util.runtime;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

/**
 * Builds the sequence of tokens that make up the string representation of a
 * command to run an external program.
 */
public interface CommandBuilder {
    
    /**
     * Builds the sequence of tokens that make up the string representation of 
     * this command, e.g. (pseudo code) {@code [java, -cp, my.jar:ur.jar, 
     * app.Main]}.
     * @return the list of tokens that make up this argument string.
     * @throws IllegalStateException if the argument hasn't been set yet.
     */
    Stream<String> tokens();  // see note at bottom of file

    /**
     * Makes a new command by appending the sequence of tokens in {@code other}
     * to this sequence.
     * <p><em>Note</em>.
     * Command builders form a monoid under the "join token lists" operation;
     * this method basically gives you the monoid's multiplication.
     * </p>
     * @param other the other sequence of tokens.
     * @return a new command having a sequence of tokens given by this sequence
     * followed by that of {@code other}.
     * @throws NullPointerException if the argument is {@code null}.
     * @see #empty() empty
     */
    default CommandBuilder join(CommandBuilder other) {
        requireNonNull(other, "other");
        return () -> Stream.concat(tokens(), other.tokens());
    }

    /**
     * Convenience {@link CommandBuilder} that produces an empty sequence of
     * tokens.
     * <p>
     * <em>Note</em>.
     * Command builders form a monoid under the "join token lists" operation;
     * this class represents the monoid's identity.
     * </p>
     * @return the empty command builder.
     * @see #join(CommandBuilder) join
     */
    static CommandBuilder empty() {
        return Stream::empty;
    }

}
/* NOTE. Quoting & escaping.
 * When and how should token be quoted and what characters should be escaped?
 * Getting this right in general across operating systems is hairy to say the
 * least, see e.g.
 * - http://stackoverflow.com/questions/6306386/how-can-i-escape-an-arbitrary-string-for-use-as-a-command-line-argument-in-bash
 * - http://stackoverflow.com/questions/6427732/how-can-i-escape-an-arbitrary-string-for-use-as-a-command-line-argument-in-windo
 * 
 * For now we feed the tokens into ProcessBuilder.command(...) which seems to
 * quote them so that each token (even if it has spaces in it) becomes an entry
 * in the program's arguments vector---i.e. what is passed to the main method.
 * See e.g. 
 * - http://stackoverflow.com/questions/12124935/processbuilder-adds-extra-quotes-to-command-line
 * - http://stackoverflow.com/questions/18099499/how-to-start-a-process-from-java-with-arguments-which-contain-double-quotes
 * 
 * However, if you dig into the code you see that ProcessBuilder just forwards
 * the tokens to a native fork & exec method which should hopefully handle the
 * escaping as well? Didn't have time to test this but it seems to work for 
 * simple cases which is all we need for now...
 * 
 * In the case of a system property containing spaces, luckily this still works 
 * so we don't need to do any extra quoting ourselves. 
 * In fact, consider the token "-Da key = a value ". ProcessBuilder will pass it
 * as a single argument to the JVM which will parse it as key = "a key " and
 * value = " a value ". This is the same as what you'd get from a straight JVM
 * invocation on the CLI as shown below:
 *      
 *      java "-Da key = a value " Main
 *  
 * If Main contains this code:
 * 
 *      String value = System.getProperty("a key ");
 *      System.out.println(">>>" + value + "<<<");
 * 
 * The output will be: 
 * 
 *      >>> a value <<<
 *      
 * Also note that quoting each token as in
 * 
 *      java -D"a key "=" a value " Main
 *      
 * has the exact same effect, the output still is:
 * 
 *      >>> a value <<<
 *      
 */