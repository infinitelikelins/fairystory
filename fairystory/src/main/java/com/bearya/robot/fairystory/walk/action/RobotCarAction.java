package com.bearya.robot.fairystory.walk.action;

import com.bearya.robot.base.walk.Direct;
import com.bearya.robot.base.walk.action.Action;
import com.bearya.robot.base.walk.action.ActionSet;
import com.bearya.robot.base.walk.action.BackwardAction;
import com.bearya.robot.base.walk.action.DirectAction;
import com.bearya.robot.base.walk.action.ForwardAction;
import com.bearya.robot.base.walk.action.LeftAction;
import com.bearya.robot.base.walk.action.MoveAndDirectAction;
import com.bearya.robot.base.walk.action.RightAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotCarAction extends Action {

    private static final Map<Integer, Direct> angleDirectMap = new HashMap<>();

    static {
        angleDirectMap.put(0, Direct.Forward);
        angleDirectMap.put(90, Direct.Right);
        angleDirectMap.put(180, Direct.Backward);
        angleDirectMap.put(270, Direct.Left);
    }

    private final List<MoveAndDirectAction> actionStack = new ArrayList<>();
    private MoveAndDirectAction currentAction;

    public RobotCarAction() {
        super(null, 0);
    }

    private void addAction(MoveAndDirectAction action) {
        actionStack.add(action);
    }

    public void set(ActionSet actionSet) {
        List<Action> actionList = mergeDirectActions(actionSet).getActionList();
        ForwardAction firstForwardAction = new ForwardAction(1);
        firstForwardAction.setId(0);
        actionList.add(0, firstForwardAction);
        MoveAndDirectAction moveAndDirectAction = new MoveAndDirectAction();
        append(moveAndDirectAction);
        for (int i = 0; i < actionList.size(); i++) {
            if (i == actionList.size() - 1) {
                moveAndDirectAction.setLast(true);
            }
            Action action = actionList.get(i);
            if (action instanceof ForwardAction) {
                ForwardAction moveAction = (ForwardAction) action;
                if (moveAction.getStep() > 1) {
                    for (int j = 0; j < moveAction.getStep(); j++) {
                        ForwardAction forwardAction = new ForwardAction(1);
                        forwardAction.setId(moveAction.getId());
                        forwardAction.setAdditional(moveAction.getAdditional());
                        moveAndDirectAction.setMoveAction(forwardAction);
                        moveAndDirectAction = new MoveAndDirectAction();
                        append(moveAndDirectAction);
                    }
                } else {
                    ForwardAction forwardAction = new ForwardAction(1);
                    forwardAction.setId(moveAction.getId());
                    forwardAction.setAdditional(moveAction.getAdditional());
                    moveAndDirectAction.setMoveAction(forwardAction);
                    moveAndDirectAction = new MoveAndDirectAction();
                    append(moveAndDirectAction);
                }
            } else {
                if (action instanceof DirectAction) {
                    MoveAndDirectAction lastMoveAndDirectAction = actionStack.get(actionStack.size() - 2);//由于默认在第一个添加了一个前进卡,所以方向卡需要挂在前一个前进动作中
                    lastMoveAndDirectAction.addMoveBeforeDirectAction((DirectAction) action);
                }
            }
        }
    }

    public ActionSet mergeDirectActions(ActionSet actionSet) {
        ActionSet merged = new ActionSet();
        List<DirectAction> actionQueue = new ArrayList<>();
        for (Action action : actionSet.getActionList()) {
            if (action instanceof DirectAction) {
                actionQueue.add((DirectAction) action);
            } else {
                if (actionQueue.size() > 0) {
                    DirectAction da = mergeAction(actionQueue);
                    merged.add(da);
                    actionQueue.clear();
                }
                merged.add(action);
            }
        }
        return merged;
    }

    private int getAngle(Direct d) {
        for (Integer angle : angleDirectMap.keySet()) {
            Direct value = angleDirectMap.get(angle);
            if (d == value) {
                return angle;
            }
        }
        return 0;
    }

    public DirectAction mergeAction(List<DirectAction> directActions) {
        int angle = 0;
        for (DirectAction action : directActions) {
            angle += getAngle(action.getDirect());
        }
        Direct direct = angleDirectMap.get(angle % 360);
        switch (direct) {
            case Left: return new LeftAction(1);
            case Right: return new RightAction(1);
            case Backward: return new BackwardAction(1);
            default: return null;
        }
    }

    @Override
    public void append(Action action) {
        addAction((MoveAndDirectAction) action);
    }

    @Override
    public int getStep() {
        int step = 0;
        for (Action action : actionStack) {
            step += action.getStep();
        }
        return step;
    }

    @Override
    public String getName() {
        return "最终动作列表";
    }

    public MoveAndDirectAction nextAction() {
        if (!actionStack.isEmpty()) {
            currentAction = actionStack.remove(0);
            return currentAction;
        }
        return null;
    }

    public MoveAndDirectAction getCurrentAction() {
        return currentAction;
    }

    public int getActionId() {
        if (currentAction != null) {
            if (currentAction.getMoveAction() != null) {
                return currentAction.getMoveAction().getId();
            }
        }
        return 0;
    }

    public boolean hasMoreAction() {
        return actionStack.size() > 1;
    }

}