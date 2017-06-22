package ome.smuggler.jclient.providers.http;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.jclient.providers.http.JsonEntity.toEntity;
import static ome.smuggler.jclient.providers.http.JsonResponseHandlers.*;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 * A simple HTTP client to operate on resources represented as JSON and encoded
 * in UTF-8.
 * This client reads HTTP entities into memory in their entirety, which is not
 * likely to be appropriate when dealing with large amounts of data where it
 * would be desirable to process content in constant memory space. (In that
 * case using an object to represent your data may not be the best option anyway
 * and streaming might be your answer instead...)
 */
public class JsonResource<T> {

    private static void acceptJsonOnly(HttpRequest request) {
        ContentType json = ContentType.APPLICATION_JSON;
        request.addHeader("Accept", json.getMimeType());
        request.addHeader("Accept-Charset", json.getCharset().name());  // (*)
    }
    // (*) ContentType.APPLICATION_JSON is constructed with a charset of UTF-8.


    private final URI target;
    private final CloseableHttpClient client;

    /**
     * Creates a new instance to operate on the specified resource.
     * @param target identifies the target resource to operate on.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonResource(URI target) {
        requireNonNull(target, "target");

        this.target = target;
        this.client = HttpClients.createDefault();
    }

    /**
     * GET the resource.
     * @param resourceType the type of the JSON object; generics are not
     *                     supported.
     * @return the resource as an object.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the returned JSON could not be converted to
     * an object.
     */
    public T get(Class<T> resourceType) throws IOException {
        HttpGet get = new HttpGet(target);
        acceptJsonOnly(get);
        return client.execute(get, readResponseBodyHandler(resourceType));
    }

    /**
     * POST the resource.
     * @param resource the resource to POST.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the resource could not be serialized to JSON.
     */
    public void post(T resource) throws IOException {
        HttpPost post = new HttpPost(target);
        acceptJsonOnly(post);
        post.setEntity(toEntity(resource));
        client.execute(post, ignoreResponseBodyHandler());
    }

    /**
     * POST the resource and reads a response.
     * @param <R> response type.
     * @param resource the resource to POST.
     * @param responseType the type of the JSON object; generics are not
     *                     supported.
     * @return the response.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the resource could not be serialized to JSON.
     */
    public <R> R post(T resource, Class<R> responseType) throws IOException {
        HttpPost post = new HttpPost(target);
        acceptJsonOnly(post);
        post.setEntity(toEntity(resource));
        return client.execute(post, readResponseBodyHandler(responseType));
    }

    /**
     * PUT the resource.
     * @param resource the resource to PUT.
     * @throws IOException If a connection or protocol error occurs.
     * @throws RuntimeException if the resource could not be serialized to JSON.
     */
    public void put(T resource) throws IOException {
        HttpPut put = new HttpPut(target);
        acceptJsonOnly(put);
        put.setEntity(toEntity(resource));
        client.execute(put, ignoreResponseBodyHandler());
    }

    /**
     * DELETE the resource.
     * @throws IOException If a connection or protocol error occurs.
     */
    public void delete() throws IOException {
        HttpDelete del = new HttpDelete(target);
        client.execute(del, ignoreResponseBodyHandler());
    }

}
