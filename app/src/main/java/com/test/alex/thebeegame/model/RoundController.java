package com.test.alex.thebeegame.model;


import android.content.Context;
import android.support.annotation.NonNull;

import com.test.alex.thebeegame.controller.AbstractFactory;
import com.test.alex.thebeegame.utils.Assert;

public class RoundController {
//    private String roundName;

    private Board roundBoard;

    private PlayerEventListener firstPlayer;
    private PlayerEventListener secondPlayer;

    Board.CurrentPlayer currentPlayer;

    public RoundController(BaseLevelLoader.LevelLoaderErrorHandler eHandler, @NonNull String scenarioName) {
        LevelLoader ll = AbstractFactory.getInstance().createLevelLoader(eHandler);
        roundBoard = ll.createBoardForScenario(scenarioName);

        // User hit first
        currentPlayer = Board.CurrentPlayer.User;
        roundBoard.setCurrentPlayer(currentPlayer);
    }

    public Board getRoundBoard() {
        return roundBoard;
    }

    private void swapCurrentPlayer() {
        switch (currentPlayer) {
            case User:
                currentPlayer = Board.CurrentPlayer.Opposer;
                roundBoard.setCurrentPlayer(currentPlayer);
                break;
            case Opposer:
                currentPlayer = Board.CurrentPlayer.User;
                roundBoard.setCurrentPlayer(currentPlayer);
                break;
            default:
                Assert._assert(false, "Logic error");
        }
    }

    private PlayerEventListener getListenerForCurrentPlayer() {
        if(Board.CurrentPlayer.User == currentPlayer) {
            return firstPlayer;
        } else if (Board.CurrentPlayer.Opposer == currentPlayer) {
            return secondPlayer;
        } else {
            Assert._assert(false, "Logic error");
            return null;
        }
    }


    public void makeHit(String targetId) {
        Assert._assert(targetId != null, "Illegal target id");
        Unit unit = roundBoard.getCurOppositUnits().get(targetId);
        Assert._assert(unit != null, "Illegal state");

        int startHP = unit.getHP();
        unit.hitUnit();

        PlayerEventListener listener = getListenerForCurrentPlayer();
        listener.onUnitWereHit(unit, startHP);

        //  Check current user win/lost
        if(roundBoard.checkCurPlayerLost()) {
            //  Currently will always false, was add for consistency
            listener.onPlayerLost();
        } else if(roundBoard.checkCurPlayerWin()) {
            listener.onPlayerWin();
        } else {
            swapCurrentPlayer();
            listener.onPlayerSwapped();
        }
    }


    public void setFirstPlayer(@NonNull PlayerEventListener player) {
        firstPlayer = player;
    }

    public void setSecondPlayer(@NonNull PlayerEventListener player) {
        secondPlayer = player;
    }

}
