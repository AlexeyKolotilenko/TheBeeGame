package com.test.alex.thebeegame.controller;

import com.test.alex.thebeegame.model.Unit;

/**
 * Contains event which UI must implement
 */
public interface GameActionHandler {
    void showNewHitData(Unit hitUnit, int startHP, Unit hitter, boolean isUserHit);
    void showPlayerWin();
    void showPlayerLost();
}
