package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.List;
import java.util.Map;

public class FBFunction_BasicCallableList implements Continuation<HttpsCallableResult, List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
        HttpsCallableResult callableResult = task.getResult();

        return (List<Map<String, Object>>) callableResult.getData();
    }
}
