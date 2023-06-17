package com.bearya.robot.fairystory.walk.car;

import android.graphics.Point;

import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.car.TravelPath;
import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.play.Director;
import com.bearya.robot.base.play.PlayListener;
import com.bearya.robot.base.protocol.DriveResult;
import com.bearya.robot.base.protocol.ILock;
import com.bearya.robot.base.protocol.Key;
import com.bearya.robot.base.protocol.KeyListener;
import com.bearya.robot.base.protocol.LockListener;
import com.bearya.robot.base.util.DebugUtil;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.RobotOidReaderRater;
import com.bearya.robot.base.walk.Direct;
import com.bearya.robot.base.walk.InObstacleReason;
import com.bearya.robot.base.walk.RobotInLoadMethod;
import com.bearya.robot.base.walk.Travel;
import com.bearya.robot.base.walk.TravelCrossOver;
import com.bearya.robot.base.walk.action.ForwardAction;
import com.bearya.robot.base.walk.action.MoveAndDirectAction;
import com.bearya.robot.fairystory.ui.res.ThemeConfig;
import com.bearya.robot.fairystory.walk.action.RobotCarAction;
import com.bearya.robot.fairystory.walk.car.drive.BaseDriveState;
import com.bearya.robot.fairystory.walk.car.drive.IState;
import com.bearya.robot.fairystory.walk.car.travel.TWheelController;
import com.bearya.robot.fairystory.walk.car.travel.WheelControllerListener;
import com.bearya.robot.fairystory.walk.load.EndLoad;
import com.bearya.robot.fairystory.walk.load.NoEntryLoad;
import com.bearya.robot.fairystory.walk.load.StartLoad;
import com.bearya.robot.fairystory.walk.load.XLoad;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

import java.util.List;

/**
 * 1.前读头进入新地垫
 */
public class DriveController {

    private static final int MAX_TOW_OID_EMPTY_TIMES = 3;
    private static final int MAX_ONE_OID_EMPTY_TIMES = 6;
    private Engine engine;
    private int headOid;
    private int tailOid;
    private Point headPoint;
    private Point tailPoint;

    /**
     * 前一块路
     */
    private BaseLoad preLoad;

    private ICar.DriveListener mListener;
    private TWheelController wheelController;
    private Runnable delayStopRunnable;
    private boolean isMoving;
    private Key mKey = null;
    private int oidEmptyTimes = 0;
    private StayInPlaceWatcher stayInPlaceWatcher = new StayInPlaceWatcher();
    private RobotCarAction robotCarAction;

    public void towOidEmpty(RobotOidReaderRater robotOidReaderRater) {
        if (mState == recognitionLoadState || mState == makeFaceStartLoadState || mState == makeFaceEndLoadState) {
            if (oidEmptyTimes < MAX_TOW_OID_EMPTY_TIMES) {
                BaseApplication.getInstance().moveALittle(oidEmptyTimes % 2 == 0);
                oidEmptyTimes++;
                robotOidReaderRater.addHeaderOid();
                return;
            } else {
                DebugUtil.debug("towOidEmpty");
                oidEmptyTimes = 0;
                setOutOfLoadState();
            }
        }
        if (System.currentTimeMillis() - lastOneOidEmptyTimeStamp > 5000) {
            oidEmptyTimes = 0;
        }
        lastOneOidEmptyTimeStamp = System.currentTimeMillis();
        if (mState == travelState) {
            if (oidEmptyTimes < MAX_TOW_OID_EMPTY_TIMES) {
                oidEmptyTimes++;
            } else {
                oidEmptyTimes = 0;
                setOutOfLoadState();
            }
        }
    }

    private long lastOneOidEmptyTimeStamp;

    public void onOidEmpty(RobotOidReaderRater robotOidReaderRater) {
        if (mState == recognitionLoadState) {
            BaseApplication.getInstance().moveALittle(oidEmptyTimes % 2 == 0);
        }
        if (System.currentTimeMillis() - lastOneOidEmptyTimeStamp > 5000) {
            oidEmptyTimes = 0;
        }
        lastOneOidEmptyTimeStamp = System.currentTimeMillis();
        if (mState == travelState || mState == exitLoadState || mState == makeFaceStartLoadState || mState == makeFaceEndLoadState) {
            if (oidEmptyTimes < MAX_ONE_OID_EMPTY_TIMES) {
                oidEmptyTimes++;
                robotOidReaderRater.addHeaderOid();
                robotOidReaderRater.addTailerOid();
            } else {
                DebugUtil.debug("onOidEmpty");
                oidEmptyTimes = 0;
                setOutOfLoadState();
            }
        }
    }

