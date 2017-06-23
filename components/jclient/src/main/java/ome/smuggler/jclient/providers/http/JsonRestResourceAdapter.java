package ome.smuggler.jclient.providers.http;

import ome.smuggler.jclient.core.service.http.RestResource;
import ome.smuggler.jclient.core.service.http.RestResourceException;

import java.net.URI;

/**
 * Turns a {@link JsonResource} into a {@link RestResource} by wrapping errors
 * into {@link RestResourceException}s.
 */
public class JsonRestResourceAdapter<T> implements RestResource<T> {

    private final JsonResource<T> adaptee;

    /**
     * Creates a new instance to operate on the specified resource.
     * @param target identifies the target resource to operate on.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonRestResourceAdapter(URI target) {
        this.adaptee = new JsonResource<>(target);
    }

    @Override
    public T get(Class<T> resourceType) {
        try {
            return adaptee.get(resourceType);
        } catch (Exception e) {
            throw new RestResourceException(e);
        }
    }

    @Override
    public void post(T resource) {
        try {
            adaptee.post(resource);
        } catch (Exception e) {
            throw new RestResourceException(e);
        }
    }

    @Override
    public <R> R post(T resource, Class<R> responseType) {
        try {
            return adaptee.post(resource, responseType);
        } catch (Exception e) {
            throw new RestResourceException(e);
        }
    }

    @Override
    public void put(T resource) {
        try {
            adaptee.put(resource);
        } catch (Exception e) {
            throw new RestResourceException(e);
        }
    }

    @Override
    public void delete() {
        try {
            adaptee.delete();
        } catch (Exception e) {
            throw new RestResourceException(e);
        }
    }

}
