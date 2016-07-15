package com.test.alex.thebeegame.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.test.alex.thebeegame.bot.RandomHitBot;
import com.test.alex.thebeegame.model.BaseLevelLoader;
import com.test.alex.thebeegame.model.Board;
import com.test.alex.thebeegame.model.LevelLoader;
import com.test.alex.thebeegame.model.PlayerEventListener;
import com.test.alex.thebeegame.model.RoundController;
import com.test.alex.thebeegame.model.Unit;
import com.test.alex.thebeegame.utils.Assert;

/**
 *  Configure game process
 */
public class GameController {

    //  Handler
    private final BaseLevelLoader.LevelLoaderErrorHandler eHandler;
    private final GameActionHandler actionHandler;

    // Game logic

    private RoundController rc;
    private RandomHitBot userBot;
    private RandomHitBot pcBot;

    private PlayerEventListener userEvents;
    private PlayerEventListener pcEvents;


    public GameController(BaseLevelLoader.LevelLoaderErrorHandler eHandler, @NonNull GameActionHandler actionHandler) {
        if(actionHandler == null) {
            throw new IllegalArgumentException("Action Handler were not set");
        }

        this.eHandler = eHandler;
        this.actionHandler = actionHandler;
    }

    /**
     * Start new round.
     *
     * @param scenarioName start given scenario, or default if {@code scenarioName} {@code null}
     */
    public void startRound(String scenarioName) {
        final String DEFAULT_SCENARIO_NAME = "level 1";

        if(scenarioName == null) {
            scenarioName = DEFAULT_SCENARIO_NAME;
        }

        rc = new RoundController(eHandler, scenarioName);

        initGameEventListeners();

        rc.setFirstPlayer(userEvents);
        rc.setSecondPlayer(pcEvents);

        final Board board = rc.getRoundBoard();
        Assert._assert(board != null, "Logic error - board should be initialized here");

        userBot = new RandomHitBot(board);
        pcBot = new RandomHitBot(board);
    }

    private void initGameEventListeners() {
        userEvents = new PlayerEventListener() {
            @Override
            public void onUnitWereHit(Unit unit, int startHP) {
                Unit hitter = userBot.getLastHitter();
                actionHandler.showNewHitData(unit, startHP, hitter, true);
            }

            @Override
            public void onPlayerSwapped(){
                // Now make PC hit
                String targetId = pcBot.nextHitTargetId();
                rc.makeHit(targetId);
            }

            @Override
            public void onPlayerLost() {
                actionHandler.showPlayerLost();
            }

            @Override
            public void onPlayerWin() {
                actionHandler.showPlayerWin();
            }
        };

        pcEvents = new PlayerEventListener() {
            @Override
            public void onUnitWereHit(Unit unit, int startHP) {
                Unit hitter = pcBot.getLastHitter();
                actionHandler.showNewHitData(unit, startHP, hitter, false);
                //  Show PC hit result and wait until user make next hit
            }

            @Override
            public void onPlayerSwapped(){
                //  Not job here
            }

            @Override
            public void onPlayerLost() {
                //  PC lost means Player win
                actionHandler.showPlayerWin();
            }

            @Override
            public void onPlayerWin() {
                //  PC win means Player lost
                actionHandler.showPlayerLost();
            }
        };
    }

    public void dispatchUserHit() {
        String targetId = userBot.nextHitTargetId();
        rc.makeHit(targetId);
    }
}