    public void setRobotAction(RobotCarAction robotCarAction) {
        this.robotCarAction = robotCarAction;
    }

    private IState mState;
    private IState recognitionLoadState;
    private IState makeFaceStartLoadState;
    private IState makeFaceEndLoadState;
    private IState newLoadState;
    private IState computeExitPathState;
    private IState unLockingState;
    private TravelState travelState;
    private IState arriveTargetState;
    private IState exitLoadState;
    private IState inObstacleState;
    private IState outOfLoadState;

    public DriveController() {
        engine = new Engine() {
            @Override
            public void update() {
                if (mState != null) {
                    mState.makeFaceStartLoad();
                }
                if (mState != null) {
                    mState.recognitionLoad();//识别道路
                }
                if (mState != null) {
                    mState.newLoad();//两个读头进入地垫
                }
                if (mState != null) {
                    mState.computeExitPath();//规划出口路径
                }
                if (mState != null) {
                    mState.unLocking();//解锁中
                }
                if (mState != null) {
                    mState.travel();//行走
                }
                if (mState != null) {
                    mState.arriveTarget();//行程完成
                }
                if (mState != null) {
                    mState.exitLoad();//走出当前地垫
                }
                if (mState != null) {
                    mState.inObstacle();//进入避障区
                }
                if (mState != null) {
                    mState.outOfLoad();//走出地垫
                }
                if (mState != null) {
                    mState.makeFaceEndLoad();//走到最后一张中心点
                }

            }
        };
        initState();
    }

    private void reset() {

        if (makeFaceStartLoadState != null) {
            makeFaceStartLoadState.reset();
        }
        if (recognitionLoadState != null) {
            recognitionLoadState.reset();
        }
        if (newLoadState != null) {
            newLoadState.reset();
        }
        if (computeExitPathState != null) {
            computeExitPathState.reset();
        }
        if (unLockingState != null) {
            unLockingState.reset();
        }
        if (travelState != null) {
            travelState.reset();
        }
        if (arriveTargetState != null) {
            arriveTargetState.reset();
        }
        if (exitLoadState != null) {
            exitLoadState.reset();
        }
        if (inObstacleState != null) {
            inObstacleState.reset();
        }
        if (outOfLoadState != null) {
            outOfLoadState.reset();
        }
        if (makeFaceEndLoadState != null) {
            makeFaceEndLoadState.reset();
        }

        if (mKey != null) {
            mKey.release();
        }
    }

    public void setState(IState state) {
        DebugUtil.debug("设置为%s状态", state == null ? "空" : state.getClass().getSimpleName());
        if (state != null) {
            state.reset();
        }
        if (mState != null) {
            mState.reset();
        }
        this.mState = state;
    }

    public void setListener(ICar.DriveListener listener) {
        this.mListener = listener;
    }

    public void setDrive(boolean drive) {
        if (drive) {
            RobotActionManager.turnHead(4, 50, 1);
            if (robotCarAction != null) {
                MoveAndDirectAction action = robotCarAction.nextAction();
                if (action != null) {
                    DebugUtil.debug("开始执行:%s", action.toString());
                }
            }
            setMakeFaceStartLoadState();
            if (!engine.isRunning()) {
                engine.start();
            }
        } else {
            engine.stop();
            dontMove("setDrive-false");
        }
    }

    private void setMakeFaceStartLoadState() {
        setState(makeFaceStartLoadState);
    }

    public void setHeadOid(int oid) {
        this.headOid = oid;
    }

    public void setTailOid(int oid) {
        this.tailOid = oid;
    }

