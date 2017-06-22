package util.spring.io;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static util.sequence.Arrayz.array;
import static util.string.Strings.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import util.object.Pair;
import util.sequence.Arrayz;
import util.string.Strings;

@RunWith(Theories.class)
public class FifoResourceLoaderAdapterTest {

    public static final String Exists = "E";
    public static final String CanRead = "R";
    public static final String Available = Exists + CanRead;
    
    @DataPoints
    public static String[] availabilities = 
            array(null, Exists, CanRead, Available);
    
    
    // [a0, a1, ...] ==> [rez(0, a0), rez(1, a1), ...]
    private static Rez[] buildResources(String[] xs) {
        return Stream.of(Arrayz.zipIndex(xs))
                     .map(p -> new Rez(p.fst(), p.snd()))
                     .toArray(Rez[]::new);
    }
    
    private static LociResourceLoader newRezLdr(String...availability) {
        Rez[] rs = buildResources(availability);
        RezLdr loader = new RezLdr(rs);
        return new FifoResourceLoaderAdapter(loader);
    }
    
    // [a0, null, a2, null, a4, ...] ==> [0, null, 2, null, 4, ...]
    // i.e. keep any null
    private static ResourceLocation[] index(String...availability) {
        return Stream.of(Arrayz.zipIndex(availability))
                     .map(p -> {
                         Integer ix = p.fst();
                         String aval = p.snd();
                         
                         return isNullOrEmpty(aval) ? aval : ix.toString();
                     })
                     .map(ix -> isNullOrEmpty(ix) ? null : 
                                                 ResourceLocation.relpath(ix))
                     .toArray(ResourceLocation[]::new);
    }
    
    private static Optional<Resource> invoke(
            boolean nullResourceHandle, String...availability) {
        LociResourceLoader target = nullResourceHandle ? 
                                    newRezLdr() : newRezLdr(availability);
        ResourceLocation[] loci = index(availability);
        return target.selectResource(loci);
    }
    
    private static boolean allNullOrEmpty(String...xs) {
        return Stream.of(xs).allMatch(Strings::isNullOrEmpty);
    }
    
    private static boolean anyAvailable(String...xs) {
        return Stream.of(xs).anyMatch(x -> Objects.equals(x, Available));
    }
    
    private static Optional<Integer> firstAvailable(String[] xs) {
        return Stream.of(Arrayz.zipIndex(xs))
                     .filter(p -> Objects.equals(p.snd(), Available))
                     .map(Pair::fst)
                     .findFirst();
    } 
   
    @Theory
    public void selectFirstAvailable(String a0, String a1, String a2, String a3) {
        String[] avails = array(a0, a1, a2, a3);
        assumeTrue(anyAvailable(avails));
        
        Integer expectedIndex = firstAvailable(avails).get();
        Optional<Resource> actual = invoke(false, avails);
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        Rez actualRez = (Rez) actual.get();
        assertThat(actualRez.locationIndex, is(expectedIndex));
    }
    
    @Theory
    public void absentResourceWhenNoResourceAvailable(String a0, String a1) {
        assumeFalse(allNullOrEmpty(a0, a1));  // avoid unnecessary tests
        assumeFalse(anyAvailable(a0, a1));
        
        Optional<Resource> actual = invoke(false, a0, a1);
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Theory
    public void absentResourceWhenLociAllNullOrEmpty(String a0, String a1) {
        assumeTrue(allNullOrEmpty(a0, a1));
        
        Optional<Resource> actual = invoke(false, a0, a1);
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Theory
    public void absentResourceWhenNullResourceHandle(String a0, String a1) {
        Optional<Resource> actual = invoke(true, a0, a1);
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void absentResourceWhenNullLoci() {
        LociResourceLoader target = newRezLdr(Available);
        Optional<Resource> actual = target
                                   .selectResource((ResourceLocation[])null);
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void absentResourceWhenEmptyLoci() {
        Optional<Resource> actual = invoke(false);
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    public static class RezLdr implements ResourceLoader {
        
        public final Rez[] resources;
        
        public RezLdr(Rez[] resources) {
            assertNotNull(resources);
            this.resources = resources;
        }
        
        @Override
        public Resource getResource(String locationIndex) {
            int ix = Integer.parseInt(locationIndex);
            return 0 <= ix && ix < resources.length ? resources[ix] : null;
        }

        @Override
        public ClassLoader getClassLoader() { return null; }
    }
    
    public static class Rez implements Resource {
        
        public final Integer locationIndex;
        public final String availability;
        
        public Rez(Integer locationIndex, String availability) {
            this.locationIndex = locationIndex;
            this.availability = availability;
        }

        @Override
        public boolean exists() {
            return Objects.equals(availability, Available)
                || Objects.equals(availability, Exists);
        }

        @Override
        public boolean isReadable() {
            return Objects.equals(availability, Available)
                || Objects.equals(availability, CanRead);
        }

        // other methods not needed for our tests.
        
        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }
        
        @Override
        public boolean isOpen() { return false; }

        @Override
        public URL getURL() throws IOException { return null; }

        @Override
        public URI getURI() throws IOException { return null; }

        @Override
        public File getFile() throws IOException { return null; }

        @Override
        public long contentLength() throws IOException { return 0; }

        @Override
        public long lastModified() throws IOException { return 0; }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            return null;
        }

        @Override
        public String getFilename() { return null; }

        @Override
        public String getDescription() { return null; }
    }
    
}
