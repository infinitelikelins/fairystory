package com.bearya.robot.fairystory.walk.car.travel;

import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.robot.base.car.TravelPath;
import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.util.DebugUtil;
import com.bearya.robot.base.walk.DistanceInfo;
import com.bearya.robot.base.walk.Travel;
import com.bearya.robot.base.walk.TravelFace;
import com.bearya.robot.fairystory.walk.car.LoadMgr;

public class TWheelController {
    public static final int ALLOW_ERROR_ANGLE = 6;//允许误差

    public static final int MAX_SPEECH = 60;
    public static final int MIN_SPEECH = -MAX_SPEECH;
    public static final int MAKE_SURE_ANGLE_SPEED = 30;//确定角度所能准确的最高速度33
    private final WheelControllerListener listener;
    private int mLSpeed;//左轮速度
    private int mRSpeed;//右轮速度
    private Travel mTarget;
    private BaseState mState;
    private TravelPath<Travel> travelPath;
    public static final int TURN_SPEED = 35;//转弯速度

    private int faceOid;

    /**
     * 角度与速度的对应
     */
    private BaseState mWalking;

    private BaseState mArrivePassShop;

    private int anchorPoint;


    public void setTravelPath(TravelPath<Travel> travelPath) {
        this.travelPath = travelPath;
        nextTravel();
    }

    private void nextTravel() {
        if (travelPath != null && !travelPath.isEmpty()) {
            setTarget(travelPath.nextTravel());
        }
    }


    public TWheelController(WheelControllerListener listener) {
        this.listener = listener;
        initState();
    }

    private void setTarget(Travel target) {
        this.mTarget = target;
        DebugUtil.error("setTarget:%d", target.getOid());
        setState(mWalking);
    }


    public void drive() {
        walking();
        arrivePassShop();
        makeSureAngleAndTurnDirect();
    }


    private void initState() {
        mWalking = new Walking();
        mArrivePassShop = new ArriveTarget();
    }

    public int getSpeed(int speed) {
        if (speed <= MIN_SPEECH) {
            speed = MIN_SPEECH;
        }
        if (speed > MAX_SPEECH) {
            speed = MAX_SPEECH;
        }
        return speed;
    }

    public void setLeftSpeed(int leftWheelSpeech) {
        this.mLSpeed = getSpeed(leftWheelSpeech);
    }

    public void setRightSpeed(int rightWheelSpeech) {
        this.mRSpeed = getSpeed(rightWheelSpeech);
    }

    protected void goWithSpeed(int ls, int rs) {
        if (ls == 0 && rs == 0) {
            RobotActionManager.stopWheel();
        } else if (ls >= 0 && rs >= 0) {
            DebugUtil.debug("前进:LS=%d,RS=%d", ls, rs);
            RobotActionManager.goAhead(ls, rs, "编程地垫游戏");
        } else if (ls < 0 && rs >= 0) {
            DebugUtil.debug("左转:LS=%d,RS=%d", ls, rs);
            RobotActionManager.turnLeft(-ls, rs, "编程地垫游戏");
        } else if (ls >= 0 && rs < 0) {
            DebugUtil.debug("右转:LS=%d,RS=%d", ls, rs);
            RobotActionManager.turnRight(ls, -rs, "编程地垫游戏");
        }
    }


    /**
     * 前OID为A点
     * 后OID为B点
     * 目标为C点
     * 求角ABC度数
     */
    protected int computeAngle() {
        if (anchorPoint == 0) {
            anchorPoint = listener.getHeadOid();
        }
        int tailOid = anchorPoint;
        int tOid = listener.getTailOid();
        int hOid = listener.getHeadOid();
        BaseLoad load = LoadMgr.getInstance().getCurrentLoadEntrance().getLoad();
        int distanceHT = load.distance(tOid, hOid);
        DebugUtil.debug("distanceHT=%d", distanceHT);
        if (distanceHT >= 7 && distanceHT <= 9) {
            tailOid = tOid;
        } else if (load.distance(anchorPoint, hOid) < 2) {
            return 0;
        }
        return load.getAngle(listener.getHeadOid(), tailOid, mTarget.getOid());
    }

    public void stop() {
        travelPath.reset();
        setLeftSpeed(0);
        setRightSpeed(0);
        RobotActionManager.stopWheel();
    }

    public void release() {
        faceOid = 0;
        stop();
        setState(null);
    }

    private class BaseState {
        public void walking() {
        }

        public void arrivePassShop() {
        }

        public void makeSureAngleAndTurnDirect() {
        }
    }

    /**
     * 是否到达目标点
     */
    protected boolean isArrive() {
        return robotDistanceTarget().isArrive();
    }

    /**
     * 旋转角度完成
     */
    private boolean rotateComplete(int angle) {
        return (angle >= 0 && angle <= ALLOW_ERROR_ANGLE) || (angle <= 360 && angle >= (360 - ALLOW_ERROR_ANGLE));
    }

