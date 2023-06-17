package com.bearya.robot.base.walk;

import java.util.Locale;

public class Travel {

    private int oid;

    public Travel(int oid) {
        this.oid = oid;
    }

    public int getOid() {
        return oid;
    }

    @Override
    public String toString() {
        return String.format(Locale.CHINA, "点 %d", oid);
    }

}
