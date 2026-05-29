package no.clueless.binpacking.three_dimensional.best_fit;

import no.clueless.binpacking.three_dimensional.shared.Point3D;
import no.clueless.binpacking.three_dimensional.shared.Size3D;

import java.util.Objects;

class Pocket3D {
    private final Point3D    position;
    private final Size3D     size;
    private final PocketType type;
    private final double     localRowDepth;
    private final double     localShelfHeight;

    public enum PocketType {
        CURRENT_ROW,
        NEW_ROW,
        NEW_SHELF
    }

    Pocket3D(Point3D position, Size3D size, PocketType type, double localRowDepth, double localShelfHeight) {
        this.position         = Objects.requireNonNull(position, "position cannot be null");
        this.size             = Objects.requireNonNull(size, "size cannot be null");
        this.type             = Objects.requireNonNull(type, "type cannot be null");
        this.localRowDepth    = localRowDepth;
        this.localShelfHeight = localShelfHeight;
    }

    Point3D getPosition() {
        return position;
    }

    Size3D getSize() {
        return size;
    }

    PocketType getType() {
        return type;
    }

    double getLocalRowDepth() {
        return localRowDepth;
    }

    double getLocalShelfHeight() {
        return localShelfHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pocket3D pocket3D = (Pocket3D) o;
        return Double.compare(localRowDepth, pocket3D.localRowDepth) == 0 && Double.compare(localShelfHeight, pocket3D.localShelfHeight) == 0 && Objects.equals(position, pocket3D.position) && Objects.equals(size, pocket3D.size) && type == pocket3D.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, size, type, localRowDepth, localShelfHeight);
    }

    @Override
    public String toString() {
        return "Pocket3D{" +
                "position=" + position +
                ", size=" + size +
                ", type=" + type +
                ", localRowDepth=" + localRowDepth +
                ", localShelfHeight=" + localShelfHeight +
                '}';
    }
}
