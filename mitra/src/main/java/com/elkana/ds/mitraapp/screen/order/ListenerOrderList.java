package com.elkana.ds.mitraapp.screen.order;

import com.elkana.dslibrary.pojo.OrderBucket;

/**
 * Created by Eric on 05-Oct-17.
 */

public interface ListenerOrderList {
    void onItemSelected(OrderBucket order);
    void onChangeTech(OrderBucket data);

    void onRefresh();

    void onNewOrderCameIn(OrderBucket orderBucket);
}
