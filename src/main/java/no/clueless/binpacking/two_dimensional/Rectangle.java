package no.clueless.binpacking.two_dimensional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rectangle {
    private final double width;
    private final double height;
    private final double x;
    private final double y;

    public Rectangle(double width, double height, double x, double y) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x and y cannot be negative");
        }
        this.width  = width;
        this.height = height;
        this.x      = x;
        this.y      = y;
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

    public double x1() {
        return x;
    }

    public double y1() {
        return y;
    }

    public double x2() {
        return x + width;
    }

    public double y2() {
        return y + height;
    }

    public boolean fits(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        return this.width >= width && this.height >= height;
    }

    /**
     * Does this rectangle fully contain the other rectangle?
     *
     * @return True if the other rectangle is fully contained within this rectangle, otherwise false.
     */
    public boolean contains(Rectangle other) {
        Objects.requireNonNull(other, "other cannot be null");
        return x1() <= other.x1() &&
                y1() <= other.y1() &&
                x2() >= other.x2() &&
                y2() >= other.y2();
    }

    public boolean intersects(Rectangle other) {
        Objects.requireNonNull(other, "other cannot be null");
        return x1() < other.x2() && x2() > other.x1() && y1() < other.y2() && y2() > other.y1();
    }

    public List<Rectangle> extract(Rectangle other) {
        Objects.requireNonNull(other, "other cannot be null");

        var newSlots = new ArrayList<Rectangle>();

        if (other.x1() > x1() && other.x1() < x2()) {
            var newLeftSlot = new Rectangle(other.x1() - x1(), height(), x1(), y1());
            newSlots.add(newLeftSlot);
        }

        if (other.x2() > x1() && other.x2() < x2()) {
            var newRightSlot = new Rectangle(x2() - other.x2(), height(), other.x2(), y1());
            newSlots.add(newRightSlot);
        }

        if (other.y1() > y1() && other.y1() < y2()) {
            var newBottomSlot = new Rectangle(width(), other.y1() - y1(), x1(), y1());
            newSlots.add(newBottomSlot);
        }

        if (other.y2() > y1() && other.y2() < y2()) {
            var newTopSlot = new Rectangle(width(), y2() - other.y2(), x1(), other.y2());
            newSlots.add(newTopSlot);
        }

        return newSlots;
    }

    @Override
    public String toString() {
        return String.format("%.2fx%.2f at (%.2f, %.2f)", width(), height(), x1(), y1());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return Double.compare(width, rectangle.width) == 0 && Double.compare(height, rectangle.height) == 0 && Double.compare(x, rectangle.x) == 0 && Double.compare(y, rectangle.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, x, y);
    }
}
