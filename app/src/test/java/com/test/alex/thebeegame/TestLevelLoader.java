package com.test.alex.thebeegame;

import com.test.alex.thebeegame.model.BaseLevelLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Allow to load resources without android context
 */
public class TestLevelLoader extends BaseLevelLoader {

    private final String BASE_ASSET_PATH = "main/assets/";

    public TestLevelLoader(LevelLoaderErrorHandler eHandler) {
        super(eHandler);
        UNIT_NAME_PREFIX = "Test Name";
    }

    protected String loadFileAsString(String path) throws IOException {
        //"main/assets/scenarios/level 1/units.txt";
        path = BASE_ASSET_PATH + path;

        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        try {
            String temp;

            InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

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
