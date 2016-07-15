package com.test.alex.thebeegame.bot;

import android.support.annotation.NonNull;

import com.test.alex.thebeegame.model.Board;
import com.test.alex.thebeegame.model.Unit;
import com.test.alex.thebeegame.utils.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This is simple bot which makes random moves
 */
public class RandomHitBot extends Bot {

    /**
     * Bot "brain"
     */
    private Random rand = new Random();

    private String lastHitterId;
    private Unit lastHitter;

    public RandomHitBot(@NonNull Board board) {
        super(board);
    }

    public String nextHitTargetId() {
        String result;

        //  Select one to hit
        HashMap<String, Unit> units = board.getCurOppositUnits();
        result = chooseRandomOne(units);

        //  Choose who hit
        units = board.getCurPlayerUnits();
        lastHitterId = chooseRandomOne(units);
        lastHitter = units.get(lastHitterId);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getLastHitterId() {
        return lastHitterId;
    }

    public Unit getLastHitter() {
        return lastHitter;
    }

    private String chooseRandomOne(HashMap<String, Unit> units) {
        String result;
        List<String> aliveIds = Board.getAliveUnitsId(units);

        int num = aliveIds.size();
        Assert._assert(num > 0, "Logic error: This shouldn't happen");

        if(num == 1) {
            //  Only one left, lest finish with it!
            result = aliveIds.get(0);
        } else {
            int index = rand.nextInt(num);
            result = aliveIds.get(index);
        }

        return result;
    }
}
