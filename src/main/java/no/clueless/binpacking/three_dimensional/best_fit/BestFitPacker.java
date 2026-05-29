package no.clueless.binpacking.three_dimensional.best_fit;

import no.clueless.binpacking.shared.NonEmptyList;
import no.clueless.binpacking.three_dimensional.shared.*;

import java.util.*;
import java.util.stream.Collectors;

public class BestFitPacker {
    private final Size3D                  bounds;
    private final List<PackingStrategy3D> additionalNewBinStrategies;

    public BestFitPacker(Size3D bounds, List<PackingStrategy3D> additionalNewBinStrategies) {
        this.bounds                     = Objects.requireNonNull(bounds, "bounds must not be null");
        this.additionalNewBinStrategies = additionalNewBinStrategies == null ? new ArrayList<>() : additionalNewBinStrategies;
    }

    ScoredPlacement findBestFit(BinPackingState binPackingState, Item3D item) {
        Objects.requireNonNull(binPackingState, "binPackingState must not be null");
        Objects.requireNonNull(item, "item must not be null");

        ScoredPlacement bestPlacement = null;

        for (var pocket : binPackingState.getOpenPockets()) {
            for (var orientation : item.orientations()) {
                var context = new PlacementContext3D(orientation, binPackingState.getBin(), pocket.getPosition().getX(), pocket.getPosition().getY(), pocket.getPosition().getZ());
                if(additionalNewBinStrategies.stream().anyMatch(additionalNewBinStrategy -> additionalNewBinStrategy.isActionRequired(context))) {
                    continue;
                }

                if (orientation.getWidth() <= pocket.getSize().getWidth() && orientation.getHeight() <= pocket.getSize().getHeight() && orientation.getDepth() <= pocket.getSize().getDepth()) {
                    var challenger  = new ScoredPlacement(binPackingState, pocket, orientation);

                    if (bestPlacement == null || challenger.compareTo(bestPlacement) < 0) {
                        bestPlacement = challenger;
                    }
                }
            }
        }

        return bestPlacement;
    }

    public Optional<NonEmptyList<Bin3D>> pack(NonEmptyList<Item3D> items) {
        Objects.requireNonNull(items, "items must not be null");

        var sortedItems = items.stream()
                .sorted(Comparator.comparingDouble(Item3D::getHeight).reversed())
                .collect(Collectors.toList());
        var binPackingStates = new ArrayList<BinPackingState>();

        for (var item : sortedItems) {
            ScoredPlacement bestGlobalPlacement = null;

            for (var binPackingState : binPackingStates) {
                var bestLocalPlacement = findBestFit(binPackingState, item);
                if (bestLocalPlacement != null) {
                    if (bestGlobalPlacement == null || bestLocalPlacement.compareTo(bestGlobalPlacement) < 0) {
                        bestGlobalPlacement = bestLocalPlacement;
                    }
                }
            }

            if (bestGlobalPlacement == null) {
                var newBinPackingState = new BinPackingState(bounds);
                bestGlobalPlacement = findBestFit(newBinPackingState, item);

                if (bestGlobalPlacement == null) {
                    return Optional.empty();
                }

                binPackingStates.add(newBinPackingState);
            }

            var binPackingState = bestGlobalPlacement.getState();
            var bestPocket      = bestGlobalPlacement.getPocket();
            var bestOrientation = bestGlobalPlacement.getOrientation();
            var cuboid = new Cuboid(
                    bestOrientation.getWidth(),
                    bestOrientation.getHeight(),
                    bestOrientation.getDepth(),
                    bestPocket.getPosition().getX(),
                    bestPocket.getPosition().getY(),
                    bestPocket.getPosition().getZ()
            );

            binPackingState.addToBin(cuboid);
            binPackingState.removePocket(bestPocket);

            binPackingState.spawnResidualPockets(bestPocket, bestOrientation);
        }

        var resultingBins = binPackingStates.stream()
                .map(BinPackingState::getBin)
                .collect(NonEmptyList.collector());
        return Optional.of(resultingBins);
    }
}
