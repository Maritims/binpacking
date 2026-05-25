package no.clueless.binpacking.two_dimensional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bin2D {
    private final double          width;
    private final double          height;
    private final double          area;
    private final List<Rectangle> items        = new ArrayList<>();
    private       double          consumedArea = 0;

    public Bin2D(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        this.width  = width;
        this.height = height;
        this.area   = width * height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @SuppressWarnings("unused")
    public double getArea() {
        return area;
    }

    public List<Rectangle> getItems() {
        return List.copyOf(items);
    }

    @SuppressWarnings("unused")
    public double getConsumedArea() {
        return consumedArea;
    }

    public void add(Rectangle rectangle) {
        Objects.requireNonNull(rectangle, "rectangle cannot be null");
        consumedArea += rectangle.area();
        items.add(rectangle);
    }

    @Override
    public String toString() {
        return "Bin2D{" +
                "width=" + width +
                ", height=" + height +
                ", area=" + area +
                ", items=" + items +
                ", consumedArea=" + consumedArea +
                '}';
    }
}
