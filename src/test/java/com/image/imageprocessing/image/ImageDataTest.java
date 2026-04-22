package com.image.imageprocessing.image;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class ImageDataTest {

    private static final BufferedImage DUMMY =
            new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

    @Test
    void constructorSetsAllFields() {
        ImageData data = new ImageData(DUMMY, 10, 20, 5, 7);
        assertSame(DUMMY,  data.image());
        assertEquals(10,   data.tileX());
        assertEquals(20,   data.tileY());
        assertEquals(5,    data.tileWidth());
        assertEquals(7,    data.tileHeight());
    }

    @Test
    void equalityByValue() {
        ImageData a = new ImageData(DUMMY, 0, 0, 10, 10);
        ImageData b = new ImageData(DUMMY, 0, 0, 10, 10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void inequalityOnDifferentPosition() {
        ImageData a = new ImageData(DUMMY, 0, 0, 10, 10);
        ImageData b = new ImageData(DUMMY, 5, 0, 10, 10);
        assertNotEquals(a, b);
    }

    @Test
    void toStringContainsFieldValues() {
        ImageData data = new ImageData(DUMMY, 3, 7, 10, 10);
        String s = data.toString();
        assertTrue(s.contains("tileX"), "toString should mention field names");
        assertTrue(s.contains("3"),     "toString should contain tileX value");
        assertTrue(s.contains("7"),     "toString should contain tileY value");
    }
}