    /**
     * 全部完成
     */
    private void travelPathComplete() {
        release();
        if (listener != null) {
            listener.onCompleteTravelPath();
        }
    }

    /**
     * 是否全部完成
     */
    private boolean isTravelPathComplete() {
        return travelPath.isEmpty();
    }

    /**
     * 机器人和目标点的距离
     */
    protected DistanceInfo robotDistanceTarget() {
        int headOidDis = LoadMgr.getInstance().getCurrentLoadEntrance().getLoad().distance(listener.getHeadOid(), mTarget.getOid());
        return new DistanceInfo(headOidDis, 0);
    }

    private class Walking extends BaseState {

        @Override
        public void walking() {
            int angle = computeAngle();
            DebugUtil.debug("walking angle=%d", angle);
            if ((angle >= 358 && angle <= 360) || (angle <= 2 && angle >= 0)) {
                goWithSpeed(40, 40);
            } else {
                if (angle > 180) {
                    computeTurnLeftSpeed(360 - angle);
                } else {
                    computeTurnRightSpeed(angle);
                }
                goWithSpeed(mLSpeed, mRSpeed);
            }
            if (isArrive()) {
                setState(mArrivePassShop);
            }
        }

        private int getAccel(int angle) {
            if (angle < 20) {
                return (int) (angle * 4f);
            } else {
                return 60;
            }
        }

        private void computeTurnLeftSpeed(int angle) {
            //如果前后读头与目标点形成一个类似等腰三角形则需要进行原地转圈,不然会一直无法走到目标点
            int accel = getAccel(angle);
            DebugUtil.debug("Laccel=%d angle=%d", accel, angle);
            setLeftSpeed(MAKE_SURE_ANGLE_SPEED + accel);
            setRightSpeed(MAKE_SURE_ANGLE_SPEED - accel);
        }

        private void computeTurnRightSpeed(int angle) {

            int accel = getAccel(angle);
            DebugUtil.debug("Raccel=%d angle=%d", accel, angle);
            setLeftSpeed(MAKE_SURE_ANGLE_SPEED - accel);
            setRightSpeed(MAKE_SURE_ANGLE_SPEED + accel);
        }

    }


    private class ArriveTarget extends BaseState {
        @Override
        public void arrivePassShop() {
            anchorPoint = 0;
            if (isTravelPathComplete()) {
                if (faceOid > 0) {
                    setTarget(new TravelFace(faceOid));
                    setState(new MakeSureAngleAndTurnDirect());
                } else {
                    travelPathComplete();
                }
            } else {
                nextTravel();
            }
        }
    }

    public void setState(BaseState state) {
        this.mState = state;
    }

    private void arrivePassShop() {
        if (mState != null) {
            mState.arrivePassShop();
        }
    }

    private void walking() {
        if (mState != null) {
            mState.walking();
        }
    }

    protected void makeSureAngleAndTurnDirect() {
        if (mState != null) {
            mState.makeSureAngleAndTurnDirect();
        }
    }

    private enum TurnFlag{
        UNKNOWN,
        TURN_LEFT,
        TURN_RIGHT
    }

    public void setFaceOid(int faceOid) {
        this.faceOid = faceOid;
    }

    private class MakeSureAngleAndTurnDirect extends BaseState {

        private TurnFlag turnFlag = TurnFlag.UNKNOWN;

        @Override
        public void makeSureAngleAndTurnDirect() {
            int angle = computeAngle();
            DebugUtil.error("makeSureAgnleAndTurnDirect %d ", angle);
            if (rotateComplete(angle)) {
                travelPathComplete();
                return;
            }
            if (TurnFlag.UNKNOWN == turnFlag) {
                turnFlag = getTurnDirect(angle);
            } else {
                DebugUtil.error("向%s转", turnFlag == TurnFlag.TURN_LEFT ? "左" : "右");
                switch (turnFlag) {
                    case TURN_LEFT:
                        turnLeft();
                        break;
                    case TURN_RIGHT:
                        turnRight();
                        break;
                }
                if (rotateComplete(angle)) {//转向完成
                    travelPathComplete();
                }
            }

        }

        /**
         * 原地左转
         * isRotate 是否为原地转
         */
        protected void turnLeft() {
            goWithSpeed(-TURN_SPEED, TURN_SPEED);
        }

        /**
         * 原地右转
         * isRotate 是否为原地转
         */
        protected void turnRight() {
            goWithSpeed(TURN_SPEED, -TURN_SPEED);
        }

        private TurnFlag getTurnDirect(int angle) {
            if (headOidInLeft(angle)) {
                return TurnFlag.TURN_LEFT;
            } else {
                return TurnFlag.TURN_RIGHT;
            }
        }

        /**
         * 前OID头是否在后OID头与目标点连线的左边
         */
        private boolean headOidInLeft(int angle) {
            return angle <= 180;
        }

    }
}
