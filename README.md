# Async Image Processor (Java)

A JavaFX-based image processing project. This README explains the repository layout, prerequisites, and how to build and run the application on Windows (PowerShell) using the included Maven wrapper.

## Repository structure

- `pom.xml` - Maven build descriptor and plugin configuration (uses javafx-maven-plugin).
- `mvnw`, `mvnw.cmd` - Maven wrapper scripts (use these so you don't need a system-wide Maven installation).
- `.mvn/` - Maven wrapper metadata.
- `src/main/java/` - Java source code; main package: `com.image.imageprocessing`.
  - `HelloApplication.java` - JavaFX application entry point.
  - `filter/` - image filter interfaces/implementations (e.g., `GreyScaleFilter.java`, `ImageFilter.java`).
  - `image/` - image helpers (`DrawMultipleImagesOnCanvas.java`, `ImageData.java`).
  - `io/` - image IO helpers (`FileImageIO.java`, `ImageReadInf.java`, and sample `test.jpg`).
  - `processor/` - core image processing logic (`ImageProcessor.java`).
- `.gitignore` - files excluded from Git.

## Prerequisites

- Java JDK 24 (project `pom.xml` is configured to compile with `source`/`target` 24). Install a matching JDK and ensure `JAVA_HOME` points to it.
- Git (for cloning and normal source control operations).
- On Windows, use PowerShell (examples below use PowerShell).

Note: The project declares JavaFX dependencies in `pom.xml` and uses the `javafx-maven-plugin`; you normally don't need to install a separate JavaFX SDK. If you run into module/runtime errors, ensure your JDK and plugin versions are compatible.

## Build & Run (Windows PowerShell)

Open PowerShell at the project root (the folder that contains `mvnw.cmd` and `pom.xml`) and run:

```powershell
# Make sure you're in the project directory
Set-Location -Path 

# Use the Maven wrapper to run the JavaFX application
.\mvnw.cmd clean javafx:run
```

Alternatively, if you have Maven installed globally:

```powershell
# mvn clean javafx:run
```

What this does:
- `clean` removes previous build artifacts.
- `javafx:run` is provided by the `javafx-maven-plugin` (configured in `pom.xml`) and launches the JavaFX application using the `mainClass` configured.

## Build a distributable

The `javafx-maven-plugin` can also produce jlink images or packaged applications. Check the plugin docs and `pom.xml` settings. A simple package command (may require extra configuration for platform-specific packaging):

```powershell
.\mvnw.cmd package
```

## Common issues / troubleshooting

- "JavaFX runtime components are missing": make sure you use a JDK compatible with JavaFX version declared in `pom.xml` (JDK 17+, but this project compiles with Java 24; plugin handles modules). If necessary, install a matching JavaFX SDK or adjust plugin/dependencies.
- `JAVA_HOME` wrong or `java` version mismatch: verify with `java -version` and ensure `JAVA_HOME` points to the JDK used by your system.
- Permission issues running `mvnw.cmd`: on Windows PowerShell you may need to run with the execution policy that allows running scripts, or simply run the `mvnw.cmd` file directly as shown above.

## Development notes

- If you import the project into an IDE (IntelliJ IDEA, Eclipse with Maven support), import it as a Maven project. Ensure the IDE uses the same JDK as the project (Java 24).
- Code entry point is `com.image.imageprocessing.HelloApplication` (configured in the `javafx-maven-plugin` in `pom.xml`).

## Contributing

If you want to add features/tests or fix issues:

1. Fork or clone the repo.
2. Create a feature branch: `git checkout -b feature/my-feature`.
3. Make changes and run `./mvnw.cmd clean javafx:run` to test.
4. Commit and push, then open a Pull Request.

---

If you want, I can also add a short `README` badge, a simple `LICENSE`, or create a GitHub Actions workflow to run tests/compile on push. Tell me which you'd like next.
