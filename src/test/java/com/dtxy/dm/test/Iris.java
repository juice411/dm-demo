package com.dtxy.dm.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtxy.dm.BPN;
import com.dtxy.dm.util.Util;

public class Iris {
    private static final Logger LOG = LoggerFactory.getLogger(Iris.class);

    public static void main(String[] args) {

        BPN bpn = new BPN(4, 12, 3, 0.25);

        /*List<double[]> datas = getDataList(System.getProperty("user.dir") + "/data/train.txt");

        for (int j = 0; j < 1000; j++) {
            for (double[] data : datas) {

                double inputValues[] = new double[4];
                double targets[] = new double[3];

                System.arraycopy(data, 0, inputValues, 0, data.length - 1);
                bpn.tramnmx(inputValues);

                for (int i = 0; i < targets.length; i++) {
                    if (data[data.length - 1] == i) {
                        targets[i] = 1d;
                    }

                }

                bpn.train(inputValues, targets);

            }
            System.out.format("第%d次迭代训练\n", j);
        }

        System.out.println("训练完毕，开始测试准确率！。");

        System.out.format("正确率：%s\n", test(bpn));

        if (test(bpn) > 0.96) {
            bpn.saveMatrix(System.getProperty("user.dir") + "/data/MatrixOfIris.txt");
        }*/

        //验证
        bpn.loadMatrix(System.getProperty("user.dir") + "/data/MatrixOfIris.txt");
        try {
            testAndOut2File(bpn);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void testAndOut2File(BPN bpn) throws IOException {

        List<double[]> datas = getDataList(System.getProperty("user.dir") + "/data/iris.txt");

        BufferedWriter output = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + "/data/iris_result.txt")));

        double right = 0d;
        for (double[] data : datas) {

            double inputValues[] = new double[4];
            double outputValues[] = null;
            double targets[] = new double[1];

            System.arraycopy(data, 0, inputValues, 0, data.length - 1);
            bpn.tramnmx(inputValues);

            /*for (int i = 0; i < targets.length; i++) {
                if (data[data.length - 1] == i) {
                    targets[i] = 1d;
                }
                System.out.format("index=%s\n", data[data.length - 1]);
                System.out.format("%s\n", targets[i]);
            }*/

            System.arraycopy(data, data.length - 1, targets, 0, 1);

            outputValues = bpn.test(inputValues);

            int idx = Util.getIndexOfMax(outputValues);

            if (idx == targets[0]) {
                right++;

            }

            output.write(String.format("%s,%s,%s,%s,%s--%s\n", data[0], data[1], data[2], data[3], data[4], idx));
            output.flush();

        }

        output.close();
        System.out.format("正确率：%s\n", right / datas.size());

    }

    public static double test(BPN bpn) {

        List<double[]> datas = getDataList(System.getProperty("user.dir") + "/data/test.txt");

        double right = 0d;
        for (double[] data : datas) {

            double inputValues[] = new double[4];
            double outputValues[] = null;
            double targets[] = new double[1];

            System.arraycopy(data, 0, inputValues, 0, data.length - 1);
            bpn.tramnmx(inputValues);

            /*for (int i = 0; i < targets.length; i++) {
                if (data[data.length - 1] == i) {
                    targets[i] = 1d;
                }
                System.out.format("index=%s\n", data[data.length - 1]);
                System.out.format("%s\n", targets[i]);
            }*/

            System.arraycopy(data, data.length - 1, targets, 0, 1);

            outputValues = bpn.test(inputValues);

            int idx = Util.getIndexOfMax(outputValues);

            if (idx == targets[0]) {
                right++;
            }

        }

        return (right / datas.size());
    }

    public static List<double[]> getDataList(String fileName) {

        List<double[]> list = new ArrayList<double[]>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            String line = null;

            while ((line = br.readLine()) != null) {
                String splits[] = line.split(",");
                double[] data = new double[splits.length];
                int i = 0;
                for (; i < splits.length; i++) {
                    try {
                        data[i] = (Double.valueOf(splits[i]));
                    } catch (NumberFormatException e) {
                        // 非数字，则为类别名称，将类别映射为数字
                        if (TYPE.setosa.name.equalsIgnoreCase(splits[i])) {
                            data[i] = TYPE.setosa.index;
                        } else if (TYPE.versicolor.name.equalsIgnoreCase(splits[i])) {
                            data[i] = TYPE.versicolor.index;
                        } else {
                            data[i] = TYPE.virginica.index;
                        }
                        list.add(data);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    enum TYPE {
        versicolor("Iris-versicolor", 0), setosa("Iris-setosa", 1), virginica("Iris-virginica", 2);

        private final String name;
        private final int index;

        private TYPE(String name, int index) {

            this.name = name;
            this.index = index;
        }/*

         public String getName() {

            return name;
         }

         public void setName(String name) {

            this.name = name;
         }

         public int getIndex() {

            return index;
         }

         public void setIndex(int index) {

            this.index = index;
         }*/
    };

}
