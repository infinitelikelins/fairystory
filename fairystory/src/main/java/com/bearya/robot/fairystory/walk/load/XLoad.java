package com.bearya.robot.fairystory.walk.load;

import android.graphics.Rect;

import com.bearya.robot.base.car.TravelPath;
import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.walk.Travel;
import com.bearya.robot.base.walk.TravelCrossOver;
import com.bearya.robot.base.walk.TravelFace;
import com.bearya.robot.base.walk.action.DirectAction;
import com.bearya.robot.base.walk.action.MoveAndDirectAction;

import java.util.ArrayList;
import java.util.List;

public abstract class XLoad extends BaseLoad {

    public static final int TRAVEL_PATH_STRATEGY_MOVE_AND_DIRECT = 1;
    public static final int TRAVEL_PATH_STRATEGY_MOVE_ONLY = 2;

    public XLoad(int startOid) {
        super(startOid);
        loadRects.add(new Rect(BORDER, 0, COLUMN - BORDER, ROW));
        loadRects.add(new Rect(0, BORDER, COLUMN, ROW - BORDER));
    }

    public TravelPath<Travel> getExitTravelPath(int entranceOid, MoveAndDirectAction action, int strategy) {
        return computeTravelPath(entranceOid, action, strategy);
    }

    private TravelPath<Travel> computeTravelPath(int faceEntranceOid, MoveAndDirectAction action, int strategy) {
        TravelPath<Travel> travelPath = new TravelPath<>();
        if (strategy == TRAVEL_PATH_STRATEGY_MOVE_AND_DIRECT) {
            if (!action.getDirectOfMoveActionBefore().isEmpty()) {
                TravelPathInfo travelPathInfoBefore = computeTravelPathByMoveAndDirect(faceEntranceOid, action.isLast(), action.getDirectOfMoveActionBefore());
                travelPath.addTravelList(travelPathInfoBefore.travels);
                faceEntranceOid = travelPathInfoBefore.faceEntranceOid;
            } else if (action.getMoveAction() != null) {
                int centerToTargetHalfOid = getCenterOid(faceEntranceOid);
                travelPath.addTravel(new TravelFace(centerToTargetHalfOid));
                travelPath.addTravel(new TravelCrossOver(centerToTargetHalfOid));
                travelPath.addTravel(new TravelFace(faceEntranceOid));
                travelPath.addTravel(new TravelCrossOver(faceEntranceOid));
            }
        } else {
            if (!action.getDirectOfMoveActionBefore().isEmpty()) {
                TravelPathInfo travelPathInfoBefore = computeTravelPathByMoveOnly(faceEntranceOid, action.getDirectOfMoveActionBefore());
                travelPath.addTravelList(travelPathInfoBefore.travels);
                faceEntranceOid = travelPathInfoBefore.faceEntranceOid;
            } else if (action.getMoveAction() != null) {
                int centerToTargetHalfOid = getCenterOid(faceEntranceOid);
                travelPath.addTravel(new TravelFace(centerToTargetHalfOid));
                travelPath.addTravel(new TravelCrossOver(centerToTargetHalfOid));
                travelPath.addTravel(new TravelFace(faceEntranceOid));
                travelPath.addTravel(new TravelCrossOver(faceEntranceOid));
            }
        }
        return travelPath;
    }

    private TravelPathInfo computeTravelPathByMoveOnly(int faceEntranceOid, List<DirectAction> actions) {
        List<Travel> travels = new ArrayList<>();
        int converedEntranceOid = faceEntranceOid;
        if (!actions.isEmpty()) {
            for (int i = 0; i < actions.size(); i++) {
                DirectAction directAction = actions.get(i);
                converedEntranceOid = rotateDirectByEntrance(converedEntranceOid, directAction.getDirect());
                travels.add(new TravelCrossOver(getCenterOid()));
                travels.add(new TravelCrossOver(converedEntranceOid));
            }
        }
        return new TravelPathInfo(travels, converedEntranceOid);
    }

    private TravelPathInfo computeTravelPathByMoveAndDirect(int faceEntranceOid, boolean isLast, List<DirectAction> actions) {
        List<Travel> travels = new ArrayList<>();
        int converedEntranceOid = faceEntranceOid;
        if (!actions.isEmpty()) {
            for (int i = 0; i < actions.size(); i++) {
                DirectAction directAction = actions.get(i);
                converedEntranceOid = rotateDirectByEntrance(converedEntranceOid, directAction.getDirect());
                travels.add(new TravelCrossOver(getCenterOid()));
                //添加中点 开始
                int centerToTargetHalfOid = getCenterOid(converedEntranceOid);
                travels.add(new TravelFace(centerToTargetHalfOid));
                travels.add(new TravelCrossOver(centerToTargetHalfOid));
                //添加中点 结束
                travels.add(new TravelFace(converedEntranceOid));
                if (!isLast) {
                    travels.add(new TravelCrossOver(converedEntranceOid));
                }
            }
        }
        return new TravelPathInfo(travels, converedEntranceOid);
    }

    private static class TravelPathInfo {
        List<Travel> travels;
        int faceEntranceOid;

        public TravelPathInfo(List<Travel> travels, int faceEntranceOid) {
            this.travels = travels;
            this.faceEntranceOid = faceEntranceOid;
        }
    }

}