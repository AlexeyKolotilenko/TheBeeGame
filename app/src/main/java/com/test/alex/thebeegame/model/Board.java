package com.test.alex.thebeegame.model;

import android.support.annotation.NonNull;

import com.test.alex.thebeegame.utils.Assert;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Reflect current board state
 */
public class Board {

    private HashMap<String, Unit> _opposerUnitMap;
    private HashMap<String, Unit> _userUnitMap;

    public enum CurrentPlayer {
        User,
        Opposer
    }

    private CurrentPlayer curPlayer;
    private HashMap<String, Unit> curPlayerUnits;
    private HashMap<String, Unit> curOppositUnits;

    public Board(@NonNull HashMap<String, Unit> opposerUnitMap,
                  @NonNull HashMap<String, Unit> userUnitMap,
                  @NonNull List<UnitType> unitTypes) {
        this._opposerUnitMap = opposerUnitMap;
        this._userUnitMap = userUnitMap;

        //Must be set
        curPlayer = null;
    }

    void setCurrentPlayer(CurrentPlayer side) {
        curPlayer = side;
        switch (curPlayer) {
            case Opposer:
                curPlayerUnits = _opposerUnitMap;
                curOppositUnits = _userUnitMap;
                break;
            case User:
                curPlayerUnits = _userUnitMap;
                curOppositUnits = _opposerUnitMap;
                break;
            default:
                Assert._assert(false, "This shouldn't happen");
        }
    }

    boolean checkCurPlayerLost() {
        //  Bypass lost rules. Currently there is no such.
        return false;
    }

    boolean checkCurPlayerWin() {
        //  TODO Could be refactored to Strategy patter in the case of many rules will be add.
        //  Currently we have only one.
        boolean isOpposerQueenDead = checkQueenIsDead(curOppositUnits);
        return isOpposerQueenDead;
    }

    public HashMap<String, Unit> getCurOppositUnits() {
        return curOppositUnits;
    }

    public HashMap<String, Unit> getCurPlayerUnits() {
        return curPlayerUnits;
    }


    /**
     * Check wheather queen for cur player is dead.
     *
     * @return {@code true} if yes.
     */
    private boolean checkQueenIsDead(HashMap<String, Unit> unitMap) {
        Boolean result = false;
        for (Map.Entry<String, Unit> entry : unitMap.entrySet()) {
            Unit unit = entry.getValue();
            UnitType type = unit.getType();
            if (type.isQueen()) {
                if (unit.isAlive() == true) {
                    result = false;     // Not dead.
                } else {
                    result = true;
                }

                break;
            }
        }

        //  Currently Queen is required
        Assert._assert(result != null, "Logic error: Queen is not in list!");

        return result;
    }

    public static List<String> getAliveUnitsId(HashMap<String, Unit> units) {
        LinkedList<String> result = new LinkedList<String>();

        for (Map.Entry<String, Unit> entry : units.entrySet()) {
            String id = entry.getKey();
            Unit unit = entry.getValue();
            if (unit.isAlive() == true) {
                result.add(id);
            }
        }

        return result;
    }
}
