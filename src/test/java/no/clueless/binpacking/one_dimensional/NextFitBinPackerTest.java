package no.clueless.binpacking.one_dimensional;

import no.clueless.binpacking.shared.NonEmptyList;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NextFitBinPackerTest {

    @Test
    void pack() {
        // arrange
        var sut   = new NextFitBinPacker(35.0);
        var items = Stream.of(new Item1D(10.0), new Item1D(20.0), new Item1D(35.0)).collect(NonEmptyList.collector());

        // act
        var actual = sut.pack(items).orElse(null);

        // assert
        assertNotNull(actual);
        assertEquals(2, actual.size(), actual::toString);
        assertEquals(0.0, actual.first().getRemainingCapacity(), actual::toString);
        assertEquals(5.0, actual.second().getRemainingCapacity(), actual::toString);
    }
}