    public void release() {
        setDrive(false);
        mListener = null;
        mState = null;
        recognitionLoadState = null;
        newLoadState = null;
        computeExitPathState = null;
        unLockingState = null;
        travelState = null;
        arriveTargetState = null;
        exitLoadState = null;
        inObstacleState = null;
        outOfLoadState = null;
        if (wheelController != null) {
            wheelController.release();
        }
        LoadMgr.getInstance().clear();
        Director.getInstance().reset();
        stopBgMusic();
        if (LoadMgr.getInstance().getCurrentLoadEntrance() != null && getCurrentLoad() != null) {
            getCurrentLoad().release();
        }
    }

    private void initState() {
        makeFaceStartLoadState = new MakeFaceStartLoadState();
        makeFaceEndLoadState = new MakeFaceEndLoadState();
        recognitionLoadState = new RecognitionLoadState();
        newLoadState = new NewLoadState();
        computeExitPathState = new ComputeExitPathState();
        unLockingState = new UnLockingState();
        travelState = new TravelState();
        arriveTargetState = new ArriveTargetState();
        exitLoadState = new ExitLoadState();
        inObstacleState = new InObstacleState();
        outOfLoadState = new OutOfLoadState();
    }

    private void setRecognitionLoadState() {
        reset();
        setState(recognitionLoadState);
    }

    private void setNewLoadState() {
        Director.getInstance().reset();
        setState(newLoadState);
    }

    private boolean isLastRobotAction() {
        return robotCarAction == null || !robotCarAction.hasMoreAction();
    }

    private void setComputeExitPathState() {
        if (!isMoving) {
            moveing();
        }
        setState(computeExitPathState);
    }

    enum GameOverReason {
        ActionComplete,
        OutOfLoad,
        NoEntry,
    }

    /**
     * 所有路径都走完
     */
    private void onGameOver(GameOverReason reason) {
        pause();
        BaseLoad baseLoad = getCurrentLoad();
        switch (reason) {
            case ActionComplete:
                if (!(baseLoad instanceof EndLoad)) {//不在终点地垫上
                    mListener.onDriveResult(DriveResult.FailLessAction, robotCarAction.getActionId(), baseLoad, null);
                } else {
                    //在终点地垫上
                    List<String> loastEquipmentLoads = LoadMgr.getInstance().getLostEquipmentLoadList((EndLoad) baseLoad);
                    if (loastEquipmentLoads == null) {//走到与之不匹配的终点
                        mListener.onDriveResult(DriveResult.FailEndLoadUnMatch, robotCarAction.getActionId(), baseLoad, LoadMgr.getInstance().getThemeEndLoad().getName());
                        return;
                    }
                    if (loastEquipmentLoads.size() > 0) {
                        mListener.onDriveResult(DriveResult.FailLostEquipmentLoads, robotCarAction.getActionId(), baseLoad, LoadMgr.getInstance().getLostEquipmentLoads());
                    } else {
                        mListener.onDriveResult(DriveResult.Success, 0, baseLoad, null);
                    }
                }
                break;
            case OutOfLoad:
                if (robotCarAction != null) {
                    mListener.onDriveResult(DriveResult.FailMoreAction, robotCarAction.getActionId() + 1, baseLoad, null);
                }
                break;
            case NoEntry:
                mListener.onDriveResult(DriveResult.FailNoEntry, robotCarAction.getActionId(), baseLoad, null);
                break;
        }
    }

    private void setUnLockingState() {
        setState(unLockingState);
    }

    private void setTravelState(TravelPath<Travel> travelPath) {
        RobotActionManager.turnHead(4, 50, 1);
        travelState.setTravelPath(travelPath);
        setState(travelState);
    }

    private void setArriveTargetState() {
        setState(arriveTargetState);
    }

    private void setExitLoadState() {
        setState(exitLoadState);
    }

    private void setInObstacleState(InObstacleReason reason) {
        ((InObstacleState) inObstacleState).setInObstacleReason(reason);
        setState(inObstacleState);

    }

    private void setOutOfLoadState() {
        setState(outOfLoadState);
    }

    class MakeFaceStartLoadState extends BaseDriveState implements WheelControllerListener {
        TWheelController wc;

