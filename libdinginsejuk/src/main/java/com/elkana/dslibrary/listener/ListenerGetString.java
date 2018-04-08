package com.elkana.dslibrary.listener;

/**
 * Created by Eric on 18-Mar-18.
 */

public interface ListenerGetString {
    void onSuccess(String value);
    void onError(Exception e);

}
