package com.elkana.teknisi.screen.svcdtl;

import com.elkana.dslibrary.pojo.technician.ServiceItem;

/**
 * Created by Eric on 14-Nov-17.
 */

public interface ListenerServiceDetail {
    void onAddServiceDetail();
    void onDeleteItem(ServiceItem obj, int position);

    void onAddDataAC();
}
