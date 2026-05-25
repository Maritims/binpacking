package no.clueless.binpacking.two_dimensional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Item2DTest {
    @Test
    void compareTo_should_compare_areas() {
        // arrange
        var expected = IntStream.rangeClosed(1, 5).mapToObj(i -> new Item2D(i, i)).collect(Collectors.toList());
        var randomized = new ArrayList<>(expected);
        Collections.shuffle(randomized);
        assertNotEquals(expected, randomized);

        // act
        Collections.sort(randomized);

        // assert
        assertEquals(expected, randomized);
    }
}
