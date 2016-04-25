package com.dtxy.dm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPN {
    private static final Logger LOG = LoggerFactory.getLogger(BPN.class);

    private final int inputNeurons;
    private final int hiddenNeurons;
    private final int outputNeurons;
    private final double learningrate;
    private double[][] m1;
    private double[][] m2;

    //构造神经网络对象
    public BPN(int input, int hidden, int output, double lrate) {

        inputNeurons = input; //输入
        hiddenNeurons = hidden; //隐藏层
        outputNeurons = output; //输出
        learningrate = lrate; //学习概率
        m1 = new double[inputNeurons + 1][hiddenNeurons]; //输入到隐藏层的加权
        m2 = new double[hiddenNeurons + 1][outputNeurons]; //隐藏层到输出层的加权 
        fillRandom(m1);
        fillRandom(m2);
    }

    //迭代设置网络最小权值,即 -1 和 1 之间的随机数
    private void fillRandom(double[][] matrix) {

        Random ran = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = ran.nextDouble() * 2 - 1;
            }
        }

    }

    //根据前层计算下层的值
    private void calculateIn2Out(double[] inputValuesIn, double[] outputValuesOut, double[][] m) {

        for (int i = 0; i < outputValuesOut.length; i++) {
            double sum = 0;
            for (int j = 0; j < inputValuesIn.length; j++) {
                sum += m[j][i] * inputValuesIn[j];
            }
            outputValuesOut[i] = sigmoid(sum); //计算隐藏层
        }
    }

    //激励函数
    private double sigmoid(double sum) {

        return 1 / (1 + Math.exp(-(sum)));
    }

    // 反向激励函数，即sigmoid函数的导数
    private double backwardSigmoid(double outputValuesIn, double errSum) {

        return outputValuesIn * (1 - outputValuesIn) * errSum;
    }

    //双s型激活函数
    private double logsig(double sum) {

        return ((Math.exp(sum) - Math.exp(-sum)) / (Math.exp(sum) + Math.exp(-sum)));
    }

    //双s型激活函数的导数
    private double backwardLogsig(double outputValuesIn, double errSum) {

        return ((1 - Math.pow(outputValuesIn, 2)) * errSum);
    }

    //对输入数据做归一化[0.0,1.0]
    public void tramnmx(double[] x) {

        double min = x[0], max = x[0];
        for (int i = 0; i < x.length; i++) {
            if (min > x[i]) {
                min = x[i];
            } else if (max < x[i]) {
                max = x[i];
            }

        }
        //System.out.println(String.format("-------min=%s,max=%s-----------------", min, max));
        for (int i = 0; i < x.length; i++) {
            x[i] = (x[i] - min) / (max - min);

        }
        /*for(int i=0;i<x.length;i++){
        	System.out.println(String.format("-------%s-----------------" ,x[i]));
        	
        }*/
    }

    //对输入数据做归一化[-1.0,1.0]
    public void premnmx(double[] x) {

        double min = x[0], max = x[0];
        for (int i = 0; i < x.length; i++) {
            if (min > x[i]) {
                min = x[i];
            } else if (max < x[i]) {
                max = x[i];
            }

        }
        //System.out.println(String.format("-------min=%s,max=%s-----------------", min, max));
        for (int i = 0; i < x.length; i++) {
            x[i] = 2 * (x[i] - min) / (max - min) - 1;

        }
        /*for(int i=0;i<x.length;i++){
            System.out.println(String.format("-------%s-----------------" ,x[i]));
            
        }*/
    }

    //反向更新权值
    private void update(double[] valuesIn, double[] error, double[][] m) {

        for (int l = 0; l < m.length; l++) {
            for (int o = 0; o < m[0].length; o++) {
                m[l][o] = m[l][o] + learningrate * error[o] * valuesIn[l];
            }
        }
    }

    public void train(double[] inputValues, double[] targets) {

        double[] inputValuesIn = new double[inputValues.length + 1];//输入层层作为in端时

        double[] hiddenValuesOut = new double[hiddenNeurons];//隐层作为输出端时
        double[] hiddenValuesIn = new double[hiddenNeurons + 1];//隐层作为输入端时

        double[] outputValues = new double[outputNeurons];//最终输出

        double[] outputError = new double[outputValues.length]; //输出单元误差
        double[] hiddenError = new double[hiddenValuesIn.length]; //隐藏单元误差

        System.arraycopy(inputValues, 0, inputValuesIn, 1, inputValues.length);
        inputValuesIn[0] = -1d;//为了动态调整输入到隐层的偏移量

        calculateIn2Out(inputValuesIn, hiddenValuesOut, m1);//计算输入到隐层的输出值

        System.arraycopy(hiddenValuesOut, 0, hiddenValuesIn, 1, hiddenValuesOut.length);
        hiddenValuesIn[0] = -1d;//为了动态调整隐层到输出的偏移量        
        calculateIn2Out(hiddenValuesIn, outputValues, m2);//计算隐层到输出的输出值

        //利用神经网络加权计算输出单元误差outputError
        for (int j = 0; j < outputValues.length; j++) {
            // outputError[j] = outputValues[j] * (1 - outputValues[j]) *(targets[j] - outputValues[j]);
            outputError[j] = backwardSigmoid(outputValues[j], targets[j] - outputValues[j]);

        }

        //利用神经网络加权计算隐藏单元误差hiddenError

        for (int j = 0; j < hiddenValuesIn.length; j++) {
            double sum = 0;
            for (int k = 0; k < outputNeurons; k++) {
                sum += m2[j][k] * outputError[k];
            }
            // hiddenError[j] = hiddenValuesIn[j] * (1 - hiddenValuesIn[j]) *sum;
            hiddenError[j] = backwardSigmoid(hiddenValuesIn[j], sum);
        }

        //更新网络权值m2
        update(hiddenValuesIn, outputError, m2);

        //更新网络权值m1
        update(inputValuesIn, hiddenError, m1);

    }

    public double[] test(double[] inputValues) {

        double[] inputValuesIn = new double[inputValues.length + 1];//输入层层作为in端时

        double[] hiddenValuesOut = new double[hiddenNeurons];//隐层作为输出端时
        double[] hiddenValuesIn = new double[hiddenNeurons + 1];//隐层作为输入端时

        double[] outputValues = new double[outputNeurons];//最终输出

        System.arraycopy(inputValues, 0, inputValuesIn, 1, inputValues.length);
        inputValuesIn[0] = -1d;

        calculateIn2Out(inputValuesIn, hiddenValuesOut, m1);//计算输入到隐层的输出值

        System.arraycopy(hiddenValuesOut, 0, hiddenValuesIn, 1, hiddenValuesOut.length);
        hiddenValuesIn[0] = -1d;
        calculateIn2Out(hiddenValuesIn, outputValues, m2);//计算隐层到输出的输出值

        return outputValues;

    }

    public void saveMatrix(String path) {

        MatrixMgr ms = new MatrixMgr(m1, m2);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            //将对象写入文件
            oos.writeObject(ms);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public MatrixMgr loadMatrix(String path) {

        MatrixMgr ms = null;
        try {

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            //将对象写入文件
            ms = (MatrixMgr) ois.readObject();
            this.m1 = ms.getM1();
            this.m2 = ms.getM2();
            ois.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ms;

    }

}
