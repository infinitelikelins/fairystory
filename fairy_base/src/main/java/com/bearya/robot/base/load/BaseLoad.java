package com.bearya.robot.base.load;

import android.graphics.Point;
import android.graphics.Rect;

import com.bearya.robot.base.car.RobotInLoadDirect;
import com.bearya.robot.base.car.TravelPath;
import com.bearya.robot.base.protocol.ILoad;
import com.bearya.robot.base.protocol.ILock;
import com.bearya.robot.base.protocol.IntMap;
import com.bearya.robot.base.walk.Direct;
import com.bearya.robot.base.walk.ISection;
import com.bearya.robot.base.walk.Section;
import com.bearya.robot.base.walk.SectionArea;
import com.bearya.robot.base.walk.Travel;
import com.bearya.robot.base.walk.action.MoveAndDirectAction;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLoad extends IntMap implements ILoad {
    public static final int COLUMN = 30;
    public static final int ROW = 30;
    public static final int BORDER = 2; // 设置避障区的宽度

    public static final String ON_NEW_LOAD = "on_new_load";
    public static final String ON_UNLOCK_SUCCESS = "on_unlock_success";
    public static final String ON_UNLOCK_FAIL = "on_unlock_fail";

    protected ISection section;
    protected int leftEntranceOid;//左入口OId
    protected int topEntranceOid;//上入口OId
    protected int rightEntranceOid;//右入口OId
    protected int bottomEntranceOid;//下入口OId

    protected List<Rect> loadRects = new ArrayList<>();//道路矩阵

    protected ILock lock;
    private int centerOid;
    private final RobotInLoadDirect robotInLoadDirect = new RobotInLoadDirect();

    public BaseLoad(int startOid) {
        section = new Section(startOid, startOid + COLUMN * ROW - 1);
        init();
    }

    public BaseLoad(int maxColumn, int startOid) {
        section = new SectionArea(maxColumn, startOid, COLUMN, ROW);
        init();
    }

    private void init() {
        int entranceOidBorder = 1;
        Point leftEntrancePoint = new Point(entranceOidBorder, ROW / 2);
        Point topEntrancePoint = new Point(COLUMN / 2, entranceOidBorder);
        Point rightEntrancePoint = new Point(COLUMN - entranceOidBorder, ROW / 2);
        Point bottomEntrancePoint = new Point(COLUMN / 2, ROW - entranceOidBorder);
        Point centerPoint = new Point(COLUMN / 2, ROW / 2);
        leftEntranceOid = toOid(leftEntrancePoint);
        topEntranceOid = toOid(topEntrancePoint);
        rightEntranceOid = toOid(rightEntrancePoint);
        bottomEntranceOid = toOid(bottomEntrancePoint);
        centerOid = toOid(centerPoint);
    }

    @Override
    public ISection getOidSection() {
        return section;
    }

    @Override
    public int getStartOid() {
        return section.getStart();
    }

    /**
     * 引方法专门针对起点地垫设计的
     */
    public abstract TravelPath<Travel> getExitTravelPath(int entranceOid, MoveAndDirectAction action, int strategy);

    @Override
    public ILock getLock() {
        return lock;
    }

    @Override
    public int getCenterOid() {
        return centerOid;
    }

    public int getEntrance(Direct direct) {
        switch (direct) {
            case Backward:
                return bottomEntranceOid;
            case Forward:
                return topEntranceOid;
            case Right:
                return rightEntranceOid;
            case Left:
                return leftEntranceOid;
        }
        return 0;
    }

    @Override
    public void release() {
        if (lock != null) {
            lock.release();
        }
    }

    /**
     * 某点是否在避障区内
     */
    public boolean isInObstacle(Point point) {
        boolean inLoad = false;
        for (Rect rect : loadRects) {
            boolean rectCheck = rect.left < rect.right && rect.top < rect.bottom;// check for empty first
            boolean inRect = point.x >= rect.left && point.x <= rect.right && point.y >= rect.top && point.y <= rect.bottom;
            inLoad = (rectCheck && inRect) || inLoad;
        }
        return !inLoad;
    }

    public boolean hasLock() {
        return lock != null;
    }

    /**
     * 更新设备在地垫上的位置信息
     */
    public void updateRobotInLoadDirect(int headOid, int tailOid) {
        robotInLoadDirect.update(this, headOid, tailOid);
    }

    public int getLeftEntranceOid() {
        return leftEntranceOid;
    }

    public int getTopEntranceOid() {
        return topEntranceOid;
    }

    public int getRightEntranceOid() {
        return rightEntranceOid;
    }

    public int getBottomEntranceOid() {
        return bottomEntranceOid;
    }

    public RobotInLoadDirect getRobotInLoadDirect() {
        return robotInLoadDirect;
    }

    public abstract void registerPlay();


    /**
     * 入口根据方向转向
     */
    public int rotateDirectByEntrance(int faceEntranceOid, Direct direct) {
        switch (direct) {
            case Forward:
                return topEntranceOid;
            case Backward:
                if (faceEntranceOid == topEntranceOid) {
                    return bottomEntranceOid;
                } else if (faceEntranceOid == bottomEntranceOid) {
                    return topEntranceOid;
                } else if (faceEntranceOid == leftEntranceOid) {
                    return rightEntranceOid;
                } else if (faceEntranceOid == rightEntranceOid) {
                    return leftEntranceOid;
                }
                break;
            case Left:
                if (faceEntranceOid == topEntranceOid) {
                    return leftEntranceOid;
                } else if (faceEntranceOid == bottomEntranceOid) {
                    return rightEntranceOid;
                } else if (faceEntranceOid == leftEntranceOid) {
                    return bottomEntranceOid;
                } else if (faceEntranceOid == rightEntranceOid) {
                    return topEntranceOid;
                }
                break;
            case Right:
                if (faceEntranceOid == topEntranceOid) {
                    return rightEntranceOid;
                } else if (faceEntranceOid == bottomEntranceOid) {
                    return leftEntranceOid;
                } else if (faceEntranceOid == leftEntranceOid) {
                    return topEntranceOid;
                } else if (faceEntranceOid == rightEntranceOid) {
                    return bottomEntranceOid;
                }
                break;
        }
        return 0;
    }

    public int getCenterOid(int oid) {
        int value = centerOid - oid;
        int dis = distance(centerOid, oid);
        if (value > 0) {
            if (value > COLUMN) {
                return centerOid - COLUMN * (dis / 2);
            } else {
                return oid + (centerOid - oid) / 2;
            }
        } else {
            if (-value > COLUMN) {
                return centerOid + COLUMN * (dis / 2);
            } else {
                return centerOid + (oid - centerOid) / 2;
            }
        }
    }

    @Override
    public int distance(int s1, int s2) {
        return section.in(s1) && section.in(s2) ? super.distance(s1, s2) : 0;
    }
}
