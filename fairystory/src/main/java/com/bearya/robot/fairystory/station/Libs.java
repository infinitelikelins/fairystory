package com.bearya.robot.fairystory.station;

import java.util.ArrayList;
import java.util.List;

public class Libs {
    List<Lib> libList;

    public Libs() {
        libList = new ArrayList<>();
    }

    public void addLib(Lib lib) {
        libList.add(lib);
    }

    public int getCount(){
        return libList.size();
    }

    public Lib get(int position) {
        return libList.get(position);
    }
}
