package com.elkana.teknisi.screen.svcdtl;

import com.elkana.dslibrary.pojo.technician.ServiceItem;

import java.util.List;

/**
 * Created by Eric on 14-Nov-17.
 */

public interface ListenerServiceDetail {
    void onAddServiceDetail();
    void onDeleteItem(ServiceItem obj, int position);

    // awal mulanya kosong, jadi user bisa tambah item terlebih dulu. biasa dipakai utk servicecharge
    void onPrepareList(List<ServiceItem> mList);

    void onAddDataAC(ServiceItem obj);
}
