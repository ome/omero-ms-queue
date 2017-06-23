package ome.smuggler.core.types;

import static ome.smuggler.core.types.ValueParserFactory.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class ImportInputTest {

    public static boolean isRunningOnWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
    
    public static PositiveN posN(String value) {
        return positiveInt(value).getRight();
    }
    
    public static TextAnnotation anno(String ns, String value) {
        return textAnnotation(ns, value).getRight();
    }
    
    public static ImportInput makeNew() {
        String target = isRunningOnWindows() ? "C:\\target\\my file"
                                             : "/target/my file";
        
        return new ImportInput(email("user@micro.edu").getRight(), 
                               targetUri(target).getRight(), 
                               omeroUri("omero", "1234").getRight(), 
                               "sessionKey");
    }
    
    private static void assertEmptyOptional(Optional<?> x) {
        assertNotNull(x);
        assertFalse(x.isPresent());
    }
    
    private static void assertOptionalValue(Optional<?> actual, Object expected) {
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(expected));
    }
    
    private static <T> void assertStream(Stream<T> actual, T[] expected) {
        assertNotNull(actual);
        assertArrayEquals(expected, actual.toArray());
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullEmail() {
        new ImportInput(null, 
                        uri("target").getRight(), 
                        uri("omero").getRight(), 
                        "sessionKey");
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTarget() {
        new ImportInput(email("some@where").getRight(), 
                        null, 
                        uri("omero").getRight(), 
                        "sessionKey");
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullOmero() {
        new ImportInput(email("some@where").getRight(), 
                        uri("target").getRight(), 
                        null, 
                        "sessionKey");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullSessionKey() {
        new ImportInput(email("some@where").getRight(), 
                        uri("target").getRight(), 
                        uri("omero").getRight(), 
                        null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptySessionKey() {
        new ImportInput(email("some@where").getRight(), 
                        uri("target").getRight(), 
                        uri("omero").getRight(), 
                        "");
    }
    
    @Test
    public void nameEmptyIfUnset() {
        assertEmptyOptional(makeNew().getName());
        assertEmptyOptional(makeNew().setName(null).getName());
    }
    
    @Test
    public void nameHasValueIfSet() {
        assertOptionalValue(makeNew().setName("").getName(), "");
        assertOptionalValue(makeNew().setName("x").getName(), "x");
    }
    
    @Test
    public void descriptionEmptyIfUnset() {
        assertEmptyOptional(makeNew().getDescription());
        assertEmptyOptional(makeNew().setDescription(null).getDescription());
    }
    
    @Test
    public void descriptionHasValueIfSet() {
        assertOptionalValue(makeNew().setDescription("").getDescription(), "");
        assertOptionalValue(makeNew().setDescription("x").getDescription(), "x");
    }
    
    @Test
    public void datatsetOrScreenEmptyIfUnset() {
        ImportInput x = makeNew();
        
        assertEmptyOptional(x.getDatasetId());
        assertEmptyOptional(x.getScreenId());
        assertFalse(x.hasDatasetId());
        assertFalse(x.hasScreenId());
    }
    
    @Test
    public void setEmptyDatasetId() {
        ImportInput x = makeNew().setDatasetId(Optional.empty());
        
        assertEmptyOptional(x.getDatasetId());
        assertEmptyOptional(x.getScreenId());
        assertFalse(x.hasDatasetId());
        assertFalse(x.hasScreenId());
    }
    
    @Test
    public void setNonEmptyDatasetId() {
        DatasetId id = new DatasetId(1L);
        ImportInput x = makeNew().setDatasetId(Optional.of(id));
        Optional<DatasetId> actual = x.getDatasetId();
        
        assertOptionalValue(actual, id.get());
        assertTrue(x.hasDatasetId());
        assertFalse(x.hasScreenId());
    }
    
    @Test
    public void setNonEmptyScreenId() {
        ScreenId id = new ScreenId(1L);
        ImportInput x = makeNew().setScreenId(Optional.of(id));
        Optional<ScreenId> actual = x.getScreenId(); 
        
        assertOptionalValue(actual, id.get());
        assertFalse(x.hasDatasetId());
        assertTrue(x.hasScreenId());
    }
    
    @Test
    public void setEmptyScreenId() {
        ImportInput x = makeNew().setScreenId(Optional.empty());
        
        assertEmptyOptional(x.getDatasetId());
        assertEmptyOptional(x.getScreenId());
        assertFalse(x.hasDatasetId());
        assertFalse(x.hasScreenId());
    }
    
    @Test
    public void settingDatasetOverridesScreen() {
        PositiveN one = posN("1"), two = posN("2");
        ImportInput x = makeNew();
        x.setScreenId(one).setDatasetId(two);
        
        assertOptionalValue(x.getDatasetId(), two);
        assertEmptyOptional(x.getScreenId());
    }
    
    @Test
    public void settingScreenOverridesDataset() {
        PositiveN one = posN("1"), two = posN("2");
        ImportInput x = makeNew();
        x.setDatasetId(one).setScreenId(two);
        
        assertOptionalValue(x.getScreenId(), two);
        assertEmptyOptional(x.getDatasetId());
    }
    
    @Test (expected = NullPointerException.class)
    public void setDatasetIdThrowsIfNullArg() {
        makeNew().setDatasetId((PositiveN)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void setDatasetIdThrowsIfNullOptional() {
        makeNew().setDatasetId((Optional<DatasetId>)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void setScreenIdThrowsIfNullArg() {
        makeNew().setScreenId((PositiveN)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void setScreenIdThrowsIfNullOptional() {
        makeNew().setScreenId((Optional<ScreenId>)null);
    }
    
    @Test
    public void textAnnotationsEmptyIfUnset() {
        assertStream(makeNew().getTextAnnotations(), array());
        assertStream(makeNew().addTextAnnotation().getTextAnnotations(), array());
        assertStream(makeNew().addTextAnnotations(Stream.empty()).getTextAnnotations(), array());
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationThrowsIfNullArray() {
        makeNew().addTextAnnotation((TextAnnotation[])null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationThrowsIfNullArg() {
        makeNew().addTextAnnotation((TextAnnotation)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationThrowsIfArrayHasNulls() {
        makeNew().addTextAnnotation(anno("n", "v"), null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationsThrowsIfNullStream() {
        makeNew().addTextAnnotations(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addTextAnnotationsThrowsIfStreamHasNulls() {
        makeNew().addTextAnnotations(Stream.of(anno("n", "v"), null));
    }
    
    @Test
    public void annotationsReturnedInSameOrderTheyWereAdded() {
        TextAnnotation a1 = anno("n1", "v1");
        TextAnnotation a2 = anno("n2", "v2");
        assertStream(makeNew().addTextAnnotation(a1, a2).getTextAnnotations(), 
                     array(a1, a2));
    }
    
    @Test
    public void annotationIdsEmptyIfUnset() {
        assertStream(makeNew().getAnnotationIds(), array());
        assertStream(makeNew().addAnnotationId().getAnnotationIds(), array());
        assertStream(makeNew().addAnnotationIds(Stream.empty()).getAnnotationIds(), array());
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfNullArray() {
        makeNew().addAnnotationId((PositiveN[])null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfNullArg() {
        makeNew().addAnnotationId((PositiveN)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfArrayHasNulls() {
        makeNew().addAnnotationId(posN("1"), null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdsThrowsIfNullStream() {
        makeNew().addAnnotationIds(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void addAnnotationIdThrowsIfStreamHasNulls() {
        makeNew().addAnnotationIds(Stream.of(posN("1"), null));
    }
    
    @Test
    public void annotationIdsReturnedInSameOrderTheyWereAdded() {
        PositiveN id1 = posN("1");
        PositiveN id2 = posN("2");
        assertStream(makeNew().addAnnotationId(id1, id2).getAnnotationIds(), 
                     array(id1, id2));
    }
    
}
