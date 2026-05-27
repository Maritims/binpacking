package no.clueless.binpacking.three_dimensional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bin3D {
    private final String       id;
    private final Size3D       bounds;
    private final List<Cuboid> items = new ArrayList<>();

    public Bin3D(String id, Size3D bounds) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be null or blank");
        }
        this.id     = id;
        this.bounds = Objects.requireNonNull(bounds, "bounds cannot be null");
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public Size3D getBounds() {
        return bounds;
    }

    public List<Cuboid> getItems() {
        return items;
    }

    public void add(Cuboid item) {
        Objects.requireNonNull(item, "item cannot be null");
        items.add(item);
    }

    @Override
    public String toString() {
        return "Bin3D{" +
                "id=" + id +
                ",bounds=" + bounds +
                ",items=" + items +
                '}';
    }
}
