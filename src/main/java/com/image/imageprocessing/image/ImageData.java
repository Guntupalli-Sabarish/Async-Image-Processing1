package com.image.imageprocessing.image;

import java.awt.image.BufferedImage;

public record ImageData(
        BufferedImage image,
        int tileX,
        int tileY,
        int tileWidth,
        int tileHeight) {
}
