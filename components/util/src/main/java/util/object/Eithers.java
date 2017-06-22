package util.object;

import static java.util.Objects.requireNonNull;
import static util.object.Pair.pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Common functionality to manipulate {@link Either} values.
 */
public class Eithers {

    /**
     * Collects all the left values in encounter order.
     * Any {@code null} or right value is filtered out.
     * @param <L> left value type.
     * @param xs the values to collect.
     * @return all the left values in the input stream.
     */
    public static <L> Stream<L> collectLeft(Stream<Either<L, ?>> xs) {
        requireNonNull(xs, "xs");
        return xs.filter(x -> x != null && x.isLeft())
                 .map(Either::getLeft);
    }
    
    /**
     * Collects all the right values in encounter order.
     * Any {@code null} or left value is filtered out.
     * @param <R> right value type.
     * @param xs the values to collect.
     * @return all the right values in the input stream.
     */
    public static <R> Stream<R> collectRight(Stream<Either<?, R>> xs) {
        requireNonNull(xs, "xs");
        return xs.filter(x -> x != null && x.isRight())
                 .map(Either::getRight);
    }

    /**
     * Collects left and right values in separate lists, in encounter order.
     * @param <L> left value type.
     * @param <R> right value type.
     * @param xs the values to collect.
     * @return a pair whose first element contains the input left values, in
     * encounter order, and second element contains the input right values,
     * again in encounter order.
     */
    public static <L, R> Pair<List<L>, List<R>> partitionEithers(
            Stream<Either<L, R>> xs) {
        requireNonNull(xs, "xs");

        List<L> ls = new ArrayList<>();
        List<R> rs = new ArrayList<>();
        xs.filter(Objects::nonNull)
          .forEach(x -> {
              if (x.isLeft()) {
                  ls.add(x.getLeft());
              } else {
                  rs.add(x.getRight());
              }
        });

        return pair(ls, rs);
    }

}
