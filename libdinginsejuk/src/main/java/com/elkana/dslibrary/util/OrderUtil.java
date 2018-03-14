package com.elkana.dslibrary.util;

import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;

import java.util.Date;

/**
 * Created by Eric on 14-Mar-18.
 */

public class OrderUtil {

    public static void setScheduleOrder(OrderHeader orderHeader, OrderBucket orderBucket, Date newDate, String updatedBy) {
        orderHeader.setRescheduleCounter(orderHeader.getRescheduleCounter() + 1);
        orderHeader.setDateOfService(Util.convertDateToString(newDate, "yyyyMMdd"));
        orderHeader.setTimeOfService(Util.convertDateToString(newDate, "HH:mm"));
        orderHeader.setTimestamp(newDate.getTime());
        orderHeader.setUpdatedBy(updatedBy);
        orderHeader.setUpdatedTimestamp(new Date().getTime());

        orderBucket.setOrderTimestamp(orderHeader.getTimestamp());
        orderBucket.setUpdatedBy(updatedBy);
        orderBucket.setUpdatedTimestamp(new Date().getTime());

    }

    public static void setRescheduleOrder(OrderHeader orderHeader, OrderBucket orderBucket, Date newDate, String updatedBy) {
        setScheduleOrder(orderHeader, orderBucket, newDate, updatedBy);

        // reset some property
        orderHeader.setTechnicianId(null);
        orderHeader.setStatusDetailId(EOrderDetailStatus.CREATED.name());

        orderBucket.setTechnicianId(null);
        orderBucket.setTechnicianName(null);

    }

}
