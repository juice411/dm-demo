package com.dtxy.dm.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImgUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ImgUtil.class);

    public static void binaryImage(String imgSrc, String imgDest, String format) throws IOException {

        //File file = new File(System.getProperty("user.dir") + "/src/2722425974762424026.jpg");
        File file = new File(imgSrc);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY  
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
                LOG.debug("原值{}二值{}", rgb, grayImage.getRGB(i, j));
            }
        }

        File newFile = new File(imgDest);
        //ImageIO.write(grayImage, "jpg", newFile);
        ImageIO.write(grayImage, format, newFile);
    }

    public static void grayImage(String imgSrc, String imgDest, String format) throws IOException {

        File file = new File(imgSrc);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY  
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
                LOG.debug("原值{}灰度{}", rgb, grayImage.getRGB(i, j));
            }
        }

        File newFile = new File(imgDest);
        ImageIO.write(grayImage, format, newFile);
    }

    public static void main(String[] args) throws IOException {

        LOG.debug("user.dir={}", System.getProperty("user.dir"));

        String imgSrc = System.getProperty("user.dir") + "/data/t10k-images/g.bmp";
        String imgDest = System.getProperty("user.dir") + "/data/t10k-images/test1.bmp";
        String format = "bmp";

        //ImgUtil.grayImage(imgSrc, imgDest, format);
        ImgUtil.binaryImage(imgDest, imgDest, format);
    }
}
