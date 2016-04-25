package com.dtxy.dm.test;

import java.io.IOException;
import java.util.Random;

import com.dtxy.dm.BPN;

public class Number {

    public static void main(String[] args) {

        BPN bpn = new BPN(32, 15, 4, 0.25);//测试数字时的最佳参数，准确率达到99%

        //训练学习得到权值并保存
        /*double right = 0;
        while (right < 0.98) {
            right = learn(bpn);
            System.out.println("准确率：" + right);
        }
        bpn.saveMatrix(System.getProperty("user.dir") + "/data/MatrixOfNumber.txt");
        */
        //批量验证准确率
        bpn.loadMatrix(System.getProperty("user.dir") + "/data/MatrixOfNumber.txt");//从文件加载权值     
        double right = testRight(bpn, 100d);
        System.out.println("------------------------" + right);
        //单独验证
        //testOneNumber(bpn);
    }

    public static void testOneNumber(BPN bpn) {

        while (true) {
            System.out.println("请输入一个int型数字可以带符号\n");
            byte[] input = new byte[10];
            try {
                System.in.read(input);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Integer value = Integer.parseInt(new String(input).trim());
            int rawVal = value;
            double[] binary = new double[32];
            int index = 31;
            do {
                binary[index--] = (value & 1);
                value >>>= 1;
            } while (value != 0);

            double[] result = bpn.test(binary);

            double max = -Integer.MIN_VALUE;
            int idx = -1;

            for (int i = 0; i != result.length; i++) {
                if (result[i] > max) {
                    max = result[i];
                    idx = i;
                }
            }

            switch (idx) {
                case 0:
                    System.out.format("%d是一个正奇数\n", rawVal);
                    break;
                case 1:
                    System.out.format("%d是一个正偶数\n", rawVal);
                    break;
                case 2:
                    System.out.format("%d是一个负奇数\n", rawVal);
                    break;
                case 3:
                    System.out.format("%d是一个负偶数\n", rawVal);
                    break;
            }
        }
    }

    public static double learn(BPN bpn) {

        Random random = new Random();
        //List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i != 10000; i++) {
            int value = random.nextInt();
            double[] real = new double[4];
            if (value >= 0)
                if ((value & 1) == 1)
                    real[0] = 1;
                else
                    real[1] = 1;
            else if ((value & 1) == 1)
                real[2] = 1;
            else
                real[3] = 1;
            double[] binary = new double[32];
            int index = 31;
            do {
                binary[index--] = (value & 1);
                value >>>= 1;
            } while (value != 0);

            bpn.train(binary, real);
        }

        System.out.println("训练完毕，开始测试准确率！。");
        return testRight(bpn, 1000d);
    }

    public static double testRight(BPN bpn, double num) {

        Random random = new Random();
        double right = 0;
        for (int i = 0; i < num; i++) {
            int value = random.nextInt();
            double[] real = new double[4];
            if (value >= 0)
                if ((value & 1) == 1)
                    real[0] = 1;
                else
                    real[1] = 1;
            else if ((value & 1) == 1)
                real[2] = 1;
            else
                real[3] = 1;

            double[] binary = new double[32];
            int index = 31;
            do {
                binary[index--] = (value & 1);
                value >>>= 1;
            } while (value != 0);

            double[] result = bpn.test(binary);

            double max = -Integer.MIN_VALUE;
            int idx = -1;

            for (int j = 0; j != result.length; j++) {
                if (result[j] > max) {
                    max = result[j];
                    idx = j;
                }
            }

            for (int j = 0; j != real.length; j++) {
                //System.out.println(real + "==" + real[j]);
                if (real[j] == 1.0) {
                    //System.out.println(idx + "==" + j);
                    if (idx == j) {
                        right++;
                    }
                    break;
                }
            }
            //System.out.println("------------------------" + right);
        }

        return (right / num);
    }

}
