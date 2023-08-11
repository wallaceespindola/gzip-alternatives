package com.wtech.gziptests;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MyFile implements Serializable {

    private final UUID id;

    private final String md5;

    private final String value;

    public MyFile(String value) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.md5 = DigestUtils.md5Hex(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFile myFile = (MyFile) o;
        return Objects.equals(id, myFile.id) && Objects.equals(md5, myFile.md5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, md5);
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "id=" + id +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
