package util.alg;

import static java.util.Objects.requireNonNull;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/**
 * Makes {@code T}-values into a {@link Monoid} provided there exist a suitable 
 * product and unit for {@code T}-values.
 */
public class MonoidProvider<T> implements Monoid<T> {

    private final BinaryOperator<T> product;
    private final Supplier<T> unit;
    
    /**
     * Creates a new instance.
     * @param product the monoidal operation over {@code T}-values.
     * @param unit supplies the monoid's unit.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public MonoidProvider(BinaryOperator<T> product, Supplier<T> unit) {
        requireNonNull(product, "product");
        requireNonNull(unit, "unit");
        
        this.product = product;
        this.unit = unit;
    }
    
    @Override
    public T unit() {
        return unit.get();
    }

    @Override
    public T multiply(T x, T y) {
        requireNonNull(x, "x");
        requireNonNull(y, "y");
        
        return product.apply(x, y);
    }

}
