package compressor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageCompressor {
    public void compressImage(File inputFile, File outputFile, float quality)
            throws IOException {
        BufferedImage inputImage = ImageIO.read(inputFile);

        // Mendapatkan lebar dan tinggi gambar
        int originalWidth = inputImage.getWidth();
        int originalHeight = inputImage.getHeight();

        String formatName = getFileExtension(inputFile);

        if (formatName.equals("png")) {
            originalWidth = (int)(originalWidth * quality);
            originalHeight = (int)(originalHeight * quality);
        }

        // Mengubah ukuran gambar ke dimensi yang baru
        BufferedImage outputImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, originalWidth, originalHeight, null);
        g2d.dispose();

        // Mengatur kualitas gambar terkompresi
        if (formatName.equals("png")) {
            ImageIO.write(outputImage, "png", outputFile);
        } else if (formatName.equals("jpg")) {
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                ImageOutputStream ios = ImageIO.createImageOutputStream(fos);

                ImageWriter writer = ImageIO.getImageWritersByFormatName(formatName).next();
                writer.setOutput(ios);

                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(quality);

                writer.write(null, new IIOImage(outputImage, null, null), writeParam);
                writer.dispose();
            }
        } else {
            throw new IllegalArgumentException("Format gambar " + formatName + " tidak didukung. Gunakan PNG atau JPEG.");
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
            return fileName.substring(lastIndex + 1).toLowerCase();
        }

        return "";
    }
}