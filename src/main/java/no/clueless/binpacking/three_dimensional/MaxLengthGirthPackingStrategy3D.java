package no.clueless.binpacking.three_dimensional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy dictates that a new bin must be allocated when adding an item to the current bin would result in breaching either the max length or max length + girth.
 * <p>An example of a freight product with such a constraint is <a href="https://www.bring.no/tjenester/pakker-og-gods/bedrifter-nasjonalt/pakke-til-bedrift">Pakke til bedrift</a> by Bring, which supports max length of 240 cm, and a max length + girth of 360 cm.</p>
 */
public class MaxLengthGirthPackingStrategy3D implements PackingStrategy3D {
    private static final Logger log = LoggerFactory.getLogger(MaxLengthGirthPackingStrategy3D.class);
    private final        double maxLength;
    private final        double maxLengthAndGirth;

    public MaxLengthGirthPackingStrategy3D(double maxLength, double maxLengthAndGirth) {
        if (maxLength <= 0 || maxLengthAndGirth <= 0) {
            throw new IllegalArgumentException("maxLength and maxLengthAndGirth must be positive");
        }
        this.maxLength         = maxLength;
        this.maxLengthAndGirth = maxLengthAndGirth;
    }

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
        var lengthBreached            = simulatedLongestDimension > maxLength;
        var lengthGirthBreached       = (simulatedLongestDimension + simulatedGirth) > maxLengthAndGirth;

        if (lengthBreached || lengthGirthBreached) {
            log.info("Shipping regulations breached! Forcing allocation of a new bin.");
            return true;
        }

        return false;
    }
}
