package com.bearya.robot.fairystory.station;


import java.util.ArrayList;
import java.util.List;

public class Lib{
    long uuid;
    public String icon;
    String name;
    List<LibItem> items;

    public Lib() {
        items = new ArrayList<>();
    }


    public int getCount() {
        if(items!=null){
            return items.size();
        }
        return 0;
    }
}
