package com.test.alex.thebeegame.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.test.alex.thebeegame.R;
import com.test.alex.thebeegame.utils.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * In current simple implementation all bee-types are hardcoded.
 * But in more extendable solution bee-types should be described via a series of rules;
 * and bee type name should determine only its name, NOT using name as a type. So we need
 * <ul>
 *     <li>At first load a list of types<li/>
 *     <li>Than list of unit attributes<li/>
 *     <li>Load units for types, and only than load its names with other parameters<li/>
 *     <li>Attach attributes for it units<li/>
 * <ul/>
 *
 * Board data could load dynamically in the future to setup unit number and board rules.
 */
public abstract class BaseLevelLoader implements LevelLoader {

    /**
     * Allow error handling to the class user (for ex. UI)
     */
    public interface LevelLoaderErrorHandler {
        void onCantReadDataFile(Exception e);
        void onCantParseGameData(Exception e);
    }

    //  Game items

    private final String USER_UNIT_ROOT_KEY = "user";

    private final String QUEEN_BEE_KEY = "Queen Bee";
    private final String WORKER_BEE_KEY = "Worker Bee";
    private final String DRONE_BEE_KEY = "Drone Bee";

    private final int WORKER_BEE_NUM = 5;
    private final int DRONE_BEE_NUM = 8;

    private final String HP_KEY = "hp";
    private final String OWN_DAMAGE_PER_HIT_KEY = "ownDamagePerHit";


    private final String UNITS_FILE_NAME = "/units.txt";
    private final String SCENARIOS_PATH = "scenarios/";

    /**
     * Unique unit name count
     */
    private static int nameCount = 0;

    private LevelLoaderErrorHandler eHandler;

    protected String UNIT_NAME_PREFIX;

    public BaseLevelLoader(LevelLoaderErrorHandler eHandler) {
        this.eHandler = eHandler;
        UNIT_NAME_PREFIX = "";
    }

    /**
     * @param sName
     */
    private JSONObject loadUnitJson(String sName) throws JSONException {
        String path = sName + UNITS_FILE_NAME;

        JSONObject units = null;
        try {
            String str = loadFileAsString(path);
            units = new JSONObject(str);
        } catch (IOException e) {
            if (eHandler != null) {
                eHandler.onCantReadDataFile(e);
            }
        }

        return units;
    }

    protected abstract String loadFileAsString(String path) throws IOException ;

    private HashMap<String, UnitType> parseUnits(JSONObject rootObj) throws JSONException{
        //  TODO probably use Gson here

        HashMap<String, UnitType> units = new HashMap<String, UnitType>();

        JSONObject obj = rootObj.getJSONObject(USER_UNIT_ROOT_KEY);

        //  Load Queen param
        JSONObject unitObj = obj.getJSONObject(QUEEN_BEE_KEY);
        int initialHP = unitObj.getInt(HP_KEY);
        int ownDamagePerHit = unitObj.getInt(OWN_DAMAGE_PER_HIT_KEY);

        UnitType uType = new UnitType(QUEEN_BEE_KEY, ownDamagePerHit, initialHP, true);
        units.put(QUEEN_BEE_KEY, uType);

        //  Load Worker param
        unitObj = obj.getJSONObject(WORKER_BEE_KEY);
        initialHP = unitObj.getInt(HP_KEY);
        ownDamagePerHit = unitObj.getInt(OWN_DAMAGE_PER_HIT_KEY);

        uType = new UnitType(WORKER_BEE_KEY, ownDamagePerHit, initialHP, false);
        units.put(WORKER_BEE_KEY, uType);

        //  Load Drone param
        unitObj = obj.getJSONObject(DRONE_BEE_KEY);
        initialHP = unitObj.getInt(HP_KEY);
        ownDamagePerHit = unitObj.getInt(OWN_DAMAGE_PER_HIT_KEY);

        uType = new UnitType(DRONE_BEE_KEY, ownDamagePerHit, initialHP, false);
        units.put(DRONE_BEE_KEY, uType);

        return units;
    }

    private Board initBoard(@NonNull HashMap<String, UnitType> types) {
        //  Currently board values just hardcoded

        HashMap<String, Unit> opposerUnitMap = generateUnitSet(types);
        HashMap<String, Unit> userUnitMap = generateUnitSet(types);

        LinkedList<UnitType> unitTypes = new LinkedList<UnitType>();
        for (Map.Entry<String, UnitType> entry : types.entrySet()) {
            UnitType type = entry.getValue();
            unitTypes.add(type);
        }

        Board newBoard = new Board(opposerUnitMap, userUnitMap, unitTypes);
        return newBoard;
    }

    private HashMap<String, Unit> generateUnitSet(HashMap<String, UnitType> types) {
        HashMap<String, Unit> result = new HashMap<String, Unit>();

        //  Init Queen
        UnitType type = types.get(QUEEN_BEE_KEY);

        String name = nextUnitName();
        Unit unit = type.createNewInstance(name);

        result.put(unit.getUnitId(), unit);

        //  Init Worker
        type = types.get(WORKER_BEE_KEY);

        for(int i = 0; i < WORKER_BEE_NUM; i++) {
            name = nextUnitName();
            unit = type.createNewInstance(name);

            result.put(unit.getUnitId(), unit);
        }

        //  Init Drone
        type = types.get(DRONE_BEE_KEY);

        for(int i = 0; i < DRONE_BEE_NUM; i++) {
            name = nextUnitName();
            unit = type.createNewInstance(name);

            result.put(unit.getUnitId(), unit);
        }

        return result;
    }

    /**
     * Generates String unique name for unit;
     */
    private String nextUnitName() {
        return UNIT_NAME_PREFIX + nameCount++;
    }

    /**
     * @param sName level scenario name
     * @return initialized board.
     */
    public Board createBoardForScenario(String sName) {
        Assert._assert(!TextUtils.isEmpty(sName), "scenario name can't be null");

        //"scenarios/level 1/units.json"
        Board board = null;

        sName = SCENARIOS_PATH + sName;
        try {
            JSONObject units = loadUnitJson(sName);
            HashMap<String, UnitType> types = parseUnits(units);

            board = initBoard(types);
        } catch (JSONException e) {
            e.printStackTrace();
            if (eHandler != null) {
                eHandler.onCantParseGameData(e);
            }
        }

        return board;
    }
}
