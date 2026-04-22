package com.image.imageprocessing.filter;

import java.awt.image.BufferedImage;

public class SepiaFilter implements ImageFilter {

    @Override
    public BufferedImage filter(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = original.getRGB(x, y);
                int r   = (rgb >> 16) & 0xFF;
                int g   = (rgb >> 8)  & 0xFF;
                int b   =  rgb        & 0xFF;

                int outR = clamp((int) (r * 0.393 + g * 0.769 + b * 0.189));
                int outG = clamp((int) (r * 0.349 + g * 0.686 + b * 0.168));
                int outB = clamp((int) (r * 0.272 + g * 0.534 + b * 0.131));

                result.setRGB(x, y, (outR << 16) | (outG << 8) | outB);
            }
        }
        return result;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    @Override
    public String toString() {
        return "Sepia";
    }
}
