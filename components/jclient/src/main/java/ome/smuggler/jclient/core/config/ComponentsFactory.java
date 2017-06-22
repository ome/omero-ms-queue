package ome.smuggler.jclient.core.config;

import java.net.URI;

import ome.smuggler.jclient.core.service.http.RestResource;
import ome.smuggler.jclient.core.service.imports.ImportService;
import ome.smuggler.jclient.core.service.imports.impl.ImportServiceImpl;
import ome.smuggler.jclient.core.service.omero.SessionService;
import ome.smuggler.jclient.core.service.omero.impl.SessionServiceImpl;
import ome.smuggler.jclient.providers.http.JsonRestResourceAdapter;


/**
 * Binds component implementations to interfaces so we can code against
 * interfaces rather than depending on actual implementations.
 */
public class ComponentsFactory {

    public static <T> RestResource<T> jsonResource(URI target) {
        return new JsonRestResourceAdapter<>(target);
    }

    public static SessionService omeroSession() {
        return new SessionServiceImpl();
    }

    public static ImportService importer() {
        return new ImportServiceImpl();
    }

}
