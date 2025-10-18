

# Async-Image-Processing1

An **asynchronous, high-performance Java/JavaFX image-processing application** designed for scalable pixel-level operations on ultra-high-resolution images using concurrency and parallel pipeline architectures.

## Features

- **Concurrent processing:** Uses thread pools and futures for block-wise parallel image transformation.
- **Modular design:** Easily add new filters, IO methods, and processing stages.
- **Pixel-level operations:** Handles RGB-to-grayscale conversion and image splitting for distributed processing.
- **JavaFX visualization:** Displays processed results in a responsive JavaFX GUI.

## Project Structure

```
Async-Image-Processing1/
│
├── pom.xml                       # Maven build file
├── README.md                     # Project documentation
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── image/
│                   └── imageprocessing/
│                       ├── filter/
│                       │   ├── ImageFilter.java          # Filter interface
│                       │   └── GreyScaleFilter.java      # Grayscale filter implementation
│                       ├── image/
│                       │   ├── ImageData.java            # Data model for images
│                       │   └── DrawMultipleImagesOnCanvas.java  # Canvas drawing helper
│                       ├── io/
│                       │   ├── ImageReadInf.java         # Image IO interface
│                       │   ├── FileImageIO.java          # File IO implementation
│                       │   └── test.jpg                  # Sample image
│                       ├── processor/
│                       │   └── ImageProcessor.java       # Core processing class using concurrency
│                       ├── HelloApplication.java         # JavaFX app entry point
│                       └── module-info.java              # Java module configuration
```

## Core Components

- **Filter Module:** `ImageFilter` (interface) and `GreyScaleFilter` provide pluggable transformation logic.
- **Image Module:** `ImageData` represents blocks of images; `DrawMultipleImagesOnCanvas` displays processed segments.
- **IO Module:** `ImageReadInf` and `FileImageIO` manage reading images from disk; supports extension for new sources.
- **Processor Module:** `ImageProcessor` splits images into blocks, submits tasks to a thread pool, applies filters, and merges results asynchronously.
- **Application Entry:** `HelloApplication` launches JavaFX UI for user interaction and result visualization.
- **Config Files:** `pom.xml` provides dependencies; `module-info.java` handles Java module requirements.

## How It Works

1. **Load an image** (e.g., `test.jpg`) using the IO module.
2. **Split the image** into blocks of size N × N using `ImageProcessor`.
3. **Process blocks concurrently**: Each block is filtered in parallel via the defined filter(s).
4. **Visualize results** by enqueueing processed blocks and displaying them using JavaFX canvas.

## Getting Started

1. Clone the repo and import as a Maven project.
2. Run `HelloApplication.java` using JavaFX-compatible IDE.
3. Extend `ImageFilter` for custom processing effects.

## Example Usage

```java
ImageProcessor processor = new ImageProcessor();
processor.processImage(image, blockSize, new GreyScaleFilter(), canvasDrawer);
```

## Extending the Project

- Add new filters by implementing `ImageFilter`.
- Support alternative IO by implementing `ImageReadInf`.
- Refine concurrency by adjusting thread pool size in `ImageProcessor`.

## License

MIT

***

Feel free to copy and adapt this `README.md` as needed for your repository!

[1](https://github.com/chinmayaisaisabarish/Async-Image-Processing1/tree/main/src/main)
[2](https://github.com/chinmayaisaisabarish/Async-Image-Processing1/blob/main/src/main/java/com/image/imageprocessing/processor/ImageProcessor.java)
