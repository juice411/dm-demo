package com.dtxy.dm.util;

public class Util {

    public static void main(String[] args) {

        // TODO Auto-generated method stub

    }

    public static int getIndexOfMax(double[] outputValues) {

        double max = -Integer.MIN_VALUE;
        int idx = -1;
        for (int j = 0; j != outputValues.length; j++) {
            if (outputValues[j] > max) {
                max = outputValues[j];
                idx = j;
            }
        }
        return idx;
    }

}