        @Override
        public void makeFaceStartLoad() {
            BaseLoad headLoad = LoadMgr.getInstance().getLoad(headOid);
            if (headLoad != null && headLoad.getName().equals(StartLoad.NAME)) {
                LoadMgr.getInstance().getCurrentLoadEntrance().setLoad(headLoad);
            }
            RobotInLoadInfo robotInLoadInfo = getRobotInLoadInfo();
            if (robotInLoadInfo != null && robotInLoadInfo.state == RobotInLoadState.InObstacle) {
                setInObstacleState(InObstacleReason.DriveInObstacle);
                return;
            }
            if (wc == null) {
                wc = new TWheelController(this);
                if (headLoad instanceof StartLoad) {
                    StartLoad startLoad = (StartLoad) headLoad;
                    wc.setTravelPath(startLoad.getInitTravelPath(XLoad.TRAVEL_PATH_STRATEGY_MOVE_AND_DIRECT));
                    wc.setFaceOid(startLoad.getFaceOid());
                }
                RobotActionManager.reset();
            }
            wc.drive();
            stayInPlaceWatcher.setLocate(headOid, tailOid);
        }


        @Override
        public int getHeadOid() {
            return headOid;
        }

        @Override
        public int getTailOid() {
            return tailOid;
        }

        @Override
        public void onCompleteTravelPath() {
            DebugUtil.debug("起点地垫已较对完成");
            wc = null;
            setRecognitionLoadState();
        }
    }


    class MakeFaceEndLoadState extends BaseDriveState implements WheelControllerListener {
        TWheelController wc;

        @Override
        public void makeFaceEndLoad() {
            final BaseLoad currentLoad = LoadMgr.getInstance().getCurrentLoadEntrance().getLoad();
            if (currentLoad != null && wc == null) {
                Direct entranceDirect = currentLoad.getRobotInLoadDirect().getFaceDirect();
                LoadMgr.getInstance().getCurrentLoadEntrance().setLoad(currentLoad);
                wc = new TWheelController(this);
                TravelPath<Travel> travelPath = new TravelPath<>();
                int oid = currentLoad.getEntrance(entranceDirect);
                travelPath.addTravel(new TravelCrossOver(currentLoad.getCenterOid(oid)));
                wc.setTravelPath(travelPath);
                RobotActionManager.reset();
            }
            wc.drive();
            stayInPlaceWatcher.setLocate(headOid, tailOid);
        }


        @Override
        public int getHeadOid() {
            return headOid;
        }

        @Override
        public int getTailOid() {
            return tailOid;
        }

        @Override
        public void onCompleteTravelPath() {
            DebugUtil.debug("终地垫已较对完成");
            wc = null;
            onGameOver(GameOverReason.ActionComplete);
        }
    }


    class RecognitionLoadState extends BaseDriveState {

        /**
         * 设备不在同一地垫上
         */
        private void onRobotInTowLoad() {
            if (mListener != null) {
                exception(ICar.DriveException.PutRobotInTowLoad, null);
            }
        }

