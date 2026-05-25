package no.clueless.binpacking.three_dimensional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxLengthGirthPackingStrategy3D implements PackingStrategy3D {
    private static final Logger log = LoggerFactory.getLogger(MaxLengthGirthPackingStrategy3D.class);

    @Override
    public boolean isActionRequired(PlacementContext3D context) {
        var currentBin = context.getCurrentBin();
        var minX       = context.getCurrentX();
        var minY       = context.getCurrentY();
        var minZ       = context.getCurrentZ();
        var maxX       = context.getCurrentX() + context.getItem().getWidth();
        var maxY       = context.getCurrentY() + context.getItem().getHeight();
        var maxZ       = context.getCurrentZ() + context.getItem().getDepth();

        for (var cuboid : currentBin.getItems()) {
            minX = Math.min(minX, cuboid.getX());
            minY = Math.min(minY, cuboid.getY());
            minZ = Math.min(minZ, cuboid.getZ());
            maxX = Math.max(maxX, cuboid.x2());
            maxY = Math.max(maxY, cuboid.y2());
            maxZ = Math.max(maxZ, cuboid.z2());
        }

        var simulatedWidth            = maxX - minY;
        var simulatedHeight           = maxY - minY;
        var simulatedDepth            = maxZ - minZ;
        var simulatedSize             = new Size3D(simulatedWidth, simulatedHeight, simulatedDepth);
        var simulatedLongestDimension = simulatedSize.getLongestDimension();
        var simulatedGirth            = simulatedSize.getGirth();
        var lengthBreached            = simulatedLongestDimension > 240.0d;
        var lengthGirthBreached       = (simulatedLongestDimension + simulatedGirth) > 360.0d;

        if (lengthBreached || lengthGirthBreached) {
            log.info("Shipping regulations breached! Forcing allocation of a new bin.");
            return true;
        }

        return false;
    }
}
