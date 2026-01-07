package com.image.imageprocessing;

import com.image.imageprocessing.filter.GreyScaleFilter;
import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.image.DrawMultipleImagesOnCanvas;
import com.image.imageprocessing.io.FileImageIO;
import com.image.imageprocessing.io.ImageReadInf;
import com.image.imageprocessing.processor.ImageProcessor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HelloApplication extends Application {
    private ImageProcessor processor;

    @Override
    public void start(Stage stage) throws IOException {
        ImageReadInf imageIO = new FileImageIO();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"),
                new FileChooser.ExtensionFilter("JPG Images", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG Images", "*.png"),
                new FileChooser.ExtensionFilter("BMP Images", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF Images", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No File Selected");
            alert.setHeaderText(null);
            alert.setContentText("No image file was selected. Application will exit.");
            alert.showAndWait();
            Platform.exit();
            return;
        }

        BufferedImage image = imageIO.readImage(selectedFile.getAbsolutePath());

        if (image == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Image Load Error");
            alert.setHeaderText("Failed to Load Image");
            alert.setContentText("The selected file could not be loaded. Please ensure it is a valid image file.");
            alert.showAndWait();
            Platform.exit();
            return;
        }

        DrawMultipleImagesOnCanvas drawMultipleImagesOnCanvas = DrawMultipleImagesOnCanvas.getInstance();
        drawMultipleImagesOnCanvas.initialize(stage, image.getWidth(), image.getHeight());

        stage.setOnCloseRequest(event -> {
            if (processor != null) {
                processor.shutdown();
            }
            Platform.exit();
        });
        
        processor = new ImageProcessor();
        ImageFilter imageFilter = new GreyScaleFilter();
        processor.processImage(image, 10, imageFilter, drawMultipleImagesOnCanvas);
    }

    public static void main(String[] args) {
        launch();
    }
}