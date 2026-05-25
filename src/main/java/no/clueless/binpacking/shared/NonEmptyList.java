package no.clueless.binpacking.shared;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class NonEmptyList<T> implements Iterable<T> {
    private final List<T> list;

    public NonEmptyList(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list cannot be null or empty");
        }
        this.list = List.copyOf(list);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public NonEmptyList<T> reverse() {
        return stream()
                .sorted(Collections.reverseOrder())
                .collect(collector());
    }

    public Stream<T> stream() {
        return list.stream();
    }

    public T first() {
        return list.get(0);
    }

    public T second() {
        return list.get(1);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public static <T> Collector<T, List<T>, NonEmptyList<T>> collector() {
        return new Collector<>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return (o1, o2) -> {
                    o1.addAll(o2);
                    return o1;
                };
            }

            @Override
            public Function<List<T>, NonEmptyList<T>> finisher() {
                return NonEmptyList::new;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }
}
