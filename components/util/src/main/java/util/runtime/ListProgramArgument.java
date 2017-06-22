package util.runtime;

import static util.object.Either.left;
import static util.object.Either.right;

import java.util.List;
import java.util.stream.Stream;

import util.object.Either;

/**
 * A program argument made up by a list of {@code T}-values.
 * This class doesn't allow any {@code null} in the list argument and it 
 * tokenizes it by converting each list element {@link Object#toString() to 
 * string} in list order.  
 */
public class ListProgramArgument<T> extends BaseProgramArgument<List<T>> {

    /**
     * Creates a new instance.
     */
    public ListProgramArgument() {
        super();
    }
    
    /**
     * Creates a new instance to hold the specified argument.
     * @param argToSet the argument.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the list contains any {@code null}.
     */
    public ListProgramArgument(List<T> argToSet) {
        super(argToSet);
    }
    
    @Override
    protected Either<String, List<T>> validate(List<T> argToSet) {
        for (T arg : argToSet) {
            if (arg == null) {
                return left("null list element");
            }
        }
        return right(argToSet);
    }
    
    @Override
    protected Stream<String> tokenize(List<T> arg) {
        return arg.stream().map(Object::toString);
    }
    
}
