package com.bearya.robot.fairystory.walk.car;

import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.protocol.DriveResult;

public interface ICar {

    enum DriveException{
        /**
         * 走出道路(小贝未检测到OID)
         */
        OutOfLoad,
        /**
         * 走到马路牙子(避障区)
         */
        InObstacle,
        /**
         * 将小贝放置在两张地垫上,注意不是小贝自己走到两张地垫上
         */
        PutRobotInTowLoad,
    }

    interface DriveListener{
        void onException(DriveException exception, Object param);
        void onDriveResult(DriveResult result,int stepIndex, BaseLoad load, Object param);
    }

    /**
     *按指定跳线行驶
     */
    void drive();

}