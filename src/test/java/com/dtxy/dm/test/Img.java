package com.dtxy.dm.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtxy.dm.BPN;
import com.dtxy.dm.util.FileUtil;
import com.dtxy.dm.util.Util;

public class Img {
    private static final Logger LOG = LoggerFactory.getLogger(Img.class);

    private static Pattern p = Pattern.compile("(.*?)(\\d?)_\\d");

    public static void main(String[] args) throws IOException {

        BPN bpn = new BPN(28 * 28, 16 * 16, 10, 0.05);//图像为28*28像素，求每一行白色像素的数目

        //train(bpn);

        String path = System.getProperty("user.dir") + "/data/t10k-images/t2.bmp";
        bpn.loadMatrix(System.getProperty("user.dir") + "/data/MatrixOfImg.txt");
        testOne(bpn, path);

    }

    public static void train(BPN bpn) throws IOException {

        StringBuffer name = new StringBuffer();
        String path = System.getProperty("user.dir") + "/data/t10k-images/";

        List<String> filenames = new ArrayList<String>();
        FileUtil.listFiles(path, filenames);

        for (int epoch = 0; epoch < 50; epoch++) {//迭代次数
            Collections.shuffle(filenames);//打散数据集
            for (String fileName : filenames) {
                File file = new File(fileName);
                BufferedImage image = ImageIO.read(file);
                int width = image.getWidth();
                int height = image.getHeight();

                double[] inputs = new double[width * height];
                double targets[] = new double[10];

                int k = 0;
                BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
                for (int x = 0; x < width; x++) {
                    int count = 0;
                    for (int y = 0; y < height; y++) {
                        int rgb = image.getRGB(x, y);
                        binaryImage.setRGB(x, y, rgb);
                        rgb = binaryImage.getRGB(x, y);
                        if (rgb == -1) {
                            //count++;
                            inputs[k++] = 0.9;
                        } else {
                            inputs[k++] = 0.1;
                        }
                    }

                    //inputs[x] = count;
                }

                int index = getNumByFileName(fileName);

                targets[index] = 1d;
                //bpn.tramnmx(inputs);
                bpn.train(inputs, targets);

            }

            LOG.debug("第{}次迭代训练！", epoch);

            double right = test(bpn);

            if (right > 0.87) {
                bpn.saveMatrix(System.getProperty("user.dir") + "/data/MatrixOfImg.txt");
            }

            LOG.debug("准确率={}", right);
        }

        LOG.debug("开始测试准确率！");

        /*double right = test(bpn);

        if (right > 0.98) {
            bpn.saveMatrix(System.getProperty("user.dir") + "/data/MatrixOfImg.txt");
        }

        LOG.debug("准确率={}", right);*/
    }

    public static double test(BPN bpn) throws IOException {

        double right = 0d;
        StringBuffer name = new StringBuffer();
        String path = System.getProperty("user.dir") + "/data/t10k-images/";

        List<String> filenames = new ArrayList<String>();
        FileUtil.listFiles(path, filenames);

        Collections.shuffle(filenames);//打散数据集
        for (String fileName : filenames) {
            File file = new File(fileName);
            BufferedImage image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();

            double[] inputs = new double[width * height];
            double[] outputValues = null;
            double targets[] = new double[10];

            int k = 0;
            BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            for (int x = 0; x < width; x++) {
                int count = 0;
                for (int y = 0; y < height; y++) {
                    int rgb = image.getRGB(x, y);
                    binaryImage.setRGB(x, y, rgb);
                    rgb = binaryImage.getRGB(x, y);
                    if (rgb == -1) {
                        //count++;
                        inputs[k++] = 0.9;
                    } else {
                        inputs[k++] = 0.1;
                    }
                }

                //inputs[x] = count;
            }

            int num = getNumByFileName(fileName);
            //bpn.tramnmx(inputs);
            outputValues = bpn.test(inputs);

            int idx = Util.getIndexOfMax(outputValues);

            if (idx == num) {
                right++;
            }

        }

        return (right / filenames.size());
    }

    private static int getNumByFileName(String fileName) {

        // E:\workspace2\dm-demo\data\t10k-images\9_977.bmp
        int num = -1;

        Matcher m = p.matcher(fileName);
        while (m.find()) {
            num = Integer.parseInt(m.group(2));
        }

        return num;
    }

    private static void testOne(BPN bpn, String path) throws IOException {

        File file = new File(path);

        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();

        double[] inputs = new double[width * height];
        double[] outputValues = null;

        int k = 0;
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < width; x++) {
            int count = 0;
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                binaryImage.setRGB(x, y, rgb);
                rgb = binaryImage.getRGB(x, y);
                if (rgb == -1) {
                    //count++;
                    inputs[k++] = 0.9;
                } else {
                    inputs[k++] = 0.1;
                }
            }

            //inputs[x] = count;
        }

        //bpn.tramnmx(inputs);
        outputValues = bpn.test(inputs);

        int idx = Util.getIndexOfMax(outputValues);

        LOG.debug("识别结果为：{}", idx);

    }

}
