package com.image.imageprocessing.processor;

import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.image.ImageData;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageProcessor {

    private static final Logger LOG = Logger.getLogger(ImageProcessor.class.getName());

    private static final int MAX_THREADS = 32;
    private static final int QUEUE_CAPACITY = 4000;

    private final int threadCount;
    private ThreadPoolExecutor executor;

    private final AtomicInteger completedTiles = new AtomicInteger(0);
    private volatile int totalTiles = 0;
    private volatile boolean cancelled = false;
    private volatile CompletableFuture<Void> completionFuture;

    private Consumer<Double> progressListener;

    public ImageProcessor() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    public ImageProcessor(int threadCount) {
        this.threadCount = Math.max(1, Math.min(threadCount, MAX_THREADS));
        createExecutor();
    }

    public void setProgressListener(Consumer<Double> listener) {
        this.progressListener = listener;
    }

    public int getTotalTiles() {
        return totalTiles;
    }

    public int getCompletedTiles() {
        return completedTiles.get();
    }

    public CompletableFuture<Void> processImage(BufferedImage image,
                                                int tileSize,
                                                ImageFilter filter,
                                                Consumer<ImageData> tileConsumer) {
        cancelAndReset();

        int imageWidth  = image.getWidth();
        int imageHeight = image.getHeight();
        int numCols     = (imageWidth  + tileSize - 1) / tileSize;
        int numRows     = (imageHeight + tileSize - 1) / tileSize;

        totalTiles = numCols * numRows;
        completedTiles.set(0);
        cancelled = false;
        completionFuture = new CompletableFuture<>();

        if (totalTiles == 0) {
            completionFuture.complete(null);
            return completionFuture;
        }

        LOG.info(String.format("Processing %d×%d image as %d tiles (%d worker threads)",
                imageWidth, imageHeight, totalTiles, threadCount));

        for (int col = 0; col < numCols; col++) {
            for (int row = 0; row < numRows; row++) {
                final int tileX      = col * tileSize;
                final int tileY      = row * tileSize;
                final int tileWidth  = Math.min(tileSize, imageWidth  - tileX);
                final int tileHeight = Math.min(tileSize, imageHeight - tileY);

                executor.submit(() -> {
                    if (cancelled || Thread.currentThread().isInterrupted()) {
                        notifyTileComplete();
                        return;
                    }
                    try {
                        BufferedImage sub    = image.getSubimage(tileX, tileY, tileWidth, tileHeight);
                        BufferedImage result = filter.filter(sub);
                        tileConsumer.accept(new ImageData(result, tileX, tileY, tileWidth, tileHeight));
                    } catch (Exception ex) {
                        LOG.log(Level.WARNING,
                                String.format("Tile (%d,%d) failed: %s", tileX, tileY, ex.getMessage()));
                    } finally {
                        notifyTileComplete();
                    }
                });
            }
        }
        return completionFuture;
    }

    public void shutdown() {
        cancelled = true;
        executor.shutdownNow();
        try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (completionFuture != null) {
            completionFuture.cancel(true);
        }
        LOG.info("ImageProcessor shut down.");
    }

    private void createExecutor() {
        executor = new ThreadPoolExecutor(
                threadCount, threadCount,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private void cancelAndReset() {
        if (executor != null && !executor.isShutdown()) {
            cancelled = true;
            executor.shutdownNow();
            try {
                executor.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        createExecutor();
    }

    private void notifyTileComplete() {
        int done = completedTiles.incrementAndGet();
        if (progressListener != null) {
            progressListener.accept((double) done / totalTiles);
        }
        if (done >= totalTiles && completionFuture != null) {
            completionFuture.complete(null);
        }
    }
}
