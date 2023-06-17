package com.bearya.robot.base.walk.action;

import java.util.ArrayList;
import java.util.List;

public class ActionSet {

    private List<Action> actionQueue = new ArrayList<>();

    public void add(Action action) {
        if (actionQueue == null) {
            actionQueue = new ArrayList<>();
        }
        actionQueue.add(action);
    }

    public void add(List<Action> actions){
        if (actionQueue == null) {
            actionQueue = new ArrayList<>();
        }
        actionQueue.addAll(actions);
    }

    public void clear() {
        if (actionQueue == null) {
            actionQueue = new ArrayList<>();
        } else if (actionQueue.size() > 0) {
            actionQueue.clear();
        }
    }

    public List<Action> getActionList() {
        return actionQueue;
    }



}
