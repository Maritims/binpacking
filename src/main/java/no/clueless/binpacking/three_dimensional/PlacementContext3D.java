package no.clueless.binpacking.three_dimensional;

import java.util.Objects;

public class PlacementContext3D {
    private final Item3D item;
    private final Bin3D  currentBin;
    private final double currentX;
    private final double currentY;
    private final double currentZ;

    public PlacementContext3D(Item3D item, Bin3D currentBin, double currentX, double currentY, double currentZ) {
        if (currentX < 0 || currentY < 0 || currentZ < 0) {
            throw new IllegalArgumentException("coordinates cannot be negative");
        }
        this.item       = Objects.requireNonNull(item, "item cannot be null");
        this.currentBin = Objects.requireNonNull(currentBin, "currentBin cannot be null");
        this.currentX   = currentX;
        this.currentY   = currentY;
        this.currentZ   = currentZ;
    }

    public Item3D getItem() {
        return item;
    }

    public Bin3D getCurrentBin() {
        return currentBin;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public double getCurrentZ() {
        return currentZ;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlacementContext3D that = (PlacementContext3D) o;
        return Double.compare(currentX, that.currentX) == 0 && Double.compare(currentY, that.currentY) == 0 && Double.compare(currentZ, that.currentZ) == 0 && Objects.equals(item, that.item) && Objects.equals(currentBin, that.currentBin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, currentBin, currentX, currentY, currentZ);
    }

    @Override
    public String toString() {
        return "PackingContext3D{" +
                "item=" + item +
                ", currentBin=" + currentBin +
                ", currentX=" + currentX +
                ", currentY=" + currentY +
                ", currentZ=" + currentZ +
                '}';
    }
}
