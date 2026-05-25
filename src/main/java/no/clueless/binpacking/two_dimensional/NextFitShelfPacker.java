package no.clueless.binpacking.two_dimensional;

import no.clueless.binpacking.shared.NonEmptyList;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class NextFitShelfPacker {
    private final Size2D bounds;

    public NextFitShelfPacker(Size2D bounds) {
        this.bounds = Objects.requireNonNull(bounds, "bounds cannot be null");
    }

    boolean willNeverFit(Item2D item) {
        Objects.requireNonNull(item, "item cannot be null");
        var doesNotFitNormally = item.width() > bounds.width() || item.height() > bounds.height();
        var doesNotFitRotated  = item.height() > bounds.width() || item.width() > bounds.height();
        return doesNotFitNormally || doesNotFitRotated;
    }

    boolean isHorizontalWrappingRequired(Item2D item, double currentX) {
        Objects.requireNonNull(item, "item cannot be null");
        if (currentX < 0) {
            throw new IllegalArgumentException("currentX cannot be negative");
        }
        return item.width() + currentX > bounds.width();
    }

    boolean isNewBinRequired(Item2D item, double currentY) {
        Objects.requireNonNull(item, "item cannot be null");
        if (currentY < 0) {
            throw new IllegalArgumentException("currentY cannot be negative");
        }
        return item.height() + currentY > bounds.height();
    }

    Item2D bestOrientation(Item2D item, double currentX, double currentY) {
        Objects.requireNonNull(item, "item cannot be null");
        if (currentX < 0 || currentY < 0) {
            throw new IllegalArgumentException("currentX and currentY cannot be negative");
        }

        var rotated      = item.rotated();
        var fitsNormally = bounds.width() >= item.width() + currentX && bounds.height() >= item.height() + currentY;
        var fitsRotated  = bounds.width() >= rotated.width() + currentX && bounds.height() >= rotated.height() + currentY;

        if (fitsNormally && fitsRotated) {
            // Prefer the orientation consuming the least shelf width.
            return rotated.width() < item.width() ? rotated : item;
        } else if (fitsRotated) {
            return rotated;
        }

        return item;
    }

    public Optional<NonEmptyList<Bin2D>> pack(NonEmptyList<Item2D> items) {
        items = Objects.requireNonNull(items, "items cannot be null").reverse();
        var bins          = new ArrayList<Bin2D>();
        var currentBin    = new Bin2D(bounds.width(), bounds.height());
        var currentX      = 0.0d;
        var currentY      = 0.0d;
        var currentHeight = 0.0d;

        for (var originalItem : items) {
            if (willNeverFit(originalItem)) {
                return Optional.empty();
            }

            var itemInBestOrientation = bestOrientation(originalItem, currentX, currentY);

            if (isHorizontalWrappingRequired(itemInBestOrientation, currentX)) {
                currentY += currentHeight;
                currentX      = 0;
                currentHeight = 0;
            }

            if (isNewBinRequired(itemInBestOrientation, currentY)) {
                bins.add(currentBin);
                currentBin    = new Bin2D(bounds.width(), bounds.height());
                currentX      = 0;
                currentY      = 0;
                currentHeight = 0;

                // Re-orient after resetting the current coordinates.
                itemInBestOrientation = bestOrientation(originalItem, currentX, currentY);
            }

            currentBin.add(new Rectangle(itemInBestOrientation.width(), itemInBestOrientation.height(), currentX, currentY));

            currentX += itemInBestOrientation.width();
            currentHeight = Math.max(currentHeight, itemInBestOrientation.height());
        }

        bins.add(currentBin);
        return Optional.of(new NonEmptyList<>(bins));
    }
}
