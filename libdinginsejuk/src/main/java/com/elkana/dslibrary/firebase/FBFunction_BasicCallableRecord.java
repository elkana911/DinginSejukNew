package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Map;

public class FBFunction_BasicCallableRecord implements Continuation<HttpsCallableResult, Map<String, Object>> {
    @Override
    public Map<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
        HttpsCallableResult callableResult = task.getResult();

        Map<String, Object> data = (Map<String, Object>) callableResult.getData();

//            ObjectMapper mapper = new ObjectMapper();
//            ResponseCreateBooking obj = mapper.convertValue(data, ResponseCreateBooking.class);
        return data;
    }
}
