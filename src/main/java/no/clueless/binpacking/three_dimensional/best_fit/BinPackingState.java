package no.clueless.binpacking.three_dimensional.best_fit;

import no.clueless.binpacking.three_dimensional.shared.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

class BinPackingState {
    private final Bin3D          bin;
    private final List<Pocket3D> openPockets = new ArrayList<>();

    BinPackingState(Size3D bounds) {
        Objects.requireNonNull(bounds, "bounds cannot be null");
        bin = new Bin3D(UUID.randomUUID().toString(), bounds);
        openPockets.add(new Pocket3D(new Point3D(0, 0, 0), bounds, Pocket3D.PocketType.NEW_SHELF, 0, 0));
    }

    public List<Pocket3D> getOpenPockets() {
        return List.copyOf(openPockets);
    }

    public Bin3D getBin() {
        return bin;
    }

    void addToBin(Cuboid item) {
        Objects.requireNonNull(item, "item cannot be null");
        bin.add(item);
    }

    void removePocket(Pocket3D pocket) {
        Objects.requireNonNull(pocket, "pocket cannot be null");
        if (!openPockets.contains(pocket)) {
            throw new IllegalArgumentException("pocket must exist in openPockets");
        }
        openPockets.remove(pocket);
    }

    void spawnResidualPockets(Pocket3D pocket, Item3D item) {
        Objects.requireNonNull(pocket, "pocket must not be null");
        Objects.requireNonNull(item, "item cannot be null");

        double updatedRowDepth = Math.max(pocket.getLocalRowDepth(), item.getDepth());
        double updatedShelfHeight = Math.max(pocket.getLocalShelfHeight(), item.getHeight());

        var remainingRowWidth = pocket.getSize().getWidth() - item.getWidth();
        if (remainingRowWidth > 0) {
            var newPosition = pocket.getPosition().atX(pocket.getPosition().getX() + item.getWidth());
            var newSize     = new Size3D(remainingRowWidth, pocket.getSize().getHeight(), pocket.getSize().getDepth());
            openPockets.add(new Pocket3D(newPosition, newSize, Pocket3D.PocketType.CURRENT_ROW, updatedRowDepth, updatedShelfHeight));
        }

        if (pocket.getType() == Pocket3D.PocketType.NEW_SHELF || pocket.getType() == Pocket3D.PocketType.NEW_ROW) {
            double nextRowZ = pocket.getPosition().getZ() + updatedRowDepth;

            if (nextRowZ < bin.getBounds().getDepth()) {
                var newPosition = pocket.getPosition().atX(0).atZ(nextRowZ);
                var newWidth    = bin.getBounds().getWidth();
                var newHeight   = bin.getBounds().getHeight() - pocket.getPosition().getY();
                var newDepth    = bin.getBounds().getDepth() - nextRowZ;
                var newSize     = new Size3D(newWidth, newHeight, newDepth);
                openPockets.add(new Pocket3D(newPosition, newSize, Pocket3D.PocketType.NEW_ROW, 0.0, updatedShelfHeight));
            }
        }

        if (pocket.getType() == Pocket3D.PocketType.NEW_SHELF) {
            double nextShelfY = pocket.getPosition().getY() + updatedShelfHeight;

            if (nextShelfY < bin.getBounds().getHeight()) {
                var newPosition = pocket.getPosition().atX(0).atY(nextShelfY).atZ(0);
                var newWidth    = bin.getBounds().getWidth();
                var newHeight   = bin.getBounds().getHeight() - nextShelfY;
                var newDepth    = bin.getBounds().getDepth();
                var newSize     = new Size3D(newWidth, newHeight, newDepth);

                // Pristine shelf layer resets both local tracking dimensions back to zero
                openPockets.add(new Pocket3D(newPosition, newSize, Pocket3D.PocketType.NEW_SHELF, 0.0, 0.0));
            }
        }
    }
}
