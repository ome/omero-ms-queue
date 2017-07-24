package ome.smuggler.web.imports;

import static java.util.stream.Collectors.toList;
import static ome.smuggler.core.types.ValueParserFactory.*;
import static ome.smuggler.web.Error.error;
import static util.object.Either.left;
import static util.object.Either.right;
import static util.object.Eithers.collectLeft;
import static util.sequence.Arrayz.isNullOrZeroLength;
import static util.string.Strings.isNullOrEmpty;
import static util.string.Strings.unlines;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.core.types.*;
import ome.smuggler.web.Error;
import util.object.Either;
import util.types.Email;
import util.types.PositiveN;
import util.validation.Validator;

/**
 * Validates an {@link ImportRequest}.
 * Validation is carried out using field parsers to check whether it's possible
 * to instantiate valid values from the provided input fields and if the {@link
 * #validate(ImportRequest) validate} method returns successfully (i.e. right
 * value) the parsed values will be available through the various getters 
 * provided by this class. 
 */
public class ImportRequestValidator implements Validator<Error, ImportRequest> {

    private ImportRequest request;

    private Either<String, Email> email;
    private Either<String, URI> target;
    private Either<String, URI> omero;
    private Either<String, String> session;
    private Optional<Either<String, DatasetId>> datasetId;
    private Optional<Either<String, ScreenId>> screenId;
    private List<Either<String, TextAnnotation>> textAnnotations;
    private List<Either<String, PositiveN>> annotationIds;
    
    private List<Either<String, ?>> parseResults;
    
    
    private void applyDefaults(ImportRequest r) {
        if (isNullOrEmpty(r.omeroPort)) {
            r.omeroPort = OmeroDefault.Port.toString();
        }
    }
    
    private void checkRequiredFields(ImportRequest r) {
        email = label("experimenterEmail", email(r.experimenterEmail));
        target = label("targetUri", targetUri(r.targetUri));
        omero = label("omeroHost, omeroPort", omeroUri(r.omeroHost, r.omeroPort));
        session = label("sessionKey", string(r.sessionKey));
        
        parseResults.addAll(Arrays.asList(email, target, omero, session));
    }
    
    private void checkImageContainerId(ImportRequest r) {
        if (isNullOrEmpty(r.datasetId)) {
            datasetId = Optional.empty();
        }
        else {
            datasetId = Optional.of(label("datasetId", datasetId(r.datasetId)));
        }
        if (isNullOrEmpty(r.screenId)) {
            screenId = Optional.empty();
        }
        else {
            screenId = Optional.of(label("screenId", screenId(r.screenId)));
        }
        if (datasetId.isPresent() && screenId.isPresent()) {
            parseResults.add(left("datasetId and screenId are mutually exclusive"));
        } else {
            datasetId.ifPresent(parseResults::add);
            screenId.ifPresent(parseResults::add);
        }
    }
    
    private void checkTextAnnotations(ImportRequest r) {
        if (isNullOrZeroLength(r.textAnnotations)) {
            textAnnotations = new ArrayList<>();
        } else {
            textAnnotations = Stream.of(r.textAnnotations)
                             .map(p -> label("annotation", textAnnotation(p)))
                             .collect(toList());
            
            parseResults.addAll(textAnnotations);
        }
    }
    
    private void checkAnnotationIds(ImportRequest r) {
        if (isNullOrZeroLength(r.annotationIds)) {
            annotationIds = new ArrayList<>();
        } else {
            annotationIds = Stream.of(r.annotationIds)
                           .map(x -> label("annotation id", positiveInt(x)))
                           .collect(toList());
            
            parseResults.addAll(annotationIds);
        }
    }
    
    private Optional<String> collectErrors() {
        String errors = unlines(collectLeft(parseResults.stream()));
        return isNullOrEmpty(errors) ? Optional.empty() : Optional.of(errors);
    }
    
    @Override
    public Either<Error, ImportRequest> validate(ImportRequest r) {
        request = r;
        if (r != null) {
            parseResults = new ArrayList<>();
            
            applyDefaults(r);
            
            checkRequiredFields(r);
            checkImageContainerId(r);
            checkTextAnnotations(r);
            checkAnnotationIds(r);

            return collectErrors()
                  .<Either<Error, ImportRequest>> map(Error::error)
                  .orElse(right(r));
        }
        return error("no import request");
    }

    // util getters to use *only* in case validation succeeds

    public ImportRequest getRequest() {
        return request;
    }

    public Email getEmail() {
        return email.getRight();
    }
    
    public URI getTarget() {
        return target.getRight();
    }
    
    public URI getOmero() {
        return omero.getRight();
    }
    
    public String getSession() {
        return session.getRight();
    }
    
    public Optional<DatasetId> getDatasetId() {
        return datasetId.map(Either::getRight);
    }
    
    public Optional<ScreenId> getScreenId() {
        return screenId.map(Either::getRight);
    }
    
    public List<TextAnnotation> getTextAnnotations() {
        return textAnnotations.stream().map(Either::getRight).collect(toList());
    }
    
    public List<PositiveN> getAnnotationIds() {
        return annotationIds.stream().map(Either::getRight).collect(toList());
    }
    
}
