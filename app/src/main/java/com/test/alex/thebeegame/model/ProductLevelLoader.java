package com.test.alex.thebeegame.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.test.alex.thebeegame.R;
import com.test.alex.thebeegame.utils.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProductLevelLoader extends BaseLevelLoader {

    private final AssetManager assets;

    /**
     * @param eHandler optional
     */
    public ProductLevelLoader(@NonNull final Context ctx, LevelLoaderErrorHandler eHandler) {
        super(eHandler);
        Assert._assert(ctx != null, "Context can't be null");

        assets = ctx.getAssets();
        UNIT_NAME_PREFIX = ctx.getString(R.string.bee_name_prefix);
    }

    protected String loadFileAsString(String path) throws IOException {
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        try {
            String temp;

            InputStream json = assets.open(path);
            reader = new BufferedReader(new InputStreamReader(json, "UTF-8"));

            while ((temp = reader.readLine()) != null) {
                buf.append(temp);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }

        return buf.toString();
    }

}
