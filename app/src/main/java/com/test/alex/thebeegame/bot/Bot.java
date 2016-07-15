package com.test.alex.thebeegame.bot;

import com.test.alex.thebeegame.model.Board;

import java.util.List;

/**
 * Interface for bot-payer.
 */
public abstract class Bot {

    protected Board board;

    Bot(Board board) {
        if(board == null) {
            throw new NullPointerException("Board is required parameter");
        }

        this.board = board;
    }

    /**
     * This method calculate net target to be hit basing on current board state
     *
     * @return
     */
    abstract String nextHitTargetId();

    /**
     * Get id of hitter unit. Can be {@code null} in this case hitter is unknown.
     * This could be useful to make animated hit from bot unit.
     */
    String getLastHitterId() {
        return null;
    }

}
