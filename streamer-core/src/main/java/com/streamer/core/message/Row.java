package com.streamer.core.message;

import java.io.Serializable;

public class Row implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private Object value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Row() {

    }

    public Row(String key, Object value) {
        this.key = key;
        this.value = value;
    }

}
