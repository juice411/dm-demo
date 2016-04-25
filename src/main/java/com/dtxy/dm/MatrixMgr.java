package com.dtxy.dm;

import java.io.Serializable;

public class MatrixMgr implements Serializable {
    private final double[][] m1;
    private final double[][] m2;

    public MatrixMgr(double[][] m1, double[][] m2) {

        this.m1 = m1;
        this.m2 = m2;
    }

    public double[][] getM1() {

        return m1;
    }

    public double[][] getM2() {

        return m2;
    }
}
