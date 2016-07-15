package com.test.alex.thebeegame.model;

import com.test.alex.thebeegame.utils.Assert;

public class Unit {

    private final UnitType type;
    private final String unitId;

    private String unitName;
    private int hp;

    private boolean isAlive;

    Unit(UnitType type, int initialHP, String unitId, String name) {
        Assert._assert(type != null, "Must be set");
        Assert._assert(unitId != null, "Must be set");

        this.unitId = unitId;
        this.type = type;

        hp = initialHP;
        if(initialHP < 0) {
            isAlive = false;
        } else
        {
            isAlive = true;
        }

        setName(name);
    }

    void setName(String name) {
        if(name == null) {
            unitName = type.getTypeName();
        } else {
            unitName = name;
        }

    }

    public int getHP() {
        return hp;
    }

    public String getUnitId() {
        return unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public UnitType getType() {
        return type;
    }

    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Call if unit were hit. It will update its parameters, based on its type.
     * TODO Consider, probably this method better to move to controller.
     * TODO Reason - class became contains business logic, not only data.
     * But now it seems to be ok.
     *
     * @return {@code true} if unit still alive, otherwise {@code false}
     */
    boolean hitUnit() {
        int damage = getType().getOwnDamagePerHit();
        hp -= damage;
        if(hp <= 0) {
            isAlive = false;
        }

        return isAlive();
    }

    /**
     * Only for testcases
     * @return Copy object
     */
    public Unit copy() {
        return new Unit(type, hp, unitId, unitName);
    }
}
