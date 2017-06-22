package ome.smuggler.core.types;

import static util.string.Strings.requireString;

import java.util.UUID;

import util.object.AbstractWrapper;
import util.object.Identifiable;

/**
 * Base class for {@link Identifiable}'s whose ID is an UUID string.
 */
public class BaseStringId 
    extends AbstractWrapper<String> implements Identifiable {

    private final String uuid; 
    
    public BaseStringId(String uuid) {
        requireString(uuid);
        this.uuid = uuid;
    }
    
    public BaseStringId() {
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
