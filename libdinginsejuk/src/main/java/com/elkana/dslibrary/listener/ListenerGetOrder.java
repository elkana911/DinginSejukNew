package com.elkana.dslibrary.listener;

import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;

/**
 * Created by Eric on 05-Dec-17.
 */

public interface ListenerGetOrder {
    void onGetData(OrderHeader orderHeader, OrderBucket orderBucket);
    void onError(Exception e);
}
