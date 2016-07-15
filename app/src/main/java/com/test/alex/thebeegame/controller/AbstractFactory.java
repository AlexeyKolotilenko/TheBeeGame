package com.test.alex.thebeegame.controller;


import com.test.alex.thebeegame.model.BaseLevelLoader;
import com.test.alex.thebeegame.model.LevelLoader;

/**
 * Define factory interfaces and contains general factory interfaces.
 * TODO If app will have more than one Activity class object of AbstractFactory should load manually in Application subclass
 */
public abstract class AbstractFactory {

    private static AbstractFactory self;

    /**
     * @param eHandler optional, allows delegate error handler to user;
     */
    public abstract LevelLoader createLevelLoader(BaseLevelLoader.LevelLoaderErrorHandler eHandler);

    protected static void setSelf(AbstractFactory _self) {
        self = _self;
    }

    public static AbstractFactory getInstance() {
        if(self == null) {
            throw new IllegalStateException("Concrete Factory must be initialized first via init() call");
        }
        return self;
    }
}
