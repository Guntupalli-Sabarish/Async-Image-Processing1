package com.image.imageprocessing.image;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DrawMultipleImagesOnCanvas {

    private static DrawMultipleImagesOnCanvas instance;
    private Queue<ImageData> queue = new LinkedBlockingQueue<>();
    private GraphicsContext gc;

    public static DrawMultipleImagesOnCanvas getInstance(){
        if(instance == null){
            instance = new DrawMultipleImagesOnCanvas();
        }
        return instance;
    }

    public void addImageToQueue(ImageData image){
        queue.offer(image);
    }

    public void initialize(Stage primaryStage, int width, int height){
        Canvas canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        this.gc.clearRect(0, 0, width, height);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                ImageData imageData = queue.poll();
                if(imageData != null){
                    gc.drawImage(SwingFXUtils.toFXImage(imageData.getImage(), null),
                            imageData.getI(), imageData.getJ(), 
                            imageData.getX(), imageData.getY());
                    System.out.println("Drawing using thread " + Thread.currentThread().getName());
                    System.out.println(String.format("Drawing image at i: %s, j: %s", 
                            imageData.getI(), imageData.getJ()));
                }
            }
        }.start();

        StackPane stack = new StackPane(canvas);
        Scene scene = new Scene(stack, width, height);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Async Image Processing");
        primaryStage.show();
    }
}
