package com.image.imageprocessing.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileImageIOTest {

    private final FileImageIO io = new FileImageIO();

    @Test
    void saveAndReloadProducesEquivalentImage(@TempDir Path tmp) throws IOException {
        BufferedImage original = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        original.setRGB(0, 0, 0xFF0000);
        original.setRGB(1, 1, 0x00FF00);
        original.setRGB(2, 2, 0x0000FF);

        Path dest = tmp.resolve("roundtrip.png");
        io.saveImage(original, dest);

        BufferedImage loaded = io.readImage(dest);
        assertEquals(original.getWidth(),  loaded.getWidth(),  "width mismatch");
        assertEquals(original.getHeight(), loaded.getHeight(), "height mismatch");
        assertEquals(original.getRGB(0, 0), loaded.getRGB(0, 0), "pixel (0,0) mismatch");
        assertEquals(original.getRGB(1, 1), loaded.getRGB(1, 1), "pixel (1,1) mismatch");
        assertEquals(original.getRGB(2, 2), loaded.getRGB(2, 2), "pixel (2,2) mismatch");
    }

    @Test
    void readMissingFileThrowsIOException(@TempDir Path tmp) {
        Path missing = tmp.resolve("does_not_exist.png");
        assertThrows(IOException.class, () -> io.readImage(missing),
                "Expected IOException for missing file");
    }

    @Test
    void saveCreatesParentDirectories(@TempDir Path tmp) throws IOException {
        Path nested = tmp.resolve("a/b/c/image.png");
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        assertDoesNotThrow(() -> io.saveImage(img, nested));
        assertTrue(nested.toFile().exists(), "Saved file must exist");
    }
}
