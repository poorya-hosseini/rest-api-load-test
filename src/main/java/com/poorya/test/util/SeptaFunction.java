package com.poorya.test.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface SeptaFunction<A, B, C, D, E, F, G, R> {

    R apply(A a, B b, C c, D d, E e, F f, G g);

    default <K> SeptaFunction<A, B, C, D, E, F, G, K> andThen(Function<? super R, ? extends K> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d, E e, F f, G g) -> after.apply(apply(a, b, c, d, e, f, g));
    }
}
