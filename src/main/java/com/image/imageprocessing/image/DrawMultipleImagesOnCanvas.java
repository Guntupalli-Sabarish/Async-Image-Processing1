package com.image.imageprocessing.image;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class DrawMultipleImagesOnCanvas {

    private static final Logger LOG =
            Logger.getLogger(DrawMultipleImagesOnCanvas.class.getName());

    private static final int MAX_TILES_PER_FRAME = 20;
    private static final int QUEUE_CAPACITY = 8000;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final LinkedBlockingQueue<ImageData> queue =
            new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    private final double scale;

    private AnimationTimer timer;

    public DrawMultipleImagesOnCanvas(Canvas canvas, double scale) {
        this.canvas = canvas;
        this.gc     = canvas.getGraphicsContext2D();
        this.scale  = scale;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void addImageToQueue(ImageData data) {
        if (!queue.offer(data)) {
            LOG.warning(String.format(
                    "Render queue full – dropping tile at (%d, %d)",
                    data.tileX(), data.tileY()));
        }
    }

    public void start() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int drawn = 0;
                ImageData data;
                while (drawn < MAX_TILES_PER_FRAME && (data = queue.poll()) != null) {
                    gc.drawImage(
                            SwingFXUtils.toFXImage(data.image(), null),
                            data.tileX()      * scale,
                            data.tileY()      * scale,
                            data.tileWidth()  * scale,
                            data.tileHeight() * scale);
                    drawn++;
                }
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        queue.clear();
    }
}
