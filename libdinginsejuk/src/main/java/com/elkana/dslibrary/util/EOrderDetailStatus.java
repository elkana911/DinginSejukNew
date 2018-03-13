package com.elkana.dslibrary.util;

/**
 * Created by Eric on 30-Oct-17.
 */

public enum EOrderDetailStatus {
    /**
     * UNHANDLED = saat order baru basuk, sistem start timer selama 15 menit. jika tdk ada yg ambil maka status menjadi UNHANDLED
     */
    UNKNOWN, CREATED, RESCHEDULED, UNHANDLED, ASSIGNED, OTW, WORKING, PAYMENT, PAID, CANCELLED_BY_TIMEOUT, CANCELLED_BY_SERVER, CANCELLED_BY_CUSTOMER;

    public static EOrderDetailStatus convertValue(String value) {
        for (EOrderDetailStatus e : EOrderDetailStatus.values()) {
            if (e.name().equals(value))
                return e;
        }

        return UNKNOWN;
    }
}