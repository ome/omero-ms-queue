package util.object;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Mimics an object's fluent interface by means of function composition.
 * This interface lets you build a chain of transformations to produce a
 * target object of type {@code T}. Specifically, you {@link #make(Function)
 * start} with a factory function {@code f : S → T} taking a seed value of
 * type {@code S} and returning a {@code T}-instance. Then you further modify
 * the initial {@code T}-instance {@link #with(Function) with} transformations
 * {@code T → T} ending up with a function composition chain:
 * {@code S → T → T → ... → T}.
 * This is useful when you want to use a fluent interface on a class that
 * that doesn't have one or if you want to have a fluent interface without
 * having to add all the needed methods to one class. Another use of this
 * interface is to build a DSL to configure an object.
 *
 * @param <S> the type of the factory seed argument used to instantiate a
 *          {@code T}-object.
 * @param <T> the type of the target object to build.
 */
@FunctionalInterface
public interface Builder<S, T> extends Function<S, T> {

    /**
     * Makes a builder with an initial object created by the specified
     * factory.
     * @param factory creates this builder's initial object.
     * @param <S> the type of the factory seed argument used to instantiate a
     *          {@code T}-object.
     * @param <T> the type of the target object to build.
     * @return the builder.
     */
    static <T, S> Builder<S, T> make(Function<S, T> factory) {
        requireNonNull(factory, "factory");
        return factory::apply;
    }

    /**
     * Makes a builder with an initial object created by the specified
     * factory.
     * @param factory creates this builder's initial object.
     * @param <T> the type of the target object to build.
     * @return the builder.
     */
    static <T> Builder<Void, T> make(Supplier<T> factory) {
        requireNonNull(factory, "factory");
        return any -> factory.get();
    }

    /**
     * Convenience function composition alias to use for fluent setting of
     * properties in the target {@code T}-object.
     * @param transformer transforms the {@code T}-object produced by this
     *                    builder.
     * @return the composite of the transformer after this builder.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default Builder<S, T> with(Function<T, T> transformer) {
        requireNonNull(transformer, "transformer");
        return s -> this.andThen(transformer).apply(s);
    }

    /**
     * Convenience function composition alias to use for fluent setting of
     * properties in the target {@code T}-object.
     * @param setter sets some value in the {@code T}-object produced by this
     *               builder.
     * @return a function that first applies this builder, then the setter,
     * and then returns the {@code T}-object.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default Builder<S, T> with(Consumer<T> setter) {
        requireNonNull(setter, "setter");
        return s -> {
            T t = apply(s);
            setter.accept(t);
            return t;
        };
    }

}
