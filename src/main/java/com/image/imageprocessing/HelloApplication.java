package com.image.imageprocessing;

import com.image.imageprocessing.filter.GreyScaleFilter;
import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.filter.InvertFilter;
import com.image.imageprocessing.filter.SepiaFilter;
import com.image.imageprocessing.image.DrawMultipleImagesOnCanvas;
import com.image.imageprocessing.io.FileImageIO;
import com.image.imageprocessing.io.ImageReadInf;
import com.image.imageprocessing.processor.ImageProcessor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {

    private static final Logger LOG = Logger.getLogger(HelloApplication.class.getName());

    private final ImageReadInf imageIO = new FileImageIO();
    private ImageProcessor processor;
    private DrawMultipleImagesOnCanvas renderer;

    private BufferedImage currentImage;

    private ComboBox<String> filterCombo;
    private Slider tileSlider;
    private Label tileValueLabel;
    private Spinner<Integer> threadSpinner;
    private Button saveBtn;
    private ProgressBar progressBar;
    private Label statusLabel;
    private Pane canvasContainer;
    private ScrollPane scrollPane;
    private FileChooser fileChooser;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();

        VBox topPanel = new VBox(buildToolbar(), buildProgressRow());
        root.setTop(topPanel);

        canvasContainer = new StackPane();
        canvasContainer.setStyle("-fx-background-color: #1a1a2e;");

        Label placeholder = new Label("Open an image to start processing");
        placeholder.setStyle("-fx-text-fill: #a8b2d8; -fx-font-size: 16px;");
        canvasContainer.getChildren().add(placeholder);

        scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("Async Image Processor");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> shutdown());
        stage.show();

        openImage();
    }

    @Override
    public void stop() {
        shutdown();
    }

    private HBox buildToolbar() {
        Button openBtn = new Button("📂  Open Image");
        openBtn.setId("open-btn");
        openBtn.setOnAction(e -> openImage());

        Label filterLabel = new Label("Filter:");
        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("Grayscale", "Sepia", "Invert");
        filterCombo.setValue("Grayscale");
        filterCombo.setOnAction(e -> reprocessIfLoaded());

        Label tileSizeLabel = new Label("Tile Size:");
        tileSlider = new Slider(4, 64, 10);
        tileSlider.setMajorTickUnit(20);
        tileSlider.setShowTickMarks(false);
        tileSlider.setPrefWidth(100);
        tileValueLabel = new Label("10 px");
        tileSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int v = newVal.intValue();
            tileValueLabel.setText(v + " px");
        });
        tileSlider.setOnMouseReleased(e -> reprocessIfLoaded());

        int defaultThreads = Math.min(Runtime.getRuntime().availableProcessors() * 2, 32);
        Label threadsLabel = new Label("Threads:");
        SpinnerValueFactory<Integer> svf =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 32, defaultThreads);
        threadSpinner = new Spinner<>(svf);
        threadSpinner.setPrefWidth(60);
        threadSpinner.valueProperty().addListener((obs, o, n) -> reprocessIfLoaded());

        saveBtn = new Button("💾  Save Image");
        saveBtn.setId("save-btn");
        saveBtn.setDisable(true);
        saveBtn.setOnAction(e -> saveImage());

        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);

        HBox toolbar = new HBox(10,
                openBtn, sep1,
                filterLabel, filterCombo, sep2,
                tileSizeLabel, tileSlider, tileValueLabel,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                threadsLabel, threadSpinner,
                new Region(),
                saveBtn);
        HBox.setHgrow(toolbar.getChildren().get(toolbar.getChildren().size() - 2), Priority.ALWAYS);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(10, 16, 10, 16));
        toolbar.setId("toolbar");
        return toolbar;
    }

    private HBox buildProgressRow() {
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        statusLabel = new Label("No image loaded.");
        statusLabel.setId("status-label");

        HBox row = new HBox(12, progressBar, statusLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 16, 6, 16));
        row.setId("progressBar-row");
        return row;
    }

    private void openImage() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images",
                            "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"),
                    new FileChooser.ExtensionFilter("PNG",  "*.png"),
                    new FileChooser.ExtensionFilter("BMP",  "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF",  "*.gif"));
        }

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) return;

        try {
            BufferedImage image = imageIO.readImage(file.toPath());
            currentImage = image;
            startProcessing(image);
        } catch (IOException ex) {
            showError("Cannot Load Image",
                    "The file could not be read as an image.\n\nDetail: " + ex.getMessage(),
                    ex);
        }
    }

    private void startProcessing(BufferedImage image) {
        if (renderer != null) renderer.stop();
        if (processor != null) processor.shutdown();

        double vpW   = Math.max(scrollPane.getWidth()  - 4, 100);
        double vpH   = Math.max(scrollPane.getHeight() - 4, 100);
        double scale = Math.min(1.0, Math.min(vpW / image.getWidth(),
                                              vpH / image.getHeight()));
        int canvasW  = (int) Math.ceil(image.getWidth()  * scale);
        int canvasH  = (int) Math.ceil(image.getHeight() * scale);

        Canvas canvas = new Canvas(canvasW, canvasH);
        renderer = new DrawMultipleImagesOnCanvas(canvas, scale);

        canvasContainer.getChildren().setAll(canvas);
        renderer.start();

        int tileSize = (int) tileSlider.getValue();
        int threads  = threadSpinner.getValue();
        ImageFilter filter = buildFilter(filterCombo.getValue());

        processor = new ImageProcessor(threads);

        int total = computeTileCount(image.getWidth(), image.getHeight(), tileSize);
        statusLabel.setText("Processing…  0 / " + total + " tiles");
        progressBar.setProgress(0);
        saveBtn.setDisable(true);

        processor.setProgressListener(progress -> Platform.runLater(() -> {
            progressBar.setProgress(progress);
            int done = (int) Math.round(progress * total);
            statusLabel.setText(String.format("Processing…  %d / %d tiles  (%.0f%%)",
                    done, total, progress * 100));
        }));

        processor.processImage(image, tileSize, filter, renderer::addImageToQueue)
                .thenRunAsync(() -> Platform.runLater(() -> {
                    progressBar.setProgress(1.0);
                    statusLabel.setText("✔  Done – " + total + " tiles processed.");
                    saveBtn.setDisable(false);
                    LOG.info("Processing complete.");
                }));
    }

    private void reprocessIfLoaded() {
        if (currentImage != null) startProcessing(currentImage);
    }

    private void saveImage() {
        if (currentImage == null) return;

        FileChooser saveDlg = new FileChooser();
        saveDlg.setTitle("Save Processed Image");
        saveDlg.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        saveDlg.setInitialFileName("processed.png");

        File dest = saveDlg.showSaveDialog(primaryStage);
        if (dest == null) return;

        ImageFilter filter = buildFilter(filterCombo.getValue());
        new Thread(() -> {
            try {
                BufferedImage processed = filter.filter(currentImage);
                imageIO.saveImage(processed, dest.toPath());
                Platform.runLater(() ->
                        statusLabel.setText("✔  Saved to: " + dest.getName()));
            } catch (IOException ex) {
                Platform.runLater(() ->
                        showError("Save Failed",
                                "Could not write the image to:\n" + dest.getAbsolutePath()
                                + "\n\nDetail: " + ex.getMessage(), ex));
            }
        }, "save-thread").start();
    }

    private void shutdown() {
        if (renderer  != null) renderer.stop();
        if (processor != null) processor.shutdown();
        Platform.exit();
    }

    private static ImageFilter buildFilter(String name) {
        return switch (name) {
            case "Sepia"  -> new SepiaFilter();
            case "Invert" -> new InvertFilter();
            default       -> new GreyScaleFilter();
        };
    }

    private static int computeTileCount(int w, int h, int tileSize) {
        int cols = (w + tileSize - 1) / tileSize;
        int rows = (h + tileSize - 1) / tileSize;
        return cols * rows;
    }

    private void showError(String title, String message, Throwable cause) {
        LOG.log(Level.SEVERE, message, cause);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}