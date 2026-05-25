package no.clueless.binpacking.one_dimensional;

import java.util.Objects;

public class Item1D implements Comparable<Item1D> {
    private final double bounds;

    Item1D(double bounds) {
        if (bounds <= 0) {
            throw new IllegalArgumentException("bounds must be positive");
        }
        this.bounds = bounds;
    }

    public double getBounds() {
        return bounds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item1D item = (Item1D) o;
        return Double.compare(bounds, item.bounds) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bounds);
    }

    @Override
    public String toString() {
        return "Item{" +
                "bounds=" + bounds +
                '}';
    }

    @Override
    public int compareTo(Item1D o) {
        return Objects.compare(bounds, o.bounds, Double::compareTo);
    }
}
