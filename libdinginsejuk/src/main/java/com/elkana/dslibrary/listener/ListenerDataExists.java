package com.elkana.dslibrary.listener;

/**
 * Created by Eric on 18-Mar-18.
 */

public interface ListenerDataExists {
    void onFound();

    void onNotFound();

    void onError(Exception e);

}
