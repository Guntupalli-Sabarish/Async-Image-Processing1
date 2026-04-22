module com.image.imageprocessing {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires java.desktop;
    requires java.logging;

    opens com.image.imageprocessing to javafx.fxml;

    exports com.image.imageprocessing;
    exports com.image.imageprocessing.filter;
    exports com.image.imageprocessing.image;
    exports com.image.imageprocessing.io;
    exports com.image.imageprocessing.processor;
}