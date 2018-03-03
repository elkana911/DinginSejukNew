package com.elkana.dslibrary.util;

/**
 * Created by Eric on 30-Oct-17.
 */

public enum EOrderStatus {
    PENDING, FINISHED;

    public static EOrderStatus convertValue(String value) {
        for (EOrderStatus e : EOrderStatus.values()) {
            if (e.name().equals(value))
                return e;
        }

        return null;
    }

}
