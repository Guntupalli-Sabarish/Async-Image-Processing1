package com.image.imageprocessing.processor;

import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.image.DrawMultipleImagesOnCanvas;
import com.image.imageprocessing.image.ImageData;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ImageProcessor {

    private ExecutorService executorService;
    private DrawMultipleImagesOnCanvas drawFn;

    public ImageProcessor(){
        executorService = Executors.newFixedThreadPool(100);
    }

    public void processImage(BufferedImage image, int num, ImageFilter imageFilter, DrawMultipleImagesOnCanvas drawFn){
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        
        // Calculate number of tiles, rounding up to cover all pixels
        int numHorizontalImages = (imageWidth + num - 1) / num;
        int numVerticalImages = (imageHeight + num - 1) / num;

        List<Future<ImageData>> futures = new ArrayList<>();

        for (int i = 0; i < numHorizontalImages; i++){
            for(int j = 0; j < numVerticalImages; j++){
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
                
                Future<ImageData> future = executorService.submit(new Callable<ImageData>() {
                    @Override
                    public ImageData call(){
                        BufferedImage result = imageFilter.filter(subImage);
                        return new ImageData(result, finalI * num, finalJ * num, finalTileWidth, finalTileHeight);
                    }
                });
                futures.add(future);
            }
        }

        for (Future<ImageData> future : futures) {
            try {
                drawFn.addImageToQueue(future.get());
            } catch (Exception ex) {
                System.err.println("Not able to push the image into the queue");
            }
        }

        shutdown();

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
