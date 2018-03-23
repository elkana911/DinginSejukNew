package com.elkana.dslibrary.util;

/**
 * Created by Eric on 30-Oct-17.
 */

public enum EOrderDetailStatus {
    /**
     * UNHANDLED = saat order baru basuk, sistem start timer selama 15 menit. jika tdk ada yg ambil maka status menjadi UNHANDLED
     * sebelumnya ada RESHCEDULED tp dihapus, karena sistemnya CANCELLED_BY_CUSTOMER lalu CREATED lagi.
     *
     * INVALID_BOOKING utk future reference. saat ini kalo customer nakal ubah2 date bisanya dianggap CANCELLED. validasi wkt tetap dilakukan oleh mitra.
     */
    UNKNOWN, CREATED, /*RESCHEDULED, */UNHANDLED, ASSIGNED, OTW, WORKING, PAYMENT, PAID, CANCELLED_BY_TIMEOUT, CANCELLED_BY_SERVER, CANCELLED_BY_CUSTOMER, INVALID_BOOKING;

    public static EOrderDetailStatus convertValue(String value) {
        for (EOrderDetailStatus e : EOrderDetailStatus.values()) {
            if (e.name().equals(value))
                return e;
        }

        return UNKNOWN;
    }
}