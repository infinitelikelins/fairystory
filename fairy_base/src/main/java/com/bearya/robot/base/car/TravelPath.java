package com.bearya.robot.base.car;

import java.util.ArrayList;
import java.util.List;

public class TravelPath<T> {
    private List<T> travelList = new ArrayList<>();

    public void reset(){
        travelList.clear();
    }

    public void addTravel(T travel){
        travelList.add(travel);
    }

    public T nextTravel(){
        return travelList.remove(0);
    }

    public boolean isEmpty(){
        return travelList.isEmpty();
    }

    public void addTravelList(List<T> directOfMoveActionBeforeTravelList) {
        travelList.addAll(directOfMoveActionBeforeTravelList);
    }

}
