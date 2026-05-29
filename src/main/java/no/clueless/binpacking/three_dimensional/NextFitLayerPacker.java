package no.clueless.binpacking.three_dimensional;

import no.clueless.binpacking.shared.NonEmptyList;

import java.util.*;

public class NextFitLayerPacker {
    private final Size3D                  bounds;
    private final List<PackingStrategy3D> additionalHorizontalWrapStrategies;
    private final List<PackingStrategy3D> additionalDepthWrapStrategies;
    private final List<PackingStrategy3D> additionalNewBinStrategies;

    public NextFitLayerPacker(Size3D bounds, List<PackingStrategy3D> additionalHorizontalWrapStrategies, List<PackingStrategy3D> additionalDepthWrapStrategies, List<PackingStrategy3D> additionalNewBinStrategies) {
        this.bounds                             = Objects.requireNonNull(bounds, "bounds cannot be null");
        this.additionalHorizontalWrapStrategies = additionalHorizontalWrapStrategies == null ? new ArrayList<>() : new ArrayList<>(additionalHorizontalWrapStrategies);
        this.additionalDepthWrapStrategies      = additionalDepthWrapStrategies == null ? new ArrayList<>() : new ArrayList<>(additionalDepthWrapStrategies);
        this.additionalNewBinStrategies         = additionalNewBinStrategies == null ? new ArrayList<>() : new ArrayList<>(additionalNewBinStrategies);
    }

    @SuppressWarnings("unused")
    public NextFitLayerPacker(Size3D bounds) {
        this(bounds, null, null, null);
    }

    boolean isHorizontalWrapRequired(PlacementContext3D context) {
        Objects.requireNonNull(context, "context cannot be null");
        return context.getItem().getWidth() + context.getCurrentX() > bounds.getWidth() || additionalHorizontalWrapStrategies.stream().anyMatch(additionalHorizontalWrapStrategy -> additionalHorizontalWrapStrategy.isActionRequired(context));
    }

    boolean isDepthWrapRequired(PlacementContext3D context) {
        Objects.requireNonNull(context, "context cannot be null");
        return context.getItem().getDepth() + context.getCurrentZ() > bounds.getDepth() || additionalDepthWrapStrategies.stream().anyMatch(additionalDepthWrapStrategy -> additionalDepthWrapStrategy.isActionRequired(context));
    }

    boolean isNewBinRequired(PlacementContext3D context) {
        Objects.requireNonNull(context, "context cannot be null");
        return context.getItem().getHeight() + context.getCurrentY() > bounds.getHeight() || additionalNewBinStrategies.stream().anyMatch(additionalNewBinStrategy -> additionalNewBinStrategy.isActionRequired(context));
    }

    boolean willNeverFit(Item3D item) {
        return Objects.requireNonNull(item, "item cannot be null")
                .orientations()
                .stream()
                .noneMatch(o -> {
                    var context = new PlacementContext3D(o, new Bin3D(UUID.randomUUID().toString(), bounds), 0, 0, 0);
                    return !isHorizontalWrapRequired(context) && !isDepthWrapRequired(context) && !isNewBinRequired(context);
                });
    }

    Item3D bestOrientation(Item3D item, Bin3D currentBin, double currentX, double currentY, double currentZ) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(currentBin, "currentBin cannot be null");
        if (currentX < 0 || currentY < 0 || currentZ < 0) {
            throw new IllegalArgumentException("currentX, currentY and currentZ cannot be negative");
        }

        Item3D best = null;

        for (var orientation : item.orientations()) {
            var context = new PlacementContext3D(orientation, currentBin, currentX, currentY, currentZ);
            var fits    = !isHorizontalWrapRequired(context) && !isDepthWrapRequired(context) && !isNewBinRequired(context);

            if (fits) {
                if (best == null) {
                    best = orientation;
                } else {
                    if (orientation.getWidth() < best.getWidth()) {
                        best = orientation;
                    } else if (Double.compare(orientation.getWidth(), best.getWidth()) == 0 && orientation.getDepth() < best.getDepth()) {
                        best = orientation;
                    }
                }
            }
        }

        return best == null ? item.orientations().get(0) : best;
    }

    public Optional<NonEmptyList<Bin3D>> pack(NonEmptyList<Item3D> items) {
        items = Objects.requireNonNull(items, "items cannot be null").reverse();
        var bins               = new ArrayList<Bin3D>();
        var currentBin         = new Bin3D(UUID.randomUUID().toString(), bounds);
        var currentX           = 0.0d;
        var currentY           = 0.0d;
        var currentZ           = 0.0d;
        var currentRowDepth    = 0.0d;
        var currentLayerHeight = 0.0d;

        for (var originalItem : items) {
            if (willNeverFit(originalItem)) {
                return Optional.empty();
            }

            var itemInBestOrientation = bestOrientation(originalItem, currentBin, currentX, currentY, currentZ);

            //if (isHorizontalWrappingRequired(itemInBestOrientation, currentX)) {
            if (isHorizontalWrapRequired(new PlacementContext3D(itemInBestOrientation, currentBin, currentX, currentY, currentZ))) {
                currentZ += currentRowDepth;
                currentX        = 0;
                currentRowDepth = 0;

                itemInBestOrientation = bestOrientation(originalItem, currentBin, currentX, currentY, currentZ);
            }

            //if (isDepthWrappingRequired(itemInBestOrientation, currentZ)) {
            if (isDepthWrapRequired(new PlacementContext3D(itemInBestOrientation, currentBin, currentX, currentY, currentZ))) {
                currentY += currentLayerHeight;
                currentX           = 0;
                currentZ           = 0;
                currentLayerHeight = 0;
                currentRowDepth    = 0;

                itemInBestOrientation = bestOrientation(originalItem, currentBin, currentX, currentY, currentZ);
            }

            //if (isNewBinRequired(itemInBestOrientation, currentY)) {
            if (isNewBinRequired(new PlacementContext3D(itemInBestOrientation, currentBin, currentX, currentY, currentZ))) {
                bins.add(currentBin);
                currentBin         = new Bin3D(UUID.randomUUID().toString(), bounds);
                currentX           = 0;
                currentY           = 0;
                currentZ           = 0;
                currentLayerHeight = 0;
                currentRowDepth    = 0;

                itemInBestOrientation = bestOrientation(originalItem, currentBin, currentX, currentY, currentZ);
            }

            var cuboid = new Cuboid(itemInBestOrientation.getWidth(), itemInBestOrientation.getHeight(), itemInBestOrientation.getDepth(), currentX, currentY, currentZ);
            currentBin.add(cuboid);

            currentX += itemInBestOrientation.getWidth();
            currentRowDepth    = Math.max(currentRowDepth, itemInBestOrientation.getDepth());
            currentLayerHeight = Math.max(currentLayerHeight, itemInBestOrientation.getHeight());
        }

        bins.add(currentBin);
        return Optional.of(new NonEmptyList<>(bins));
    }
}
