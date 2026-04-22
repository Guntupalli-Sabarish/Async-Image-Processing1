package com.image.imageprocessing.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageReadInf {

    BufferedImage readImage(Path src) throws IOException;

    void saveImage(BufferedImage image, Path dest) throws IOException;
}
