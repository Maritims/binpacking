package no.clueless.binpacking.three_dimensional;

@FunctionalInterface
public interface PackingStrategy3D {
    boolean isActionRequired(PlacementContext3D context);
}
