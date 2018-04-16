package com.elkana.customer.screen.order;


import com.elkana.dslibrary.pojo.OrderHeader;

/**
 * Created by Eric on 05-Oct-17.
 */

public interface ListenerOrderList {
    void onItemOrderSelected(OrderHeader order);
    void onAddOrder();
    void onUpdateOrder(String customerId);
    void onCancelOrder(OrderHeader order);
    void onCheckStatus(OrderHeader order);
}
