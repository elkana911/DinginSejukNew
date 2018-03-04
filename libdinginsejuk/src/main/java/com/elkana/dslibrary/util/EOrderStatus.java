package com.elkana.dslibrary.util;

import com.elkana.dslibrary.pojo.OrderHeader;

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

    public static boolean isFinished(OrderHeader order) {
        return EOrderStatus.convertValue(order.getStatusId()) == EOrderStatus.FINISHED;

    }

}
