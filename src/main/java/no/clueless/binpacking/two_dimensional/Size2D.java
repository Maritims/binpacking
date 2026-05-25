package no.clueless.binpacking.two_dimensional;

import java.util.Objects;

public class Size2D {
    private final double width;
    private final double height;

    public Size2D(double width, double height) {
        if (width <= 0 && height <= 0) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Size2D size2D = (Size2D) o;
        return Double.compare(width, size2D.width) == 0 && Double.compare(height, size2D.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "Size2D{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
