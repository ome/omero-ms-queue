package ome.smuggler.web.imports;

import static java.util.stream.Collectors.toList;
import static util.lambda.Functions.constant;
import static util.object.Either.right;
import static util.object.Eithers.partitionEithers;
import static util.sequence.Streams.pruneNull;
import static util.string.Strings.asOptional;
import static util.string.Strings.unlines;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.web.Error;
import util.object.Either;
import util.object.Pair;

/**
 * {@link #build(ImportRequest...) Builds} {@link ImportInput}s out of {@link
 * ImportRequest}s.
 */
public class ImportBatchBuilder {

    private Either<Error, ImportRequestValidator> validate(ImportRequest r) {
        ImportRequestValidator validator = new ImportRequestValidator();
        return validator.validate(r).map(constant(validator));
    }

    private Stream<Either<Error, ImportRequestValidator>> validate(
            ImportRequest[] rs) {
        return pruneNull(rs).map(this::validate);
    }

    private Optional<Error> collectErrors(List<Error> xs) {
        Stream<String> reasons = xs.stream().map(e -> e.reason);
        return asOptional(unlines(reasons)).map(Error::new);
    }

    private Either<Error, Stream<ImportRequestValidator>>
    collectValidationOutcome(Stream<Either<Error, ImportRequestValidator>> xs) {
        Pair<List<Error>, List<ImportRequestValidator>>
                outcome = partitionEithers(xs);

        return collectErrors(outcome.fst())
              .<Either<Error, Stream<ImportRequestValidator>>> map(Either::left)
              .orElse(right(outcome.snd().stream()));
    }

    private ImportInput buildInput(ImportRequestValidator validator) {
        return new ImportInput(
                validator.getEmail(),
                validator.getTarget(),
                validator.getOmero(),
                validator.getSession())
                .setName(validator.getRequest().name)
                .setDescription(validator.getRequest().description)
                .setDatasetId(validator.getDatasetId())
                .setScreenId(validator.getScreenId())
                .addTextAnnotations(validator.getTextAnnotations().stream())
                .addAnnotationIds(validator.getAnnotationIds().stream());
    }

    private List<ImportInput> fromValidators(Stream<ImportRequestValidator> vs) {
        return vs.map(this::buildInput).collect(toList());
    }

    /**
     * Maps the given {@link ImportRequest}s to {@link ImportInput}s.
     * If the given array is {@code null} or empty, or if it only contains
     * {@code null} elements, an empty list will be returned. Otherwise any
     * {@code null} elements are filtered out, while the remaining non-{@code
     * null} requests are {@link ImportRequestValidator
     * validated}. If validation fails for some of them, an {@link Error} will
     * be returned that details the detected validation errors for each request
     * that didn't pass validation. On the other hand, if all the non-{@code
     * null} requests pass validation, they're turned into {@link ImportInput}s
     * and returned in a list.
     * @param requestsToBatch the requests that will be part of the batch.
     * @return either an {@link ImportInput} for each successfully validated
     * request or an error if validation failed.
     */
    public Either<Error, List<ImportInput>> build(
            ImportRequest...requestsToBatch) {
        return collectValidationOutcome(validate(requestsToBatch))
              .map(this::fromValidators);
    }

}
/* NOTE. Type inference.
 * Here's a flawless proof that Java is the best programming language ever.
 * Proof:
 * Type inference is for the weak. Real programmers don't need types
 * after all, they only need coffee, and when it comes to coffee what's
 * better than Java? Hence Java is the best programming language ever. QED.
 *
 * And in fact, collectValidationOutcome should've been written as
 *
 * Either<Error, Stream<ImportRequestValidator>>
 * collectValidationOutcome(
 *          Stream<Either<Error, ImportRequestValidator>> xs)
 *     Pair<...> outcome = partitionEithers(xs);
 *
 *     return collectErrors(outcome.fst())
 *           .map(Either::left)
 *           .orElse(right(outcome.snd().stream()));
 *
 * Except the compiler's opinion is that this last expression has type
 * Either<Error, Object> and it refuses to compile it. But wait, it gets
 * better. If you split the expression into separate lines:
 *
 *     Optional<Error> x = collectErrors(outcome.fst());
 *     Optional<Either<Error, Stream<ImportRequestValidator>>> y =
 *                     x.map(Either::left);
 *     Either<Error, Stream<ImportRequestValidator>> z =
 *                     y.orElse(right(outcome.snd().stream()));
 *     return z;
 *
 * Then it works! Wow! Not only is Java the best language out there, but
 * is also an endless source of entertainment for programmers! Couldn't
 * ask for more.
 */