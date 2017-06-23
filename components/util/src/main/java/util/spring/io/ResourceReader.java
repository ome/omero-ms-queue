package util.spring.io;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

import util.lambda.FunctionE;


/**
 * Converts resource data into instances of {@code T}.
 */
public interface ResourceReader<T> {

    /**
     * Converts the resource data into instances of {@code T}.
     * @param data the resource data.
     * @return the data in the input stream as instances of {@code T}; the
     * returned stream will be empty or {@code null} if the input stream was 
     * empty.
     * @throws Exception if an I/O or data conversion error occurs.
     */
    Stream<T> convert(InputStream data) throws Exception;
    
    /**
     * Convenience method, same as the other {@link #readResource(Optional) 
     * readResource} but it accepts a straight resource. 
     * This method is defaulted to call the other {@code readResource} method. 
     * @param data the resource to read from.
     * @return the resource data as {@code T}'s or an empty stream as the case
     * may be, but never {@code null}.
     */
    default Stream<T> readResource(Resource data) {
        return readResource(Optional.ofNullable(data));
    }
    
    /**
     * Reads the resource input stream and converts the data into objects if
     * a resource is present; otherwise just returns an empty stream.
     * An empty stream will also be returned if the data conversion resulted
     * in a {@code null}, e.g. if the resource stream was empty. Any exception
     * raised will be wrapped in a runtime {@link ResourceReadException} and
     * re-thrown as such.
     * This method is defaulted to carry out all the above using the {@link 
     * #convert(InputStream) convert} method. 
     * @param data the resource to read from.
     * @return the resource data as {@code T}'s or an empty stream as the case
     * may be, but never {@code null}.
     * @throws ResourceReadException to wrap any exception that was raised when
     * attempting to read the resource input stream and convert the data into 
     * objects.
     */
    default Stream<T> readResource(Optional<Resource> data) {
        requireNonNull(data, "data");
        
        FunctionE<Resource, InputStream> 
            getResourceStreamOrThrowIoE = Resource::getInputStream;

        try {
            return data.map(getResourceStreamOrThrowIoE)  // (*) see note below
                       .map(unchecked(this::convert))
                       .orElse(Stream.empty());    
        } catch (Exception e) {
            throw new ResourceReadException(e);
        }
    }
}
/* NOTE.
 * replacing: .map(getResourceStreamOrThrowIoE)
 *      with: .map(unchecked(Resource::getInputStream))
 * or for that matter
 *   FunctionE<Resource, InputStream> getResourceStreamOrThrowIoE = 
 *                                              Resource::getInputStream;
 * with
 *   Function<Resource, InputStream> getResourceStreamOrThrowIoE = 
 *                                  unchecked(Resource::getInputStream);
 *                                   
 * will confuse the hell out of the eclipse compiler when trying to do type 
 * inference, so it won't compile in eclipse, but it will in OpenJDK 1.8. 
 * Looks like I'm not alone tho as Java's typsie infirmity has caused some
 * headaches to the surgeons over here too:
 * - https://bugs.eclipse.org/bugs/show_bug.cgi?id=461004
 * 
 * But wait! It gets better. If I replace the last statement above with:
 * 
 *   return data.flatMap(unchecked(this::readResource));
 *   
 * then the OpenJDK compiler can't figure it out and tells  me the reference 
 * to unchecked is ambiguous as it could refer both to:
 *  
 *  1. Function<T, R> unchecked(FunctionE<T, R>)
 *  2. Consumer<T> unchecked(ConsumerE<T>)
 *  
 * Really?!
 * The eclipsie compiler tells me the unchecked method doesn't even exist...
 * Oh well. 
 */