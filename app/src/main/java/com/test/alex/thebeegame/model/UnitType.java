package com.test.alex.thebeegame.model;


import com.test.alex.thebeegame.utils.Assert;

public class UnitType {

    private String typeName;
    private int ownDamagePerHit;
    private int initialHP;

    private boolean isQueen; //This is an attribute, could be in attr list

    private static int idCount;

    public UnitType (String typeName, int ownDamagePerHit, int initialHP,  boolean isQueen ) {
        Assert._assert(typeName != null, "Name should be set");
        Assert._assert(ownDamagePerHit > 0, "Logic error, unit immortal");
        Assert._assert(initialHP > 0, "Not fair! We need to add resurrection first");

        this.typeName = typeName;
        this.ownDamagePerHit = ownDamagePerHit;
        this.initialHP = initialHP;
        this.isQueen = isQueen;
    }

    /**
     * Generates String unique name for unit;
     */
    private String nextUnitId() {
        return "" + idCount++;
    }

    Unit createNewInstance() {
        return createNewInstance(null);
    }

    Unit createNewInstance(String unitName) {
        return new Unit(this, initialHP, nextUnitId(), unitName);
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isQueen() {
        return isQueen;
    }

    public int getOwnDamagePerHit() {
        return ownDamagePerHit;
    }
}
