package com.bearya.robot.base.protocol;

import android.graphics.Point;

/**
 * Created by yexifeng on 17/11/16.
 */
public abstract class IntMap {

    private static final int MAX_MAP_COLUMN = 30;

    public abstract int getStartOid();

    /**
     * 将地垫上的OID值转为第一象限坐标点起始点1000->点P(0,0)
     */
    public Point toPoint(int oid) {
        int startOid = getStartOid();
        boolean b = oid >= startOid;
        int y = b ? (oid - startOid) / MAX_MAP_COLUMN + 1 : (oid - startOid + 1) / MAX_MAP_COLUMN - 1;
        int x = b ? (oid + (MAX_MAP_COLUMN - startOid)) % MAX_MAP_COLUMN + 1 : Math.abs((oid - (MAX_MAP_COLUMN - startOid)) % MAX_MAP_COLUMN + 1);
        return new Point(x, y);
    }

    /**
     * 将第一象限坐标点转为地垫上OID的值
     */
    public int toOid(Point point) {
        return (point.x - 1) + (MAX_MAP_COLUMN * (point.y - 1)) + getStartOid();
    }

    /**
     * 两点距离
     */
    public int distance(int s1, int s2) {
        Point p1 = toPoint(s1);
        Point p2 = toPoint(s2);
        return (int) Math.sqrt(Math.abs(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2)));
    }

    public int getAngle(int head, int tail, int target) {
        Point tailP = toPoint(tail);
        Point targetP = toPoint(target);
        Point headP = toPoint(head);
        return getAngle(headP, tailP, targetP);
    }

    /**
     * 以后OID为顶点计算前OID与目标点的夹角
     */
    private int getAngle(Point head, Point tail, Point target) {
        float dx1 = target.x - tail.x;
        float dy1 = target.y - tail.y;
        float dx2 = head.x - tail.x;
        float dy2 = head.y - tail.y;
        float c = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1) * (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);
        if (c == 0) return -1;
        int angle = (int) (Math.acos((dx1 * dx2 + dy1 * dy2) / c) * 180 / Math.PI);
        if (!headInLeft(tail, target, head)) {
            angle = 360 - angle;
        }
        return angle;
    }

    /**
     * 判断前OID在后OID与目标点连线的左边或右边
     */
    private boolean headInLeft(Point tail, Point target, Point head) {
        double tmpx = (tail.y - target.y) * head.x + (target.x - tail.x) * head.y + tail.x * target.y - target.x * tail.y;
        //当tmpx>p.x的时候，说明点在线的左边，小于在右边，等于则在线上。
        return tmpx > 0;
    }

}