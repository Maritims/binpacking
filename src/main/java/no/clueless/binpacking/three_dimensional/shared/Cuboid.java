package no.clueless.binpacking.three_dimensional.shared;

import java.util.Objects;

public class Cuboid {
    private final double width;
    private final double height;
    private final double depth;
    private final double x;
    private final double y;
    private final double z;

    public Cuboid(double width, double height, double depth, double x, double y, double z) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("width, height and depth must be positive");
        }
        if (x < 0 || y < 0 || z < 0) {
            throw new IllegalArgumentException("x, y and z cannot be negative");
        }
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        this.x      = x;
        this.y      = y;
        this.z      = z;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @SuppressWarnings("unused")
    public double getDepth() {
        return depth;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double x2() {
        return x + width;
    }

    public double y2() {
        return y + height;
    }

    public double z2() {
        return z + depth;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cuboid cuboid = (Cuboid) o;
        return Double.compare(width, cuboid.width) == 0 && Double.compare(height, cuboid.height) == 0 && Double.compare(depth, cuboid.depth) == 0 && Double.compare(x, cuboid.x) == 0 && Double.compare(y, cuboid.y) == 0 && Double.compare(z, cuboid.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth, x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(%.2fx%.2fx%.2f) @ (%.2f,%.2f,%.2f)", width, height, depth, x, y, z);
    }
}
