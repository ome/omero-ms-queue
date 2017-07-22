package ome.smuggler.core.types;

import static util.string.Strings.requireString;

import java.util.UUID;

import util.object.AbstractWrapper;
import util.object.Identifiable;

/**
 * An {@link Identifiable}'s whose ID is an UUID string.
 */
public class UuidString
    extends AbstractWrapper<String> implements Identifiable {

    private final String uuid;

    /**
     * Creates a new instance with the specified UUID.
     * @param uuid
     * @throws IllegalArgumentException if the argument is {@code null}
     * or empty.
     */
    public UuidString(String uuid) {
        requireString(uuid);
        this.uuid = uuid;
    }

    /**
     * Creates a new instance with a random UUID.
     */
    public UuidString() {
        uuid = UUID.randomUUID().toString();
    }
    
    @Override
    public String id() {
        return uuid;
    }

    @Override
    public String get() {
        return uuid;
    }
    
}
