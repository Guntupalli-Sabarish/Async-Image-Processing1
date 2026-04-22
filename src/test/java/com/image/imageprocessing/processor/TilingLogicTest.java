package com.image.imageprocessing.processor;

import com.image.imageprocessing.filter.GreyScaleFilter;
import com.image.imageprocessing.image.ImageData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TilingLogicTest {

    private final GreyScaleFilter filter = new GreyScaleFilter();

    @ParameterizedTest(name = "image={0}×{1}, tileSize={2}")
    @CsvSource({
            "100, 100, 10",
            "105, 103, 10",
            "1,   1,   10",
            "10,  10,  10",
            "7,   13,  4",
            "200, 150, 32",
    })
    void tilesCoverEntireImage(int imgW, int imgH, int tileSize) throws Exception {
        BufferedImage src = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        ImageProcessor proc = new ImageProcessor(2);

        List<ImageData> received = new CopyOnWriteArrayList<>();
        proc.processImage(src, tileSize, filter, received::add).get();

        long totalArea = received.stream()
                .mapToLong(d -> (long) d.tileWidth() * d.tileHeight())
                .sum();
        assertEquals((long) imgW * imgH, totalArea, "Tile areas must sum to image area");

        for (ImageData d : received) {
            assertTrue(d.tileX() >= 0 && d.tileX() < imgW,   "tileX out of bounds");
            assertTrue(d.tileY() >= 0 && d.tileY() < imgH,   "tileY out of bounds");
            assertTrue(d.tileWidth()  > 0,                    "tileWidth must be > 0");
            assertTrue(d.tileHeight() > 0,                    "tileHeight must be > 0");
            assertTrue(d.tileX() + d.tileWidth()  <= imgW,   "tile exceeds image width");
            assertTrue(d.tileY() + d.tileHeight() <= imgH,   "tile exceeds image height");
        }
    }

    @Test
    void singlePixelImageProducesOneTile() throws Exception {
        BufferedImage src = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ImageProcessor proc = new ImageProcessor(1);

        List<ImageData> received = new ArrayList<>();
        proc.processImage(src, 10, filter, received::add).get();

        assertEquals(1, received.size(), "1×1 image → exactly 1 tile");
        assertEquals(1, received.get(0).tileWidth());
        assertEquals(1, received.get(0).tileHeight());
    }
}
