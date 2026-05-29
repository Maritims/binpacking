package no.clueless.binpacking.three_dimensional.best_fit;

import no.clueless.binpacking.three_dimensional.shared.Item3D;

import java.util.Objects;

class ScoredPlacement implements Comparable<ScoredPlacement> {
    private final BinPackingState state;
    private final Pocket3D        pocket;
    private final Item3D          orientation;
    private final double          wastedWidth;

    ScoredPlacement(BinPackingState state, Pocket3D pocket, Item3D orientation, double wastedWidth) {
        this.state       = Objects.requireNonNull(state, "state must not be null");
        this.pocket      = Objects.requireNonNull(pocket, "pocket must not be null");
        this.orientation = Objects.requireNonNull(orientation, "orientation cannot be null");
        this.wastedWidth = wastedWidth;
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

    double getWastedWidth() {
        return wastedWidth;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScoredPlacement that = (ScoredPlacement) o;
        return Double.compare(wastedWidth, that.wastedWidth) == 0 && Objects.equals(state, that.state) && Objects.equals(pocket, that.pocket) && orientation == that.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, pocket, orientation, wastedWidth);
    }

    @Override
    public String toString() {
        return "ScoredPlacement{" +
                "state=" + state +
                ", pocket=" + pocket +
                ", orientation=" + orientation +
                ", wastedWidth=" + wastedWidth +
                '}';
    }

    @Override
    public int compareTo(ScoredPlacement o) {
        // 1. Primary score: Minimize horizontal wasted width
        int widthCompare = Double.compare(this.wastedWidth, o.wastedWidth);
        if (widthCompare != 0) {
            return widthCompare;
        }

        // 2. Secondary tie-breaker: Prioritize CURRENT_ROW over other types
        var thisType = this.pocket.getType();
        var otherType = o.pocket.getType();
        if (thisType == Pocket3D.PocketType.CURRENT_ROW && otherType != Pocket3D.PocketType.CURRENT_ROW) return -1;
        if (otherType == Pocket3D.PocketType.CURRENT_ROW && thisType != Pocket3D.PocketType.CURRENT_ROW) return 1;

        // 3. Tertiary tie-breaker: If pocket types match, minimize wasted vertical headroom
        if (thisType == otherType) {
            var thisWastedHeight = this.pocket.getSize().getHeight() - this.orientation.getHeight();
            var otherWastedHeight = o.pocket.getSize().getHeight() - o.orientation.getHeight();
            return Double.compare(thisWastedHeight, otherWastedHeight);
        }

        return 0;
    }
}
