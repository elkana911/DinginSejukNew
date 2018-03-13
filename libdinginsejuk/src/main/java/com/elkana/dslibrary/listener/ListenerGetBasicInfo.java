package com.elkana.dslibrary.listener;

import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;

import java.util.List;

/**
 * Created by Eric on 11-Mar-18.
 */

public interface ListenerGetBasicInfo {
    /**
     *
     * @param basicInfo
     */
    void onFound(BasicInfo basicInfo, List<FirebaseToken> list);
    void onError(Exception e);
}
