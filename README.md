#ParallelPix

A JavaFX desktop application that processes images asynchronously using concurrent
programming. Images are split into tiles, filtered in parallel on an adaptive thread
pool, and rendered progressively on a canvas — tile by tile in real-time.

---

## Features

| Feature | Details |
|---|---|
| **Filters** | Grayscale, Sepia, Invert — selectable at runtime |
| **Tile size** | Configurable 4 – 64 px slider (default 10 px) |
| **Thread count** | Adaptive default (`CPUs × 2`, capped at 32); adjustable spinner |
| **Progressive rendering** | Up to 20 tiles drawn per animation frame (~60 fps) |
| **Progress indicator** | Progress bar + tile counter (`done / total`) |
| **Open / Save** | File chooser for input; saves processed image as PNG |
| **Cancellation** | Opening a new image or closing the window cleanly cancels in-flight tasks |
| **Back-pressure** | Bounded work queue with `CallerRunsPolicy` prevents memory blow-up on large images |

---

## Prerequisites

| Requirement | Version |
|---|---|
| **Java** | **21** (LTS) – OpenJDK or Oracle JDK |
| **Maven** | 3.9 + (must be installed and on PATH) |
| **JavaFX** | 21.0.3 – downloaded automatically by Maven |
| **JAVA_HOME** | Must be set to your JDK 21 path (e.g. `C:\Program Files\Java\jdk-21`) |

> [!NOTE]
> The project uses the Java module system (`module-info.java`). Running with
> Java 17 is **not** supported because the build targets `--release 21`.

---

## Getting Started

### Clone

```bash
git clone <repository-url>
cd Async-Image-Processing1-1
```

### Build

```bash
mvn clean install
```

### Run

```bash
mvn javafx:run
```

### Run Tests

```bash
mvn test
```

---

## Project Structure

```
src/
├── main/java/com/image/imageprocessing/
│   ├── HelloApplication.java          # JavaFX entry point + UI layout
│   ├── filter/
│   │   ├── ImageFilter.java           # Filter interface
│   │   ├── GreyScaleFilter.java       # BT.709 luminosity grayscale
│   │   ├── SepiaFilter.java           # Sepia matrix transform
│   │   └── InvertFilter.java          # Channel inversion
│   ├── image/
│   │   ├── ImageData.java             # Immutable record (tile position + pixels)
│   │   └── DrawMultipleImagesOnCanvas.java  # AnimationTimer renderer
│   ├── io/
│   │   ├── ImageReadInf.java          # I/O interface (readImage / saveImage)
│   │   └── FileImageIO.java           # File-system implementation
│   └── processor/
│       └── ImageProcessor.java        # Adaptive thread pool, tiling, progress
├── main/resources/com/image/imageprocessing/
│   └── styles.css                     # Dark UI theme
└── test/java/com/image/imageprocessing/
    ├── filter/  GreyScaleFilterTest, SepiaFilterTest, InvertFilterTest
    ├── image/   ImageDataTest
    ├── io/      FileImageIOTest
    └── processor/ TilingLogicTest, ImageProcessorTest
```

---

## Architecture

```
┌─────────────────────────────────────────────┐
│             HelloApplication (JavaFX UI)     │
│  Controls: filter, tile size, thread count  │
│  Progress bar + Open / Save buttons         │
└────────┬─────────────────────────┬──────────┘
         │ readImage(Path)         │ addImageToQueue(ImageData)
         ▼                         ▼
  ┌────────────┐          ┌──────────────────────────┐
  │ FileImageIO│          │ DrawMultipleImagesOnCanvas│
  └────────────┘          │  AnimationTimer (20/frame)│
                          └──────────────────────────┘
         │                          ▲
         │ processImage(...)        │ Consumer<ImageData>
         ▼                         │
  ┌──────────────────────────────────────────┐
  │           ImageProcessor                 │
  │  ThreadPoolExecutor (adaptive, max 32)   │
  │  ArrayBlockingQueue (4 000, CallerRuns)  │
  │  AtomicInteger progress + CompletableFuture│
  └──────────┬───────────────────────────────┘
             │ filter.filter(subImage)
             ▼
  ┌──────────────────┐
  │  ImageFilter impl│  (GreyScaleFilter / SepiaFilter / InvertFilter)
  └──────────────────┘
```

---

## How to Add a New Filter

1. **Create the class** in `src/main/java/com/image/imageprocessing/filter/`:

   ```java
   package com.image.imageprocessing.filter;

   import java.awt.image.BufferedImage;

   public class BlurFilter implements ImageFilter {
       @Override
       public BufferedImage filter(BufferedImage original) {
           // … your per-pixel logic …
           return result;
       }

       @Override
       public String toString() { return "Blur"; }
   }
   ```

2. **Register it** in `HelloApplication.buildFilter()`:

   ```java
   case "Blur" -> new BlurFilter();
   ```

3. **Add it to the combo-box** in `HelloApplication.buildToolbar()`:

   ```java
   filterCombo.getItems().addAll("Grayscale", "Sepia", "Invert", "Blur");
   ```

4. **Write a test** in `src/test/java/…/filter/BlurFilterTest.java`.

---

## Running Tests

```bash
mvnw.cmd test        # Windows
./mvnw test          # Linux / macOS
```

Expected output:

```
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```



---

## Technologies

| Library | Version | Purpose |
|---|---|---|
| Java | 21 | Language + module system |
| JavaFX | 21.0.3 | GUI, Canvas, AnimationTimer |
| `javax.imageio` | JDK built-in | Image read / write |
| `java.util.concurrent` | JDK built-in | ThreadPoolExecutor, CompletableFuture |
| `java.util.logging` | JDK built-in | Structured logging |
| JUnit Jupiter | 5.10.2 | Unit tests |

---

## Contributing

1. Fork → branch (`git checkout -b feature/my-filter`) → commit → PR.
2. Follow the **How to Add a New Filter** guide above.
3. All tests must pass (`mvnw test`) before opening a PR.
4. Use `java.util.logging.Logger` — do not add `System.out/err` calls.

---

Made with Java and JavaFX ☕
