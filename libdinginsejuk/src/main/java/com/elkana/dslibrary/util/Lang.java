package com.elkana.dslibrary.util;

/**
 * Created by Eric on 19-Oct-17.
 */
public enum Lang {
    INDONESIAN("id"), ENGLISH("en");

    private String code;

    private Lang(String id) {
        code = id;
    }

    public String getCode() {
        return code;
    }
}

