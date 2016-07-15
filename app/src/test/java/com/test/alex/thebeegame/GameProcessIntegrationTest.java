package com.test.alex.thebeegame;

import android.util.Log;

import com.test.alex.thebeegame.controller.GameActionHandler;
import com.test.alex.thebeegame.controller.GameController;
import com.test.alex.thebeegame.model.BaseLevelLoader;
import com.test.alex.thebeegame.model.Unit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class GameProcessIntegrationTest implements GameActionHandler {

    //public int MAX_TEST_TIME_MILIS = 60000;

    private GameController gameController;
    private Timer userHitDispatcher;

    /**
     * Lock on it prevents test finish before end the game
     */
    private Object m = new Object();

    private boolean wasCriticalTestError = false;
    private Error error = null;
    private RuntimeException re = null;

    @Before
    public void prepareBoard() {
        TestFactory.init();
        userHitDispatcher = new Timer();
    }

    @Test
    public void testAll() {
        initGameLogic();
        //  Make first hit
        dispatchUserHit();

        synchronized (m) {
            try {
                //m.wait(MAX_TEST_TIME_MILIS);
                m.wait();
            } catch (InterruptedException e) {
                Assert.fail("JUnit thread unexpectedly interrupted before test finish");
                unblockMainThread();
            }
        }

        if (wasCriticalTestError) {
            if (error != null) throw error;
            if (re != null) throw re;
        }
    }

    private void dispatchUserHit() {
        userHitDispatcher.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    gameController.dispatchUserHit();
                } catch (Error e) { //  For assets
                    //reason = "Test thread finished with exception /n " + e.toString();
                    error = e;
                    wasCriticalTestError = true;
                    unblockMainThread();
                } catch (RuntimeException e1) { //  Test Exception
                    re = e1;
                    wasCriticalTestError = true;
                    unblockMainThread();
                } catch (Throwable e2) {
                    wasCriticalTestError = true;
                    Log.e(getClass().getSimpleName(), "Unexpected!");
                    unblockMainThread();
                }
            }
        }, 1);
    }


    private void initGameLogic() {
        BaseLevelLoader.LevelLoaderErrorHandler levelLoaderErrorHandler = new BaseLevelLoader.LevelLoaderErrorHandler() {
            @Override
            public void onCantReadDataFile(Exception e) {
                Assert.fail("onCantReadDataFile");
            }

            @Override
            public void onCantParseGameData(Exception e) {
                Assert.fail("onCantParseGameData");
            }
        };

        gameController = new GameController(levelLoaderErrorHandler, this);
        gameController.startRound(null);
    }


    @Override
    public void showNewHitData(Unit hitUnit, int startHP, Unit hitter, boolean isUserHit) {
        if (!isGameOverCalled) {
            Assert.assertNotNull(hitUnit);
            Assert.assertNotNull(hitter);
            Assert.assertTrue(startHP > 0);

            validateIsQueenDead();

            Unit prevUnit = updateUnitMap(hitUnit, isUserHit);

            if (prevUnit == null) {
                validateInitialHealth(hitUnit, startHP);
            }

            validateHitAmountAndHealthLosses(prevUnit, hitUnit, startHP, isUserHit);
            validateDeadUnits(prevUnit, hitter);
            validateHitOrder(isUserHit);
            validateUnitOwnership(hitUnit, hitter, isUserHit);

            if (!isUserHit) {
                dispatchUserHit();
            }
        }
    }

    @Override
    public void showPlayerWin() {
        validatePlayerWin();

        unblockMainThread();
    }

    @Override
    public void showPlayerLost() {
        validatePlayerLost();
        unblockMainThread();
    }

    private void unblockMainThread() {
        synchronized (m) {
            m.notify();
        }
    }

    HashMap<String, Unit> userUnitForId = new HashMap<String, Unit>();
    HashMap<String, Unit> pcUnitForId = new HashMap<String, Unit>();


    private Unit updateUnitMap(Unit hitUnit, boolean isUserHit) {
        Unit prevValue;

        if (isUserHit) {
            prevValue = userUnitForId.put(hitUnit.getUnitId(), hitUnit.copy());
        } else {
            prevValue = pcUnitForId.put(hitUnit.getUnitId(), hitUnit.copy());
        }

        return prevValue;
    }

    private boolean isGameOverCalled = false;

    private void validatePlayerWin() {
        validateUnitNumber();
        Assert.assertFalse(pcUnitForId.containsKey(pcQueenId));
        isGameOverCalled = true;
    }

    private void validatePlayerLost() {
        validateUnitNumber();
        Assert.assertFalse(userUnitForId.containsKey(userQueenId));
        isGameOverCalled = true;
    }

    //---------         Validate initial unit health      ----------

    private void validateInitialHealth(Unit hitUnit, int startHP) {
        switch (hitUnit.getType().getTypeName()) {
            case TestData.DRONE_BEE:
                Assert.assertEquals(startHP, TestData.INITIAL_DRONE_HP);
                break;
            case TestData.QUEEN_BEE:
                Assert.assertEquals(startHP, TestData.INITIAL_QUEEN_HP);
                break;
            case TestData.WORKER_BEE:
                Assert.assertEquals(startHP, TestData.INITIAL_WORKER_HP);
                break;
            default:
                Assert.fail("Unexpected type");
        }
    }

    //---------         Validate hit order      ----------

    private boolean wasUserHit = false;

    private void validateHitOrder(boolean isUserHit) {
        Assert.assertTrue(isUserHit != wasUserHit);
        wasUserHit = isUserHit;
    }

    //---------         Validate unit ownership       ----------
    private void validateUnitOwnership(Unit hitUnit, Unit hitter, boolean isUserHit) {
        if (isUserHit) {
            Assert.assertFalse(userUnitForId.containsKey(hitter.getUnitId()));
            Assert.assertFalse(pcUnitForId.containsKey(hitUnit.getUnitId()));
        } else {
            Assert.assertFalse(pcUnitForId.containsKey(hitter.getUnitId()));
            Assert.assertFalse(userUnitForId.containsKey(hitUnit.getUnitId()));
        }
    }

    //---------         Validate dead units     ----------

    private void validateDeadUnits(Unit prevUnit, Unit hitter) {
        if (prevUnit != null) {
            Assert.assertTrue(prevUnit.isAlive());
        }
        Assert.assertTrue(hitter.isAlive());
    }

    //---------         Validate hit amount and unit data consistency ----------

    private void validateHitAmountAndHealthLosses(Unit prevUnit, Unit hitUnit, int startHP, boolean isUserHit) {
        //  Data consistency
        if (prevUnit != null) {
            Assert.assertEquals("Unit change name", prevUnit.getUnitName(), hitUnit.getUnitName());
            Assert.assertEquals("Unit change type", prevUnit.getType(), hitUnit.getType());

            Assert.assertEquals(prevUnit.getHP(), startHP);
        }

        if (hitUnit.isAlive()) {
            Assert.assertTrue(hitUnit.getHP() > 0);
        }

        // Validate damage
        switch (hitUnit.getType().getTypeName()) {
            case TestData.DRONE_BEE:
                Assert.assertEquals(startHP - TestData.DRONE_DAMAGE, hitUnit.getHP());
                break;
            case TestData.QUEEN_BEE:
                Assert.assertEquals(startHP - TestData.QUEEN_DAMAGE, hitUnit.getHP());
                break;
            case TestData.WORKER_BEE:
                Assert.assertEquals(startHP - TestData.WORKER_DAMAGE, hitUnit.getHP());
                break;
            default:
                Assert.fail("Unexpected type");
        }

        // Check Queen is dead
        if (!hitUnit.isAlive() && hitUnit.getType().getTypeName().equals(TestData.QUEEN_BEE)) {
            if (isUserHit) {
                pcQueenId = hitUnit.getUnitId();
            } else {
                userQueenId = hitUnit.getUnitId();
            }
        }
    }

    //---------         Validate is any queen dead      ----------

    private String userQueenId;
    private String pcQueenId;

    private void validateIsQueenDead() {
        if (userQueenId != null || pcQueenId != null) {
            Log.d("sdf", "sdf");
        }

        Assert.assertTrue("Game should already end, Queen is dead", userQueenId == null);
        Assert.assertTrue("Game should already end, Queen is dead", pcQueenId == null);
    }

    //---------         Validate unit number   ----------

    private void validateUnitNumber() {
        validateSideUnitNumber(pcUnitForId);
        validateSideUnitNumber(userUnitForId);
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

        Assert.assertTrue(qCount <= TestData.INITIAL_QUEEN_NUMBER);
        Assert.assertTrue(wCount <= TestData.INITIAL_WORKER_NUMBER);
        Assert.assertTrue(dCount <= TestData.INITIAL_DRONE_NUMBER);
    }


}

