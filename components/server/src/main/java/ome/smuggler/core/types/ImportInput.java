package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.sequence.Arrayz.hasNulls;
import static util.string.Strings.requireString;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import util.types.Email;
import util.types.PositiveN;

/**
 * Details what an import task must do.
 */
public class ImportInput {

    private final Email experimenterEmail;
    private final URI target;
    private final URI omero;
    private final String sessionKey;
    private Optional<String> name;
    private Optional<String> description;    
    private Optional<DatasetId> datasetId;
    private Optional<ScreenId> screenId;
    private final List<TextAnnotation> textAnnotations;
    private final List<PositiveN> annotationIds;

    public ImportInput(Email experimenterEmail, URI target, URI omero, 
                       String sessionKey) {
        requireNonNull(experimenterEmail, "experimenterEmail");
        requireNonNull(target, "target");
        requireNonNull(omero, "omero");
        requireString(sessionKey, "sessionKey");
        
        this.experimenterEmail = experimenterEmail;
        this.target = target;
        this.omero = omero;
        this.sessionKey = sessionKey;
        name = Optional.empty();
        description = Optional.empty();
        datasetId = Optional.empty();
        screenId = Optional.empty();
        textAnnotations = new ArrayList<>();
        annotationIds = new ArrayList<>();
    }

    public Email getExperimenterEmail() {
        return experimenterEmail;
    }

    public URI getTarget() {
        return target;
    }

    public URI getOmeroHost() {
        return omero;
    }

    public String getSessionKey() {
        return sessionKey;
    }
    
    public Optional<String> getName() {
        return name;
    }

    public ImportInput setName(String n) {
        this.name = Optional.ofNullable(n);
        return this;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public ImportInput setDescription(String d) {
        this.description = Optional.ofNullable(d);
        return this;
    }
    
    public Optional<DatasetId> getDatasetId() {
        return datasetId;
    }
    
    public ImportInput setDatasetId(PositiveN id) {
        requireNonNull(id, "id");
        datasetId = Optional.of(new DatasetId(id.get()));
        screenId = Optional.empty();
        return this;
    }
    
    public ImportInput setDatasetId(Optional<DatasetId> id) {
        requireNonNull(id, "id");
        id.ifPresent(this::setDatasetId);
        return this;
    }
    
    public boolean hasDatasetId() {
        return datasetId.isPresent();
    }
    
    public Optional<ScreenId> getScreenId() {
        return screenId;
    }
    
    public ImportInput setScreenId(PositiveN id) {
        requireNonNull(id, "id");
        screenId = Optional.of(new ScreenId(id.get()));
        datasetId = Optional.empty();
        return this;
    }

    public ImportInput setScreenId(Optional<ScreenId> id) {
        requireNonNull(id, "id");
        id.ifPresent(this::setScreenId);
        
        return this;
    }
    
    public boolean hasScreenId() {
        return screenId.isPresent();
    }
    
    public ImportInput addTextAnnotation(TextAnnotation...xs) {
        if (xs == null || hasNulls(xs)) {  // zero len is okay tho
            throw new NullPointerException();
        }
        if (xs.length == 1) {
            textAnnotations.add(xs[0]);
        }
        if (xs.length > 1) {
            textAnnotations.addAll(Arrays.asList(xs));
        }
        
        return this;
    }
    
    public ImportInput addTextAnnotations(Stream<TextAnnotation> xs) {
        requireNonNull(xs, "xs");
        xs.forEach(this::addTextAnnotation);
        
        return this;
    }
    
    public ImportInput addAnnotationId(PositiveN...xs) {
        if (xs == null || hasNulls(xs)) {  // zero len is okay tho
            throw new NullPointerException();
        }
        if (xs.length == 1) {
            annotationIds.add(xs[0]);
        }
        if (xs.length > 1) {
            annotationIds.addAll(Arrays.asList(xs));
        }
        
        return this;
    }
    
    public ImportInput addAnnotationIds(Stream<PositiveN> xs) {
        requireNonNull(xs, "xs");
        xs.forEach(this::addAnnotationId);
        
        return this;
    }
    
    public Stream<TextAnnotation> getTextAnnotations() {
        return textAnnotations.stream();
    }
    
    public Stream<PositiveN> getAnnotationIds() {
        return annotationIds.stream();
    }
    
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ImportInput) {
            ImportInput other = (ImportInput) x;
            return Objects.equals(experimenterEmail, other.experimenterEmail)
                && Objects.equals(target, other.target)
                && Objects.equals(omero, other.omero)
                && Objects.equals(sessionKey, other.sessionKey)
                && Objects.equals(name, other.name)
                && Objects.equals(description, other.description)
                && Objects.equals(datasetId, other.datasetId)
                && Objects.equals(screenId, other.screenId)
                && Objects.equals(textAnnotations, other.textAnnotations)
                && Objects.equals(annotationIds, other.annotationIds);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(experimenterEmail, target, omero, sessionKey,
                            name, description, datasetId, screenId, 
                            textAnnotations, annotationIds);
    }
    
}
