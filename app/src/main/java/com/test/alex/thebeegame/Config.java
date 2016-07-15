package com.test.alex.thebeegame;

import android.content.pm.PackageManager;

public class Config {

    private Config() {
    }

    /**
     * In release app version such check should be used here
     * ASSERT_ENABLED = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
     */
    public static final boolean ASSERT_ENABLED = true;
}
