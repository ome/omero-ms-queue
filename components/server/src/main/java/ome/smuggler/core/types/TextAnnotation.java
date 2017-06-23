package ome.smuggler.core.types;

import util.object.Pair;
import util.object.Wrapper;

/**
 * An OME textual annotation, i.e. a namespace and some text; both strings have
 * length greater than zero.
 * An instance of this object can only be obtained through a parser so that it
 * is impossible to construct an invalid value.
 */
public class TextAnnotation extends Wrapper<Pair<String, String>> {

    protected TextAnnotation(Pair<String, String> wrappedValue) {
        super(wrappedValue);
    }
    
    /**
     * @return this annotation's namespace.
     */
    public String namespace() {
        return get().fst();
    }
    
    /**
     * @return this annotation's text.
     */
    public String text() {
        return get().snd();
    }

}
