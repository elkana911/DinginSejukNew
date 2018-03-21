package com.elkana.dslibrary.listener;

import java.util.List;

/**
 * Created by Eric on 14-Dec-17.
 */

public interface ListenerGetAllData {
    void onSuccess(List<? extends Object> list);
    void onError(Exception e);

    void onPrepare();
}
