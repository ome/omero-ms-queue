package ome.smuggler.jclient.providers.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import ome.smuggler.jclient.core.convert.SourceReader;

import java.io.Reader;
import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

/**
 * Parses an object in JSON serialized form as found in a given data source.
 */
public class JsonSourceReader<T> implements SourceReader<T> {

    private final Gson mapper;
    private final Type valueType;
    private final Reader source;
    
    /**
     * Creates a new instance to read the JSON representation of an object.
     * A reader created with this constructor works fine as long as the 
     * serialized data to read is not that of an object of generic type;
     * if that is the case, use the {@link #JsonSourceReader(TypeToken, Reader) 
     * other} constructor instead.
     * @param valueType the class of the object to read.
     * @param source provides access to the JSON data to deserialize into a
     * {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonSourceReader(Class<T> valueType, Reader source) {
        requireNonNull(valueType, "valueType");
        requireNonNull(source, "source");
        
        this.mapper = new Gson();
        this.valueType = valueType;
        this.source = source;
    }
    
    /**
     * Creates a new instance to read the JSON representation of an object.
     * Use this constructor when the serialized data to read is that of an 
     * object of generic type; if that is not the case, you should use the 
     * {@link #JsonSourceReader(Class, Reader) other} constructor instead.
     * @param valueType the generic class of the object to read, e.g.
     * <code>new TypeToken&lt;Optional&lt;Integer&gt;&gt;(){}</code>.
     * @param source provides access to the JSON data to deserialize into a
     * {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonSourceReader(TypeToken<T> valueType, Reader source) {
        requireNonNull(valueType, "valueType");
        requireNonNull(source, "source");
        
        this.mapper = new Gson();
        this.valueType = valueType.getType();
        this.source = source;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T read() throws JsonSyntaxException, JsonIOException {
        if (Primitives.isPrimitive(valueType)) {
            Object parsed = mapper.fromJson(source, valueType);
            return (T) Primitives.wrap(parsed.getClass()).cast(parsed);
        }
        else {
            return mapper.fromJson(source, valueType);
        }
    }
    /* NOTE.
     * Looking at the code in:
     *  
     *  + Gson.fromJson(Reader, Type)
     *  + Gson.fromJson(Reader, Class<T>)
     * 
     * it seems the two cases can be unified as we've done here, provided we
     * can recover the class of the type parameter T even in the case T is
     * a generic class which is why we need the TypeToken as well. 
     */
}
