package com.elkana.dslibrary.listener;

/**
 * Created by Eric on 18-Mar-18.
 */

public interface ListenerGetLong {
    void onSuccess(long value);
    void onError(Exception e);

}
