package com.bearya.robot.base.walk.action;

import java.util.ArrayList;
import java.util.List;

/**
 * 移位动作后再执行多次转向
 */
public class MoveAndDirectAction extends Action {
    private List<DirectAction> directOfMoveActionBefore = new ArrayList<>();
    private ForwardAction moveAction;
    private boolean isLast;

    public MoveAndDirectAction() {
        super(null,0);
    }

    public void setMoveAction(ForwardAction moveAction) {
        this.moveAction = moveAction;
    }

    public void addMoveBeforeDirectAction(DirectAction action){
        directOfMoveActionBefore.add(action);
    }

    @Override
    public String getName() {
        return "移动+方向";
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public List<DirectAction> getDirectOfMoveActionBefore() {
        return directOfMoveActionBefore;
    }

    public ForwardAction getMoveAction() {
        return moveAction;
    }

}
