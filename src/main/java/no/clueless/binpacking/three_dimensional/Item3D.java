package no.clueless.binpacking.three_dimensional;

import java.util.List;
import java.util.Objects;

public class Item3D implements Comparable<Item3D> {
    private final double width;
    private final double height;
    private final double depth;

    public Item3D(double width, double height, double depth) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("width, height and depth must be positive");
        }
        this.width  = width;
        this.height = height;
        this.depth  = depth;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    public double getVolume() {
        return width * height * depth;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public List<Item3D> orientations() {
        return List.of(
                new Item3D(width, height, depth),
                new Item3D(width, depth, height),
                new Item3D(height, width, depth),
                new Item3D(height, depth, width),
                new Item3D(depth, width, height),
                new Item3D(depth, height, width)
        );
    }

    @Override
    public int compareTo(Item3D o) {
        return Double.compare(getVolume(), o.getVolume());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item3D item3D = (Item3D) o;
        return Double.compare(width, item3D.width) == 0 && Double.compare(height, item3D.height) == 0 && Double.compare(depth, item3D.depth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth);
    }

    @Override
    public String toString() {
        return String.format("(%.2fx%.2fx%.2f)", width, height, depth);
    }
}
