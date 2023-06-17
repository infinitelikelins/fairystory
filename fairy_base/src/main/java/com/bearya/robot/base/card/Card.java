package com.bearya.robot.base.card;


public abstract class Card {
    private int oid;

    public Card(int oid) {
        this.oid = oid;
    }

    public int getOid() {
        return oid;
    }
}
