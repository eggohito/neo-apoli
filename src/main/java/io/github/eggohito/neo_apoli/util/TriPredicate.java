package io.github.eggohito.neo_apoli.util;

import java.util.Objects;

@FunctionalInterface
public interface TriPredicate<L, M, R> {

    boolean test(L left, M middle, R right);

    default TriPredicate<L, M, R> and(TriPredicate<? super L, ? super M, ? super R> other) {
        Objects.requireNonNull(other);
        return (left, middle, right) -> test(left, middle, right) && other.test(left, middle, right);
    }

    default TriPredicate<L, M, R> or(TriPredicate<? super L, ? super M, ? super R> other) {
        Objects.requireNonNull(other);
        return (left, middle, right) -> test(left, middle, right) || other.test(left, middle, right);
    }

    default TriPredicate<L, M, R> negate() {
        return (left, middle, right) -> !test(left, middle, right);
    }

    @SuppressWarnings("unchecked")
    static <L, M, R> TriPredicate<L, M, R> not(TriPredicate<? super L, ? super M, ? super R> target) {
        Objects.requireNonNull(target);
        return (TriPredicate<L, M, R>) target.negate();
    }

}
