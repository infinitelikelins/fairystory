package com.bearya.robot.fairystory.walk.car;

import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.can.Body;
import com.bearya.robot.base.can.CanDataListener;
import com.bearya.robot.base.can.CanManager;
import com.bearya.robot.base.util.RobotOidReaderRater;
import com.bearya.robot.fairystory.walk.action.RobotCarAction;

public class RobotCar implements ICar, CanDataListener {

    private DriveController driveController;
    private RobotOidReaderRater robotOidReaderRater;

    private Runnable driveRunnable = new Runnable() {
        @Override
        public void run() {
            driveController.setDrive(true);
            robotOidReaderRater.start();
        }
    };

    public RobotCar(DriveListener listener) {
        driveController = new DriveController();
        driveController.setListener(listener);
        robotOidReaderRater = new RobotOidReaderRater(2,new RobotOidReaderRater.OidReaderEmptyListener() {
            @Override
            public void onHeadEmpty(RobotOidReaderRater robotOidReaderRater) {
                onTailEmpty(robotOidReaderRater);
            }

            @Override
            public void onTailEmpty(RobotOidReaderRater robotOidReaderRater) {
                if (driveController != null) {
                    driveController.onOidEmpty(robotOidReaderRater);
                }
            }

            @Override
            public void onTowEmpty(RobotOidReaderRater robotOidReaderRater) {//两读头数据都为空
                if (driveController != null) {
                    driveController.towOidEmpty(robotOidReaderRater);
                }
            }

            @Override
            public void onFull() {

            }

        });
        CanManager.getInstance().addListener(this);
    }

    @Override
    public void drive() {
        BaseApplication.getInstance().getHandler().postDelayed(driveRunnable,500);
    }


    @Override
    public void onFrontOid(int oid) {
        robotOidReaderRater.addHeaderOid();
        if(LoadMgr.getInstance().inLoad(oid)) {
            driveController.setHeadOid(oid);
        }
    }

    @Override
    public void onBackOid(int oid) {
        robotOidReaderRater.addTailerOid();
        if(LoadMgr.getInstance().inLoad(oid)) {
            driveController.setTailOid(oid);
        }
    }

    @Override
    public void onTouchBody(Body body) {

    }

    public void release(){
        driveController.release();
        robotOidReaderRater.release();
        RobotActionManager.reset();
        CanManager.getInstance().removeListener(this);
    }

    public void setRobotAction(RobotCarAction robotCarAction) {
        driveController.setRobotAction(robotCarAction);
    }

}
