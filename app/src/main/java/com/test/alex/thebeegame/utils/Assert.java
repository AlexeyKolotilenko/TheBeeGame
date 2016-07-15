package com.test.alex.thebeegame.utils;

import android.util.Log;

import com.test.alex.thebeegame.Config;


public class Assert extends RuntimeException {

    private Assert(String errorDescription) {
        super(errorDescription);
    }

    private Assert(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }


    public static void _assert(boolean expr, String errorDescription) {
        if (Config.ASSERT_ENABLED)
            if (!expr) {
                Log.e("Assert thrown: ", errorDescription);
                throw new Assert(errorDescription);
            }
    }
}
