package no.clueless.binpacking.three_dimensional.shared;

import java.util.Objects;

public class Point3D {
    private final double x;
    private final double y;
    private final double z;

    public Point3D(double x, double y, double z) {
        if (x < 0 || y < 0 || z < 0) {
            throw new IllegalArgumentException("x, y and z cannot be negative");
        }
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Point3D atX(double x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be positive");
        }
        return new Point3D(x, y, z);
    }

    public Point3D atY(double y) {
        if (y < 0) {
            throw new IllegalArgumentException("y must be positive");
        }
        return new Point3D(x, y, z);
    }

    public Point3D atZ(double z) {
        if (z < 0) {
            throw new IllegalArgumentException("z must be positive");
        }
        return new Point3D(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return Double.compare(x, point3D.x) == 0 && Double.compare(y, point3D.y) == 0 && Double.compare(z, point3D.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
