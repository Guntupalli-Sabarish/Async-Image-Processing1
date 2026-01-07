# Async Image Processing

A JavaFX application that processes images asynchronously using concurrent programming. The application splits images into small tiles, processes them in parallel using multiple threads, and displays the result progressively on a canvas.

## About

This project demonstrates concurrent image processing with JavaFX. It takes an input image, divides it into small tiles, applies a grayscale filter to each tile in parallel using a thread pool, and renders the results in real-time.

**Key Concepts:**
- Concurrent processing using Java ExecutorService
- JavaFX for GUI and canvas rendering
- Thread-safe rendering with producer-consumer pattern
- Modular design with filter interfaces

## Features

- Load images (JPG, PNG, BMP, GIF)
- Split images into 10×10 pixel tiles
- Process tiles in parallel (100 worker threads)
- Progressive real-time rendering
- Grayscale conversion filter

## Project Structure

```
src/main/java/com/image/imageprocessing/
├── HelloApplication.java          # Main JavaFX entry point
├── filter/
│   ├── ImageFilter.java          # Filter interface
│   └── GreyScaleFilter.java      # Grayscale implementation
├── image/
│   ├── ImageData.java            # Tile data model
│   └── DrawMultipleImagesOnCanvas.java  # Canvas renderer
├── io/
│   ├── ImageReadInf.java         # I/O interface
│   └── FileImageIO.java          # File reader
└── processor/
    └── ImageProcessor.java       # Concurrent processing engine
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Async-Image-Processing1
```

### 2. Build the Project

Using the Maven wrapper (recommended):
```bash
mvnw clean install
```

Or if you have Maven installed:
```bash
mvn clean install
```

### 3. Run the Application

Using Maven wrapper:
```bash
mvnw javafx:run
```

Or with Maven:
```bash
mvn javafx:run
```

### 4. Use the Application

1. When the application starts, a file chooser dialog will appear
2. Select an image file (JPG, PNG, BMP, or GIF)
3. Watch as the image is processed tile-by-tile and converted to grayscale
4. Close the window when finished

## How It Works

1. **Image Loading**: User selects an image via file dialog
2. **Tile Splitting**: Image is divided into 10×10 pixel tiles
3. **Parallel Processing**: Each tile is submitted to a thread pool of 100 workers
4. **Filter Application**: Grayscale filter is applied to each tile independently
5. **Progressive Rendering**: Processed tiles are displayed as they complete using JavaFX AnimationTimer
6. **Display**: Final grayscale image is rendered on the canvas

## Technologies Used

- **Java 17** - Core programming language with module system
- **JavaFX 17.0.6** - GUI framework for user interface and canvas
- **Maven** - Build tool and dependency management
- **ExecutorService** - Java concurrency for thread pool management
- **LinkedBlockingQueue** - Thread-safe queue for the producer-consumer pattern

## Contributing

Contributions are welcome! Here's how you can contribute to this project:

### Steps to Contribute

1. **Fork the repository**
   - Click the 'Fork' button on GitHub

2. **Clone your fork**
   ```bash
   git clone https://github.com/your-username/Async-Image-Processing1.git
   cd Async-Image-Processing1
   ```

3. **Create a new branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Make your changes**
   - Write clean, readable code
   - Follow existing code style
   - Test your changes

5. **Commit your changes**
   ```bash
   git add .
   git commit -m "Add: brief description of your changes"
   ```

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Go to the original repository on GitHub
   - Click "New Pull Request"
   - Select your fork and branch
   - Provide a clear description of your changes

### Contribution Ideas

Here are some ways you can contribute:

- **Add new image filters** (blur, sharpen, sepia, edge detection)
- **Implement save functionality** to export processed images
- **Improve performance** (optimize thread pool size, adaptive tiling)
- **Add UI controls** (progress bar, filter selection dropdown)
- **Write unit tests** for filters and core components
- **Improve error handling** and user feedback
- **Add documentation** and code comments
- **Fix bugs** reported in issues

### Code Guidelines

- Use meaningful variable and method names
- Follow Java naming conventions (camelCase for methods, PascalCase for classes)
- Add comments for complex logic
- Keep methods small and focused
- Handle exceptions appropriately

## License

This project is available for educational and personal use.

## Contact

For questions, suggestions, or issues, please open an issue on GitHub.

---

Made with Java and JavaFX ☕