        /**
         * 设备在同一地垫上
         */
        private void onRobotInOneLoad(BaseLoad load) {
            DebugUtil.debug("前后读头在同一地垫上");
            if (load.getLock() != null) {
                //延迟停止是为了能够彻底走出当前地垫
                BaseApplication.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RobotActionManager.stopWheel();
                    }
                }, 500);
            }
            load.updateRobotInLoadDirect(headOid, tailOid);
            DebugUtil.debug("放置在%s地垫上,朝向出口为%s", load.getName(), load.getRobotInLoadDirect().getFaceDirect().name());
            LoadMgr.getInstance().getCurrentLoadEntrance().setLoad(load);
            setNewLoadState();
        }


        @Override
        public void recognitionLoad() {
            RobotInLoadInfo robotInLoadInfo = getRobotInLoadInfo();
            if (robotInLoadInfo != null) {
                BaseLoad load = robotInLoadInfo.load;
                if (mListener != null) {
                    DebugUtil.debug("RobotInLoadInfo(%d,%d) %s - %s", headOid, tailOid, load == null ? "null" : load.getName(), robotInLoadInfo.state.name());
                }
                switch (robotInLoadInfo.state) {
                    case InTowLoad:
                        if (getRobotInLoadMethod() == RobotInLoadMethod.USER_PUT) {
                            onRobotInTowLoad();//两读头分布在两张地垫上
                        }
                        break;
                    case InOneLoad:
                        onRobotInOneLoad(robotInLoadInfo.load);//在同一张地垫上
                        break;
                    case InObstacle:
                        setInObstacleState(getRobotInLoadMethod() == RobotInLoadMethod.USER_PUT ? InObstacleReason.UserPutInObstacle : InObstacleReason.DriveInObstacle);
                        break;
                    case OutOfLoad:
                        setOutOfLoadState();
                        break;
                }
            }
        }

    }

    /**
     * 移动一点点,主要试着解决
     */
    private void moveALittle() {
        RobotActionManager.goAhead(30, 30, "");
        delayStopRunnable = new Runnable() {
            @Override
            public void run() {
                delayStopRunnable = null;
                RobotActionManager.reset();
            }
        };
        BaseApplication.getInstance().getHandler().postDelayed(delayStopRunnable, 500);
    }

    private RobotInLoadInfo getRobotInLoadInfo() {
        if (headOid == 0 || tailOid == 0) {
            return null;
        }
        BaseLoad headLoad = LoadMgr.getInstance().getLoad(headOid);
        if (headLoad == null) {
            if (headOid == 0 && delayStopRunnable == null) {
                DebugUtil.debug("前 %d 无法找到对应道路,移动看一下", headOid);
                moveALittle();
            }
            return null;
        }
        BaseLoad tailLoad = LoadMgr.getInstance().getLoad(tailOid);
        if (tailLoad == null) {
            if (headOid == 0 && delayStopRunnable == null) {
                DebugUtil.debug("后 %d 无法找到对应道路,移动看一下", tailOid);
                moveALittle();
            }
            return null;
        }
        headPoint = headLoad.toPoint(headOid);
        tailPoint = tailLoad.toPoint(tailOid);
        if (headLoad != tailLoad) {//前后读头在两张不同的地垫上
            return new RobotInLoadInfo(headLoad, RobotInLoadState.InTowLoad, null);
        } else {
            int distance = headLoad.distance(headOid, tailOid);
//            DebugUtil.debug("前后读头距离%d",distance);
            if (distance > 6 && distance < 10) {//前后读头在同一张地垫上
                boolean headInObstacle = headLoad.isInObstacle(headPoint);
                boolean tailInObstacle = headLoad.isInObstacle(tailPoint);
                if (headInObstacle) {
                    return new RobotInLoadInfo(headLoad, RobotInLoadState.InObstacle, headOid);//前读头进入避障区
                }
                if (tailInObstacle) {
                    return new RobotInLoadInfo(tailLoad, RobotInLoadState.InObstacle, tailOid);//后读头进入避障区
                }
                return new RobotInLoadInfo(headLoad, RobotInLoadState.InOneLoad, headLoad);
            } else {//前后读头在两张相同的地垫上
                return new RobotInLoadInfo(headLoad, RobotInLoadState.InTowLoad, tailLoad);
            }
        }
    }

    enum RobotInLoadState {
        InObstacle,
        InTowLoad,
        InOneLoad,
        OutOfLoad,
    }

    static class RobotInLoadInfo {
        BaseLoad load;
        RobotInLoadState state;
        Object info;

        public RobotInLoadInfo(BaseLoad load, RobotInLoadState state, Object param) {
            this.load = load;
            this.state = state;
            this.info = param;
        }
    }


    /**
     * 进入新地垫
     */
    class NewLoadState extends BaseDriveState {

        @Override
        public void newLoad() {
            BaseLoad currentLoad = LoadMgr.getInstance().getCurrentLoadEntrance().getLoad();
            // 为解决卡片不足，而实现动画不能播放
            if (currentLoad != null) {
                Direct entranceDirect = currentLoad.getRobotInLoadDirect().getFaceDirect();
                int entranceOid = currentLoad.getEntrance(entranceDirect);
                if (entranceOid > 0) {
                    DebugUtil.debug("进入 - %s - 地垫", currentLoad.getName());
                    DebugUtil.debug("oid=%d,entranceOid=%d,地垫%s", tailOid, entranceOid, currentLoad.getName());
                    LoadMgr.getInstance().getCurrentLoadEntrance().setEntranceOid(entranceOid);
                    LoadMgr.getInstance().addHistory(LoadMgr.getInstance().getCurrentLoadEntrance().newInstance());
                    MoveAndDirectAction currentAction = robotCarAction.getCurrentAction();
                    if (currentAction != null) {
                        LoadMgr.getInstance().getCurrentLoadEntrance().setAction(currentAction);
                    }
                    if (currentLoad.hasLock() && !((currentLoad instanceof EndLoad) && isLastRobotAction())) {
                        dontMove("NewloadState->hasLock");
                        setUnLockingState();
                    } else {
                        setComputeExitPathState();
                    }
                }
            }
        }
    }

    /**
     * 返回小贝是如何上来
     *
     * @return RobotInLoadMethod
     */
    private RobotInLoadMethod getRobotInLoadMethod() {
        return preLoad == null ? RobotInLoadMethod.USER_PUT : RobotInLoadMethod.DRIVE;
    }

    /**
     * 计算出口路径状态
     */
    class ComputeExitPathState extends BaseDriveState {

        @Override
        public void computeExitPath() {
            if (isPerform()) {
                return;
            }
            if (isLastRobotAction()) {
                setMakeFaceEndLoadState();
                return;
            }
            TravelPath<Travel> travelPath = LoadMgr.getInstance().getCurrentLoadEntrance().computeExitPath(XLoad.TRAVEL_PATH_STRATEGY_MOVE_ONLY);
            onSureOidPath(travelPath);
            doPerform();
        }

        /**
         * 至此已经确定了从入口到出口的OID经过点
         */
        public void onSureOidPath(TravelPath<Travel> travelPath) {
            if (travelPath != null && !travelPath.isEmpty()) {
                DebugUtil.debug("选择路径:%s", travelPath.toString());
                setTravelState(travelPath);
            }
        }
    }

    private void setMakeFaceEndLoadState() {
        setState(makeFaceEndLoadState);
    }

    /**
     * 等待解锁状态
     */
    class UnLockingState extends BaseDriveState {


        @Override
        public void unLocking() {
            if (isPerform()) {
                return;
            }
            doPerform();
            final BaseLoad currentLoad = LoadMgr.getInstance().getCurrentLoadEntrance().getLoad();
            DebugUtil.debug("%s-执行解锁", currentLoad.getName());
            final ILock lock = currentLoad.getLock();
            Key key = null;
            switch (lock.getType()) {
                case Additional:
                    key = unAdditionalLock((AdditionalLock) lock, currentLoad);
                    break;
                case DirectorPlay:
                    key = unlockDirectorPlay(currentLoad);
                    break;
            }

            mKey = key;

            if (key != null) {
                key.autoUnlock(lock.getValues());
            }

        }

        private Key unAdditionalLock(final AdditionalLock lock, final BaseLoad currentLoad) {
            currentLoad.registerPlay();
            final ForwardAction action = robotCarAction.getCurrentAction().getMoveAction();
            Key key = new Key<Additional>() {
                @Override
                public void create(KeyListener<Additional> listener) {
                    if (listener != null) {
                        listener.onKey(action.getAdditional());
                    }
                }

                @Override
                public void release() {

                }

                @Override
                public void autoUnlock(Additional[] value) {

                }
            };
            if (action != null) {
                lock.unLock(key, new LockListener<Additional>() {
                    @Override
                    public void onLocking() {

                    }

                    @Override
                    public void onSuccess(Additional value, Object param) {
                        Director.getInstance().director(BaseLoad.ON_UNLOCK_SUCCESS, new PlayListener() {
                            @Override
                            public void onComplete() {
                                Director.getInstance().reset();
                                setComputeExitPathState();
                                reset();
                            }
                        });
                    }

                    @Override
                    public void onFail(final Additional value, final Object param) {
                        Director.getInstance().director(BaseLoad.ON_UNLOCK_FAIL, new PlayListener() {
                            @Override
                            public void onComplete() {
                                pause();
                                if (mListener != null) {
                                    if (value == null) {
                                        mListener.onDriveResult(DriveResult.FailObstacleAdditionalLost, action.getId(), currentLoad, null);
                                    } else {
                                        mListener.onDriveResult(DriveResult.FailObstacleAdditionalUnMatch, action.getId(), currentLoad, action.getAdditional());
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onTimeout() {

                    }

                    @Override
                    public void onMaxTimeOver() {

                    }

                });
            }
            return key;
        }

        private Key unlockDirectorPlay(final BaseLoad load) {
            load.registerPlay();
            Director.getInstance().director(BaseLoad.ON_NEW_LOAD, new PlayListener() {
                @Override
                public void onComplete() {
                    Director.getInstance().reset();
                    if (load instanceof NoEntryLoad) {//禁止通行
                        DebugUtil.error("禁止通行===========================>");
                        onGameOver(GameOverReason.NoEntry);
                    } else {
                        setComputeExitPathState();
                    }
                    reset();
                }
            });
            return null;
        }
    }


    /**
     * 寻迹状态
     */
    class TravelState extends BaseDriveState implements WheelControllerListener {
        private TravelPath<Travel> travelPath;

        @Override
        public void travel() {
            RobotInLoadInfo robotInLoadInfo = getRobotInLoadInfo();
            if (robotInLoadInfo.state == RobotInLoadState.InObstacle) {
                setInObstacleState(InObstacleReason.DriveInObstacle);
                return;
            }
            if (wheelController == null) {
                wheelController = new TWheelController(this);
                wheelController.setTravelPath(travelPath);
            }
            wheelController.drive();
            stayInPlaceWatcher.setLocate(headOid, tailOid);
        }

        public void setTravelPath(TravelPath<Travel> travelPath) {
            this.travelPath = travelPath;
        }

        @Override
        public int getHeadOid() {
            return headOid;
        }

        @Override
        public int getTailOid() {
            return tailOid;
        }

        @Override
        public void onCompleteTravelPath() {
            DebugUtil.debug("达到终点目标");
            setArriveTargetState();
        }
    }


    /**
     * 到达目标点状态
     */
    class ArriveTargetState extends BaseDriveState {

        @Override
        public void arriveTarget() {
            if (wheelController != null) {
                wheelController.stop();
                wheelController.release();
                wheelController = null;
            }
            setExitLoadState();
        }

    }

    class BaseExitLoadState extends BaseDriveState {

        protected void onReadyExit() {
            DebugUtil.debug("bug4 即将走出地垫");
            RobotActionManager.goAhead(30, 30, "走直线");
            preLoad = LoadMgr.getInstance().getCurrentLoadEntrance().getLoad();
            DebugUtil.debug("即将走出%s道路", LoadMgr.getInstance().getCurrentLoadEntrance().getLoad().getName());
        }

        protected void onExitHalf() {
            RobotActionManager.goAhead(30, 30, "走直线");
        }

        protected void onExitWhole() {
            DebugUtil.debug("bug4 完全走出地垫");
            MoveAndDirectAction currentAction = robotCarAction.getCurrentAction();
            if (currentAction != null) {
                currentAction.moveOneStep();
                DebugUtil.debug("当前地垫执行:%s", currentAction.toString());
            }
            reset();
            if (currentAction != null && currentAction.isCompleted()) {
                robotCarAction.nextAction();
            }
            setRecognitionLoadState();
        }
    }


    class ExitLoadState extends BaseExitLoadState {
        private int inTownLoadTimes = 0;

        @Override
        public void exitLoad() {
            RobotInLoadInfo robotInLoadInfo = getRobotInLoadInfo();
            if (robotInLoadInfo == null) {
                return;
            }
            if (robotInLoadInfo.state == RobotInLoadState.InObstacle) {
                RobotActionManager.stopWheel();
                setInObstacleState(InObstacleReason.LoadConnectException);
                return;
            }
            BaseLoad hLoad = LoadMgr.getInstance().getLoad(headOid);
            BaseLoad tLoad = LoadMgr.getInstance().getLoad(tailOid);
            int dis = 0;
            if (hLoad != null) {
                dis = hLoad.distance(headOid, tailOid);
            }
            DebugUtil.debug("bug4 dis=%d,hLoad=%s,tLoad=%s", dis, hLoad == null ? "null" : hLoad.getName(), tLoad == null ? "null" : tLoad.getName());
            if ((dis < 7 || dis > 10) || (hLoad != tLoad)) {
                inTownLoadTimes++;
                if (inTownLoadTimes == 1) {
                    onExitHalf();
                }
            } else {
                if (inTownLoadTimes > 5) {//完全走出地垫
                    onExitWhole();
                } else {
                    onReadyExit();
                }
            }
            stayInPlaceWatcher.setLocate(headOid, tailOid);
        }

        @Override
        public void reset() {
            super.reset();
            inTownLoadTimes = 0;
        }
    }

    /**
     * 身处避障区状态
     */
    class InObstacleState extends BaseDriveState {
        private InObstacleReason reason;


        @Override
        public void inObstacle() {
            if (!isPerform() && mListener != null) {
                exception(ICar.DriveException.InObstacle, reason);
            }
            doPerform();
        }


        public void setInObstacleReason(InObstacleReason reason) {
            this.reason = reason;
        }
    }

    /**
     * 走出地垫状态
     */
    class OutOfLoadState extends BaseDriveState {
        @Override
        public void outOfLoad() {
            dontMove("OutOfLoadState");
            if (!isPerform() && mListener != null) {
                onGameOver(GameOverReason.OutOfLoad);
                preLoad = null;
            }
            doPerform();
        }
    }


    public BaseLoad getCurrentLoad() {
        return LoadMgr.getInstance().getCurrentLoadEntrance().getLoad();
    }

    private void moveing() {
        DebugUtil.error("moveing");
        isMoving = true;
        Director.getInstance().playMovingEmotion();
        MusicUtil.playTravelBgMusic(ThemeConfig.travelBgm());
        RobotActionManager.handShake(80);
    }

    private void dontMove(String caller) {
        DebugUtil.error("dontMove caller=%s", caller);
        isMoving = false;
        stopBgMusic();
        RobotActionManager.reset();
    }

    private void stopBgMusic() {
        MusicUtil.stopBgMusic();
    }

    private void exception(ICar.DriveException exception, Object param) {
        pause();
        if (mListener != null) {
            mListener.onException(exception, param);
        }
    }

    private void pause() {
        BaseLoad load = getCurrentLoad();
        if (load != null) {
            load.release();
        }
        if (wheelController != null) {
            wheelController.release();
            wheelController = null;
        }
        setDrive(false);
        setState(null);
        dontMove("pause");
    }

    class StayInPlaceWatcher {
        public static final long WATCHER_DURING = 2000;
        int lastHeadOidLocate;
        int lastTailOidLocate;
        long lastWatcherTimestamp;

        public void setLocate(int hOid, int tOid) {
            if (System.currentTimeMillis() - lastWatcherTimestamp > WATCHER_DURING) {
                if (isStayInPlace(hOid, tOid)) {
                    RobotActionManager.resetSpeed();
                    RobotActionManager.goAhead(30, 30, "");
                    DebugUtil.debug("补发前进指令");
                }
                lastWatcherTimestamp = System.currentTimeMillis();
            }
        }

        private boolean isStayInPlace(int hOid, int tOid) {
            if (hOid == 0 || tOid == 0) {
                return false;
            }
            boolean isStayInPlace = false;
            if ((hOid == lastHeadOidLocate && tOid == lastTailOidLocate)) {
                isStayInPlace = true;
            }
            if (!isStayInPlace && hOid == lastHeadOidLocate) {
                BaseLoad tLoad = LoadMgr.getInstance().getLoad(hOid);
                int d = tLoad.distance(hOid, lastHeadOidLocate);
                if (d <= 1) {
                    isStayInPlace = true;
                }
            }
            if (!isStayInPlace && tOid == lastTailOidLocate) {
                BaseLoad tLoad = LoadMgr.getInstance().getLoad(tOid);
                int d = tLoad.distance(tOid, lastTailOidLocate);
                if (d <= 1) {
                    isStayInPlace = true;
                }
            }
            lastHeadOidLocate = hOid;
            lastTailOidLocate = tOid;
            return isStayInPlace;
        }
    }

}