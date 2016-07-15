package com.test.alex.thebeegame.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.test.alex.thebeegame.model.BaseLevelLoader;
import com.test.alex.thebeegame.model.LevelLoader;
import com.test.alex.thebeegame.model.ProductLevelLoader;

/**
 *  Available
 */
public class ProductionFactory extends AbstractFactory {

    private static volatile ProductionFactory self;
    private final Context ctx;

    private ProductionFactory(@NonNull Context ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("Context must be set");
        }

        this.ctx = ctx;
        this.self = this;
    }

    public static void init(@NonNull Context ctx) {
        if (self == null) {
            if (ctx == null) {
                throw new IllegalStateException("ProductionFactory must be initialized with Context; call init(Context ctx)");
            }

            self = new ProductionFactory(ctx);
            setSelf(self);
        }
    }

    /**
     * @Inherite
     */
    public LevelLoader createLevelLoader(BaseLevelLoader.LevelLoaderErrorHandler eHandler) {
        LevelLoader ll = new ProductLevelLoader(ctx, eHandler);

        return ll;
    }
}
