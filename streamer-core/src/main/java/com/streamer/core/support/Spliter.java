package com.streamer.core.support;


import org.apache.commons.lang3.StringUtils;

public enum Spliter {
    JSON("json", "", ""), KV("kv", "", "");

    private String name;

    private String key;

    private String value;

    Spliter(String name, String key, String value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Spliter spOf(String name, String key, String val) {
        Spliter current = Spliter.JSON;

        switch (name) {
        case "json":
            current = Spliter.JSON;
            break;
        case "kv":
            current = Spliter.KV;
            if (key != null && StringUtils.length(key.trim()) > 0 && StringUtils.length(val.trim()) > 0) {
                current.setKey(key);
                current.setValue(val);
            } else {
                current = Spliter.JSON;
            }
            break;

        default:
            current = Spliter.JSON;
            break;
        }
        return current;
    }

}
