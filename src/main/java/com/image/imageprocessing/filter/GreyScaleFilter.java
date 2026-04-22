package com.image.imageprocessing.filter;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class GreyScaleFilter implements ImageFilter {

    private static final Logger LOG = Logger.getLogger(GreyScaleFilter.class.getName());

    @Override
    public BufferedImage filter(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb  = original.getRGB(x, y);
                int r    = (rgb >> 16) & 0xFF;
                int g    = (rgb >> 8)  & 0xFF;
                int b    =  rgb        & 0xFF;
                int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                int grayRgb = (gray << 16) | (gray << 8) | gray;
                result.setRGB(x, y, grayRgb);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Grayscale";
    }
}
