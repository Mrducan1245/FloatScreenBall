package com.mrducan.floatscreenball;

import android.os.AsyncTask;

public final class AsyncTaskCompat {
    @SafeVarargs
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }

        AsyncTaskCompatHoneycomb.executeParallel(task, params);
        return task;
    }

    private AsyncTaskCompat() {
    }

}
