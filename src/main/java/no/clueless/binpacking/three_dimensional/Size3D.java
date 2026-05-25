package no.clueless.binpacking.three_dimensional;

import java.util.Objects;

public class Size3D {
    private final double width;
    private final double height;
    private final double depth;

    public Size3D(double width, double height, double depth) {
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

    public double getLongestDimension() {
        return Math.max(width, Math.max(height, depth));
    }

    public double getGirth() {
        var longestDimension = getLongestDimension();
        if (Double.compare(longestDimension, width) == 0) {
            return 2 * (height + depth);
        }
        if (Double.compare(longestDimension, height) == 0) {
            return 2 * (width + depth);
        }
        return 2 * (width + height);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Size3D size3D = (Size3D) o;
        return Double.compare(width, size3D.width) == 0 && Double.compare(height, size3D.height) == 0 && Double.compare(depth, size3D.depth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth);
    }

    @Override
    public String toString() {
        return "Size3D{" +
                "width=" + width +
                ", height=" + height +
                ", depth=" + depth +
                '}';
    }
}
