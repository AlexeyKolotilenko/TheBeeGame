package com.test.alex.thebeegame.model;

/**
 * General interface for player events.
 */
public interface PlayerEventListener {

    void onUnitWereHit(Unit hitUnit, int startHP);
    void onPlayerSwapped();
    void onPlayerLost();
    void onPlayerWin();
}
