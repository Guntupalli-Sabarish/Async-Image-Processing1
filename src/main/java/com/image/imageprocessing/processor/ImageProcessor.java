package com.image.imageprocessing.processor;

import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.image.DrawMultipleImagesOnCanvas;
import com.image.imageprocessing.image.ImageData;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageProcessor {

    private ExecutorService executorService;
    private DrawMultipleImagesOnCanvas drawFn;

    public ImageProcessor() {
        executorService = Executors.newFixedThreadPool(100);
    }

    public void processImage(BufferedImage image, int num, ImageFilter imageFilter, DrawMultipleImagesOnCanvas drawFn) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        // Calculate number of tiles, rounding up to cover all pixels
        int numHorizontalImages = (imageWidth + num - 1) / num;
        int numVerticalImages = (imageHeight + num - 1) / num;

        // SCATTER: Submit independent tasks to the Thread Pool
        for (int i = 0; i < numHorizontalImages; i++) {
            for (int j = 0; j < numVerticalImages; j++) {
                // Calculate actual tile dimensions (last tiles may be smaller)
                int x = i * num;
                int y = j * num;
                int tileWidth = Math.min(num, imageWidth - x);
                int tileHeight = Math.min(num, imageHeight - y);

                BufferedImage subImage = image.getSubimage(x, y, tileWidth, tileHeight);
                int finalI = i;
                int finalJ = j;
                int finalTileWidth = tileWidth;
                int finalTileHeight = tileHeight;

                executorService.submit(() -> {
                    try {
                        // PROCESSING
                        BufferedImage result = imageFilter.filter(subImage);
                        ImageData data = new ImageData(result, finalI * num, finalJ * num, finalTileWidth,
                                finalTileHeight);

                        // GATHER: Push the result directly to the Consumer Queue (Thread-Safe)
                        drawFn.addImageToQueue(data);
                    } catch (Exception e) {
                        System.err.println("Error processing tile: " + e.getMessage());
                    }
                });
            }
        }

        // NOTE: We do NOT call shutdown() here anymore.
        // We do NOT wait for futures.get().
        // This allows the UI thread to return immediately and stay responsive
        // while the background threads "scatter" and "gather" the results.
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("ExecutorService did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
