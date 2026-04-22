package com.image.imageprocessing.filter;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class InvertFilterTest {

    private final InvertFilter filter = new InvertFilter();

    @Test
    void whiteBecomesBlack() {
        BufferedImage src = singlePixel(255, 255, 255);
        BufferedImage out = filter.filter(src);
        assertEquals(0, out.getRGB(0, 0) & 0xFFFFFF, "255 inverted must be 0");
    }

    @Test
    void blackBecomesWhite() {
        BufferedImage src = singlePixel(0, 0, 0);
        BufferedImage out = filter.filter(src);
        assertEquals(0xFFFFFF, out.getRGB(0, 0) & 0xFFFFFF, "0 inverted must be 255");
    }

    @Test
    void channelsInvertedIndependently() {
        BufferedImage src = singlePixel(100, 150, 200);
        BufferedImage out = filter.filter(src);

        int rgb = out.getRGB(0, 0);
        assertEquals(155, (rgb >> 16) & 0xFF, "R: 255-100=155");
        assertEquals(105, (rgb >> 8)  & 0xFF, "G: 255-150=105");
        assertEquals(55,   rgb        & 0xFF, "B: 255-200=55");
    }

    @Test
    void preservesDimensions() {
        BufferedImage src = new BufferedImage(15, 25, BufferedImage.TYPE_INT_RGB);
        BufferedImage out = filter.filter(src);
        assertEquals(15, out.getWidth());
        assertEquals(25, out.getHeight());
    }

    private static BufferedImage singlePixel(int r, int g, int b) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, (r << 16) | (g << 8) | b);
        return img;
    }
}
