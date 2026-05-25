package no.clueless.binpacking.one_dimensional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class Item1DTest {

    @Test
    void compareTo_should_compare_bounds() {
        // arrange
        var expected   = IntStream.rangeClosed(1, 5).mapToDouble(i -> i).mapToObj(Item1D::new).collect(Collectors.toList());
        var randomized = new ArrayList<>(expected);
        Collections.shuffle(randomized);
        assertNotEquals(expected, randomized);

        // act
        Collections.sort(randomized);

        // assert
        assertEquals(expected, randomized);
    }
}