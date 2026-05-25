package no.clueless.binpacking.one_dimensional;

import no.clueless.binpacking.shared.NonEmptyList;

import java.util.*;

public class NextFitBinPacker {
    private final double bounds;

    public NextFitBinPacker(double bounds) {
        if (bounds <= 0) {
            throw new IllegalArgumentException("bounds must be positive");
        }
        this.bounds = bounds;
    }

    boolean willNeverFit(Item1D item) {
        Objects.requireNonNull(item, "item must not be null");
        return item.getBounds() > bounds;
    }

    boolean isNewBinRequired(Item1D item, double remainingCapacity) {
        Objects.requireNonNull(item, "item must not be null");
        if (remainingCapacity < 0) {
            throw new IllegalArgumentException("remainingCapacity cannot be negative, but was " + remainingCapacity);
        }

        return item.getBounds() > remainingCapacity;
    }

    public Optional<NonEmptyList<Bin1D>> pack(NonEmptyList<Item1D> items) {
        items = Objects.requireNonNull(items, "items must not be null").reverse();
        var bins       = new ArrayList<Bin1D>();
        var currentBin = new Bin1D(bounds);

        for (var item : items) {
            if (willNeverFit(item)) {
                return Optional.empty();
            }

            if (isNewBinRequired(item, currentBin.getRemainingCapacity())) {
                bins.add(currentBin);
                currentBin = new Bin1D(bounds);
            }

            currentBin.add(item);
        }

        if (currentBin.hasItems()) {
            bins.add(currentBin);
        }
        return Optional.of(new NonEmptyList<>(bins));
    }
}
