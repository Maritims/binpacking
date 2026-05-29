package no.clueless.binpacking.three_dimensional.shared;

@FunctionalInterface
public interface PackingStrategy3D {
    boolean isActionRequired(PlacementContext3D context);
}
