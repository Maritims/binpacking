package no.clueless.binpacking.two_dimensional;

import java.util.Objects;

public class Item2D implements Comparable<Item2D> {
    private final double width;
    private final double height;

    public Item2D(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        this.width  = width;
        this.height = height;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public double area() {
        return width * height;
    }

    public Item2D rotated() {
        return new Item2D(height(), width());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item2D item2D = (Item2D) o;
        return Double.compare(width, item2D.width) == 0 && Double.compare(height, item2D.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public int compareTo(Item2D o) {
        return Double.compare(area(), o.area());
    }
}
