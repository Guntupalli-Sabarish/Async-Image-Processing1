package com.image.imageprocessing.processor;

import com.image.imageprocessing.filter.GreyScaleFilter;
import com.image.imageprocessing.image.ImageData;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessorTest {

    private final GreyScaleFilter filter = new GreyScaleFilter();

    @Test
    void allTilesProcessed() throws Exception {
        int w = 50, h = 40, tileSize = 10;
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        List<ImageData> results = new CopyOnWriteArrayList<>();
        ImageProcessor proc = new ImageProcessor(4);

        proc.processImage(src, tileSize, filter, results::add)
                .get(5, TimeUnit.SECONDS);

        int expectedTiles = ((w + tileSize - 1) / tileSize) * ((h + tileSize - 1) / tileSize);
        assertEquals(expectedTiles, results.size(),
                "Expected exactly " + expectedTiles + " tiles");
    }

    @Test
    void progressReaches100Percent() throws Exception {
        AtomicReference<Double> lastProgress = new AtomicReference<>(0.0);
        ImageProcessor proc = new ImageProcessor(2);
        proc.setProgressListener(lastProgress::set);

        BufferedImage src = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        proc.processImage(src, 10, filter, d -> {})
                .get(10, TimeUnit.SECONDS);

        assertEquals(1.0, lastProgress.get(), 0.001,
                "Progress must reach 1.0 after all tiles complete");
    }

    @Test
    void shutdownCancelsCompletionFuture() throws Exception {
        BufferedImage src = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        ImageProcessor proc = new ImageProcessor(2);

        var future = proc.processImage(src, 5, filter, d -> {
            try { Thread.sleep(1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        proc.shutdown();

        assertTrue(future.isDone() || future.isCancelled() ||
                future.get(5, TimeUnit.SECONDS) == null,
                "Future should be resolved after shutdown");
    }

    @Test
    void reprocessCancelsPreviousJob() throws Exception {
        BufferedImage src = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        ImageProcessor proc = new ImageProcessor(2);

        proc.processImage(src, 10, filter, d -> {});

        List<ImageData> secondResults = new CopyOnWriteArrayList<>();
        proc.processImage(src, 10, filter, secondResults::add)
                .get(5, TimeUnit.SECONDS);

        assertFalse(secondResults.isEmpty(), "Second job must produce results");
    }
}
