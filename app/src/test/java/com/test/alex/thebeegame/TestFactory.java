package com.test.alex.thebeegame;


import com.test.alex.thebeegame.controller.AbstractFactory;
import com.test.alex.thebeegame.model.BaseLevelLoader;
import com.test.alex.thebeegame.model.LevelLoader;


/**
 * Create objects for test cases
 */
public class TestFactory extends AbstractFactory {

    private static TestFactory self = new TestFactory();

    private TestFactory() {
    }

    static void init() {
        setSelf(self);
    }

    /**
     * @Inherite
     */
    public LevelLoader createLevelLoader(BaseLevelLoader.LevelLoaderErrorHandler eHandler) {
        LevelLoader ll = new TestLevelLoader(eHandler);

        return ll;
    }
}