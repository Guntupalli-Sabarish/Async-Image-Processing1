package com.image.imageprocessing.filter;

import java.awt.image.BufferedImage;

public class InvertFilter implements ImageFilter {

    @Override
    public BufferedImage filter(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = original.getRGB(x, y);
                int r   = 255 - ((rgb >> 16) & 0xFF);
                int g   = 255 - ((rgb >> 8)  & 0xFF);
                int b   = 255 - ( rgb        & 0xFF);
                result.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Invert";
    }
}
