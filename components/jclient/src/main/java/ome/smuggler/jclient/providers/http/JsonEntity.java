package ome.smuggler.jclient.providers.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import ome.smuggler.jclient.providers.json.JsonSinkWriter;
import ome.smuggler.jclient.providers.json.JsonSourceReader;

/**
 * JSON serialisation to/from {@link HttpEntity}.
 */
public class JsonEntity {

    private static ContentType ensureJsonContent(HttpEntity entity)
            throws ClientProtocolException {
        ContentType type = ContentType.get(entity);
        String expected = ContentType.APPLICATION_JSON     // (1)
                                     .toString()
                                     .replace(" ", "");    // (2)
        if (type == null) {
            throw new ClientProtocolException("Unspecified content type.");
        }
        String actual = type.toString().replace(" ", "");  // (2)
        if (!expected.equalsIgnoreCase(actual)) {
            throw new ClientProtocolException(
                    String.format("Invalid content type: %s", actual));
        }
        return type;
    }
    /* (1) It's constructed with a charset of UTF-8, so when string-ified it
     * becomes: "application/json; charset=utf-8".
     * (2) Make sure these two are considered equal (note the extra space):
     * "application/json;charset=utf-8", "application/json; charset=utf-8"
     */

    static <T> StringEntity toEntity(T resource) {
        StringWriter sink = new StringWriter();
        new JsonSinkWriter<>(sink).write(resource);
        String serializedResource = sink.toString();
        return new StringEntity(serializedResource,
                                ContentType.APPLICATION_JSON);  // (*)
    }
    // (*) It's constructed with a charset of UTF-8.

    static <T> T fromEntity(HttpEntity resource, Class<T> resourceType)
            throws IOException {
        ContentType type = ensureJsonContent(resource);
        Reader source = new InputStreamReader(
                                resource.getContent(), type.getCharset());
        return new JsonSourceReader<>(resourceType, source).read();
    }

}
