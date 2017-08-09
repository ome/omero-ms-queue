package ome.smuggler.providers.json;

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import util.io.SourceReader;

/**
 * Parses an object in JSON serialized form as found in a given data source.
 */
public class JsonSourceReader<T> implements SourceReader<Reader, T> {

    private final Gson mapper;
    private final Type valueType;
    
    /**
     * Creates a new instance to read the JSON representation of an object.
     * A reader created with this constructor works fine as long as the 
     * serialized data to read is not that of an object of generic type;
     * if that is the case, use the {@link #JsonSourceReader(TypeToken) other}
     * constructor instead.
     * @param valueType the class of the object to read.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonSourceReader(Class<T> valueType) {
        requireNonNull(valueType, "valueType");
        
        this.mapper = new Gson();
        this.valueType = valueType;
    }
    
    /**
     * Creates a new instance to read the JSON representation of an object.
     * Use this constructor when the serialized data to read is that of an 
     * object of generic type; if that is not the case, you should use the 
     * {@link #JsonSourceReader(Class) other} constructor instead.
     * @param valueType the generic class of the object to read, e.g.
     * <code>new TypeToken&lt;Optional&lt;Integer&gt;&gt;(){}</code>.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonSourceReader(TypeToken<T> valueType) {
        requireNonNull(valueType, "valueType");
        
        this.mapper = new Gson();
        this.valueType = valueType.getType();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T read(Reader source) throws JsonSyntaxException, JsonIOException {
        requireNonNull(source, "source");

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
