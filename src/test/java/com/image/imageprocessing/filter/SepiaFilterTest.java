package com.image.imageprocessing.filter;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class SepiaFilterTest {

    private final SepiaFilter filter = new SepiaFilter();

    @Test
    void whiteProducesWarmSepiaWithClampedRAndG() {
        BufferedImage src = singlePixel(255, 255, 255);
        BufferedImage out = filter.filter(src);

        int rgb = out.getRGB(0, 0);
        int r   = (rgb >> 16) & 0xFF;
        int g   = (rgb >> 8)  & 0xFF;
        int b   =  rgb        & 0xFF;

        assertEquals(255, r, "R should be clamped to 255 for white input");
        assertEquals(255, g, "G should be clamped to 255 for white input");
        int expectedB = Math.min(255, (int)(255 * (0.272 + 0.534 + 0.131)));
        assertEquals(expectedB, b, 1, "B channel: sepia formula for white input");
        assertTrue(r >= g, "Sepia: R >= G (warm tone)");
        assertTrue(g >= b, "Sepia: G >= B (warm tone)");
    }

    @Test
    void blackStaysBlack() {
        BufferedImage src = singlePixel(0, 0, 0);
        BufferedImage out = filter.filter(src);
        assertEquals(0, out.getRGB(0, 0) & 0xFFFFFF);
    }

    @Test
    void correctSepiaMatrixOnGrey() {
        BufferedImage src = singlePixel(128, 128, 128);
        BufferedImage out = filter.filter(src);

        int rgb = out.getRGB(0, 0);
        int r   = (rgb >> 16) & 0xFF;
        int g   = (rgb >> 8)  & 0xFF;
        int b   =  rgb        & 0xFF;

        int expR = Math.min(255, (int) (128 * 0.393 + 128 * 0.769 + 128 * 0.189));
        int expG = Math.min(255, (int) (128 * 0.349 + 128 * 0.686 + 128 * 0.168));
        int expB = Math.min(255, (int) (128 * 0.272 + 128 * 0.534 + 128 * 0.131));

        assertEquals(expR, r, 1, "Sepia R off");
        assertEquals(expG, g, 1, "Sepia G off");
        assertEquals(expB, b, 1, "Sepia B off");
    }

    @Test
    void preservesDimensions() {
        BufferedImage src = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        BufferedImage out = filter.filter(src);
        assertEquals(40, out.getWidth());
        assertEquals(30, out.getHeight());
    }

    private static BufferedImage singlePixel(int r, int g, int b) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, (r << 16) | (g << 8) | b);
        return img;
    }
}
