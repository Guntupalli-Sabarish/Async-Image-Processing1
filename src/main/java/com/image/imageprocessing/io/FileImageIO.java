package com.image.imageprocessing.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class FileImageIO implements ImageReadInf {

    private static final Logger LOG = Logger.getLogger(FileImageIO.class.getName());

    @Override
    public BufferedImage readImage(Path src) throws IOException {
        if (!Files.exists(src)) {
            throw new IOException("File not found: " + src);
        }
        BufferedImage image = ImageIO.read(src.toFile());
        if (image == null) {
            throw new IOException(
                    "Unsupported image format or corrupt file: " + src.getFileName());
        }
        LOG.info("Loaded image: " + src.getFileName()
                + " (" + image.getWidth() + "×" + image.getHeight() + ")");
        return image;
    }

    @Override
    public void saveImage(BufferedImage image, Path dest) throws IOException {
        Files.createDirectories(dest.getParent());
        boolean written = ImageIO.write(image, "PNG", dest.toFile());
        if (!written) {
            throw new IOException("No suitable PNG writer found – image not saved: " + dest);
        }
        LOG.info("Saved processed image to: " + dest);
    }
}
