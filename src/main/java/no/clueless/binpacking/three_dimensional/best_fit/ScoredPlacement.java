package no.clueless.binpacking.three_dimensional.best_fit;

import no.clueless.binpacking.three_dimensional.shared.Item3D;

import java.util.Objects;

class ScoredPlacement implements Comparable<ScoredPlacement> {
    private final BinPackingState state;
    private final Pocket3D        pocket;
    private final Item3D          orientation;
    private final double          score;

    ScoredPlacement(BinPackingState state, Pocket3D pocket, Item3D orientation) {
        this.state       = Objects.requireNonNull(state, "state must not be null");
        this.pocket      = Objects.requireNonNull(pocket, "pocket must not be null");
        this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
        this.score       = calculateDistanceScore();
    }

    BinPackingState getState() {
        return state;
    }

    Pocket3D getPocket() {
        return pocket;
    }

    Item3D getOrientation() {
        return orientation;
    }

    double getScore() {
        return score;
    }

    /**
     * Calculates the Euclidean distance vector from the origin (0,0,0)
     * to the furthest corner of the item if placed in this pocket.
     */
    private double calculateDistanceScore() {
        double farX = pocket.getPosition().getX() + orientation.getWidth();
        double farY = pocket.getPosition().getY() + orientation.getHeight();
        double farZ = pocket.getPosition().getZ() + orientation.getDepth();

        // Standard Euclidean distance: √(x² + y² + z²)
        return Math.sqrt((farX * farX) + (farY * farY) + (farZ * farZ));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScoredPlacement that = (ScoredPlacement) o;
        return Double.compare(score, that.score) == 0 && Objects.equals(state, that.state) && Objects.equals(pocket, that.pocket) && orientation == that.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, pocket, orientation, score);
    }

    @Override
    public String toString() {
        return "ScoredPlacement{" +
                "state=" + state +
                ", pocket=" + pocket +
                ", orientation=" + orientation +
                ", wastedWidth=" + score +
                '}';
    }

    @Override
    public int compareTo(ScoredPlacement o) {
        return Double.compare(score, o.score);
    }
}
