package no.clueless.binpacking.one_dimensional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bin1D {
    private final double       maxCapacity;
    private       double       remainingCapacity;
    private final List<Item1D> items = new ArrayList<>();

    public Bin1D(double maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be positive");
        }
        this.maxCapacity       = maxCapacity;
        this.remainingCapacity = maxCapacity;
    }

    public double getRemainingCapacity() {
        return remainingCapacity;
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    @Override
    public String toString() {
        return "Bin1D{" +
                "capacity=" + maxCapacity +
                ", placedItems=" + items +
                ", remainingCapacity=" + remainingCapacity +
                '}';
    }

    public void add(Item1D item) {
        Objects.requireNonNull(item, "item cannot be null");
        remainingCapacity -= item.getBounds();
        items.add(item);
    }
}
