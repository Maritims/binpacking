package no.clueless.binpacking.two_dimensional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {

    public static Stream<Arguments> extract_should_succeed() {
        return Stream.of(
                Arguments.of(
                        new Rectangle(10, 10, 0, 0),
                        new Rectangle(4, 4, 0, 0),
                        List.of(new Rectangle(6, 10, 4, 0), new Rectangle(10, 6, 0, 4))
                )
        );
    }

    @Test
    void x2_should_be_equal_to_x1_plus_width() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertEquals(1300, sut.x2());
    }

    @Test
    void y2_should_be_equal_to_y1_plus_height() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertEquals(1000, sut.y2());
    }

    @Test
    void fits_should_return_false_when_width_exceeds_rectangle() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertFalse(sut.fits(1300, 800));
    }

    @Test
    void fits_should_return_false_when_height_exceeds_rectangle() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertFalse(sut.fits(1200, 900));
    }

    @Test
    void fits_should_return_true_when_width_and_height_fits_perfectly() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertTrue(sut.fits(1200, 800));
    }

    @Test
    void fits_should_return_true_when_width_does_not_exceed_rectangle() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertTrue(sut.fits(1100, 800));
    }

    @Test
    void fits_should_return_true_when_height_does_not_exceed_rectangle() {
        var sut = new Rectangle(1200, 800, 100, 200);
        assertTrue(sut.fits(1200, 700));
    }

    @Test
    void contains_should_return_false_when_x1_is_greater_than_other() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        var other     = new Rectangle(10, 10, 10, 0);
        assertFalse(rectangle.contains(other));
    }

    @Test
    void contains_should_return_false_when_x2_is_less_than_other() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        var other     = new Rectangle(20, 10, 0, 0);
        assertFalse(rectangle.contains(other));
    }

    @Test
    void contains_should_return_false_when_y1_is_greater_than_other() {
        var rectangle = new Rectangle(10, 10, 0, 10);
        var other     = new Rectangle(10, 20, 0, 0);
        assertFalse(rectangle.contains(other));
    }

    @Test
    void contains_should_return_false_when_y2_is_less_than_other() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        var other     = new Rectangle(10, 20, 0, 0);
        assertFalse(rectangle.contains(other));
    }

    @Test
    void contains_should_return_true_when_rectangle_fully_contains_other() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        var other     = new Rectangle(9, 9, 0, 0);
        assertTrue(rectangle.contains(other));
    }

    @Test
    void contains_should_return_true_when_rectangle_perfectly_overlaps_other() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        assertTrue(rectangle.contains(rectangle));
    }

    @Test
    void intersects_should_return_false_when_rectangles_do_not_intersect_along_their_base() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        var other     = new Rectangle(10, 10, 10, 0);
        assertFalse(rectangle.intersects(other), "Rectangle intersects other");
        assertFalse(other.intersects(rectangle), "Other intersects rectangle. This is not possible since the previous assertion passed.");
    }

    @Test
    void intersects_should_return_false_when_rectangles_do_not_intersect_along_their_height() {
        var rectangle = new Rectangle(10, 10, 0, 0);
        var other     = new Rectangle(10, 10, 0, 10);
        assertFalse(rectangle.intersects(other));
        assertFalse(other.intersects(rectangle), "Other intersects rectangle. This is not possible since the previous assertion passed.");
    }

    @ParameterizedTest
    @MethodSource
    void extract_should_succeed(Rectangle rectangle, Rectangle other, List<Rectangle> expected) {
        var actual = rectangle.extract(other);
        assertEquals(expected, actual);
    }
}