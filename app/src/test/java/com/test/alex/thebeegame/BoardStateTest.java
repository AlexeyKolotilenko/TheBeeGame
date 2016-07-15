package com.test.alex.thebeegame;


import com.test.alex.thebeegame.model.Board;
import com.test.alex.thebeegame.model.LevelLoader;
import com.test.alex.thebeegame.model.Unit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Validate initial game state defined by board.
 * As most validation performed in game runtime (like initial unit HP and hit amount) we need only
 * to check units number - like {@link GameProcessIntegrationTest#validateSideUnitNumber} but with
 * strict conditions.
 */
public class BoardStateTest {

    public final String SCENARIO_NAME = "level 1";

    private LevelLoader ll;

    @Before
    public void prepareBoard() {
        TestFactory.init();
        ll = TestFactory.getInstance().createLevelLoader(null);
    }

    @Test
    public void loadScenario() {
        Board board = ll.createBoardForScenario(SCENARIO_NAME);
        validateBoard(board);
    }

    private void validateBoard(Board board) {
        HashMap<String, Unit> playerUnits = board.getCurPlayerUnits();
        HashMap<String, Unit> opUnits = board.getCurOppositUnits();

        validateUnitSet(playerUnits);
        validateUnitSet(opUnits);
    }

    private void validateUnitSet(HashMap<String, Unit> units) {

    }

    private void validateSideUnitNumber(HashMap<String, Unit> map) {
        int qCount = 0;
        int wCount = 0;
        int dCount = 0;

        for (Map.Entry<String, Unit> entry : map.entrySet()) {
            Unit unit = entry.getValue();

            switch (unit.getType().getTypeName()) {
                case TestData.DRONE_BEE:
                    dCount++;
                    break;
                case TestData.QUEEN_BEE:
                    qCount++;
                    break;
                case TestData.WORKER_BEE:
                    wCount++;
                    break;
                default:
                    Assert.fail("Unexpected type");
            }
        }

        Assert.assertTrue(qCount == TestData.INITIAL_QUEEN_NUMBER);
        Assert.assertTrue(wCount == TestData.INITIAL_WORKER_NUMBER);
        Assert.assertTrue(dCount == TestData.INITIAL_DRONE_NUMBER);
    }

}
