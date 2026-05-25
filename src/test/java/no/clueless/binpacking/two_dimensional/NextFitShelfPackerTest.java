package no.clueless.binpacking.two_dimensional;

import no.clueless.binpacking.shared.NonEmptyList;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NextFitShelfPackerTest {

    @Test
    void pack() {
        // arrange
        var sut   = new NextFitShelfPacker(new Size2D(10, 10));
        var items = Stream.of(
                new Item2D(4, 4),
                new Item2D(3, 5),
                new Item2D(5, 2)
        ).collect(NonEmptyList.collector());

        // act
        var actual = sut.pack(items).map(NonEmptyList::first).orElse(null);

        // assert
        assertNotNull(actual);
        assertEquals(3, actual.getItems().size());
        assertEquals(new Rectangle(4, 4, 0, 0), actual.getItems().get(0), actual::toString);
        assertEquals(new Rectangle(3, 5, 4, 0), actual.getItems().get(1), actual::toString);
        assertEquals(new Rectangle(2, 5, 7, 0), actual.getItems().get(2), actual::toString);
    }
}
