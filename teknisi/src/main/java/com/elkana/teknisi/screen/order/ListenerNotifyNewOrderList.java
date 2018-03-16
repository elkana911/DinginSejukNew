package com.elkana.teknisi.screen.order;

import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;

/**
 * Created by Eric on 10-Mar-18.
 */

public interface ListenerNotifyNewOrderList {
    void onDeny(NotifyNewOrderItem data);

    void onAccept(NotifyNewOrderItem data);

    void onOrderRemoved(NotifyNewOrderItem data);

    void onTimesUp();

    void onTimerStart();
}
