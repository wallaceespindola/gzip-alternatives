package com.wtech.gziptests;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MyTestObject implements Serializable {

    static final int SIZE = 1_650; // to give about 20mb in the array initialization

    private final UUID id;
    private final String md5;
    private final String value;
    private double[][] obj = null;

    /**
     * Constructor for text mode
     *
     * @param value
     */
    public MyTestObject(String value) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.md5 = DigestUtils.md5Hex(value);
    }

    /**
     * Constructor for byte mode
     */
    public MyTestObject() {
        this("byte test mode");
        this.obj = new double[SIZE][SIZE];
        // initialize it
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                obj[i][j] = i * j;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyTestObject myTestObj = (MyTestObject) o;
        return Objects.equals(id, myTestObj.id) && Objects.equals(md5, myTestObj.md5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, md5);
    }

    @Override
    public String toString() {
        return "MyTestObject{" +
                "id=" + id +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
