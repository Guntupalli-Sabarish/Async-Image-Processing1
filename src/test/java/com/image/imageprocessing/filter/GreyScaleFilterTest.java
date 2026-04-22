package com.image.imageprocessing.filter;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class GreyScaleFilterTest {

    private final GreyScaleFilter filter = new GreyScaleFilter();

    @Test
    void pureWhiteStaysWhite() {
        BufferedImage src = singlePixel(255, 255, 255);
        BufferedImage out = filter.filter(src);

        int rgb = out.getRGB(0, 0) & 0xFFFFFF;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8)  & 0xFF;
        int b =  rgb        & 0xFF;
        assertEquals(r, g, "White: R==G");
        assertEquals(g, b, "White: G==B");
        assertTrue(r >= 254, "White pixel should stay ~255, got " + r);
    }

    @Test
    void pureBlackStaysBlack() {
        BufferedImage src = singlePixel(0, 0, 0);
        BufferedImage out = filter.filter(src);

        int rgb = out.getRGB(0, 0) & 0xFFFFFF;
        assertEquals(0, rgb, "Black pixel must remain 0");
    }

    @Test
    void correctLuminosityFormula() {
        BufferedImage src = singlePixel(100, 150, 200);
        BufferedImage out = filter.filter(src);

        int gray = (out.getRGB(0, 0) >> 16) & 0xFF;
        int expected = (int) (0.2126 * 100 + 0.7152 * 150 + 0.0722 * 200);
        assertEquals(expected, gray, 1, "Luminosity value off: expected ~" + expected);
    }

    @Test
    void preservesDimensions() {
        BufferedImage src = new BufferedImage(30, 20, BufferedImage.TYPE_INT_RGB);
        BufferedImage out = filter.filter(src);
        assertEquals(30, out.getWidth());
        assertEquals(20, out.getHeight());
    }

    @Test
    void toStringIsReadable() {
        assertFalse(filter.toString().isBlank());
    }

    private static BufferedImage singlePixel(int r, int g, int b) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, (r << 16) | (g << 8) | b);
        return img;
    }
}
