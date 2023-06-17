package com.bearya.actionlib.light;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.bearya.actionlib.can.CanDataHandler;
import com.bearya.actionlib.constants.RobotConstants;
import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.actionlib.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class ElectricityCanDataHandler extends CanDataHandler {

    public boolean isChargeing;
    public int electricity;
    public static final int LOW_POWER_ELECTRICITY = 20;
    private boolean mShowChargedUi = false;
    private ElectricityDefineMgr electricityDefineMgr;
    private boolean isScreenOff;
    private Context context;    
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case Intent.ACTION_SCREEN_ON:
                    screenOn();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    screenOff();
                    break;
            }
        }
    };

    private void screenOff() {
        isScreenOff = true;
        LightMgr.getInstance().setScreenOffState();
    }

    private void screenOn() {
        isScreenOff = false;
        controlLight();
    }

    /**
     * 充电状态
     */
    enum ChargeState{
        Broke(0xf0),//充电器断开
        Chargeing(0xf1),//充电器接入
        UnFull(0xf2),//电池未充满
        Full(0xf3),//电池充电完成
        Exception(0xf4);//充电故障
        private int value;

        ChargeState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum ElectricityState{
        Chargeing(1),//充电器接入
        Full(2),//电池充电完成
        UnChargeing(3),//充电器接入
        Exception(4);//充电故障
        private int value;

        ElectricityState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private PowerOperationDialog mPowerOperationDialog;
    protected PowerPromptDialog mPowerPromptDialog;

    protected Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showPowerOperation();
                    break;
                case 2:
                    queryPower(false);
                    break;
            }
        }
    };


    public ElectricityCanDataHandler(Context context) {
        this.context = context;
        electricityDefineMgr =new ElectricityDefineMgr();
        IntentFilter interFilter = new IntentFilter();
        interFilter.addAction(Intent.ACTION_SCREEN_ON);
        interFilter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(mScreenReceiver,interFilter);
    }

    /**
     * 充电状态数据
     * data 第10位值  F0充电器断开、F1充电器接入、F2电池未充满、F3电池充电完成、F4充电故障
     * @param data
     */
    public void onChargeStatechange(byte[] data) {
        int state = get(data,10);
        if (state == ChargeState.Chargeing.getValue()) {//充电器连接
            chargingStateChange(true);
        } else if (state == ChargeState.Broke.getValue()) {//充电器断开
            chargingStateChange(false);
            if (mPowerPromptDialog != null && mPowerPromptDialog.isShowing()) {
                mPowerPromptDialog.dismiss();
            }
        }
    }

    private void  chargingStateChange(boolean charging){
        isChargeing = charging;
        if(charging){
            if(!mShowChargedUi){
                showChargeUi(electricity);
                mShowChargedUi = true;
            }
        }else{
            mShowChargedUi = false;
        }
        LightMgr.getInstance().onChargeStateChange(charging);
        if(!charging){//拔掉电之后需要按照当前状态进行灯光还原
            controlLight();
        }
    }

    private void controlLight() {
        if(isChargeing){
            LightMgr.getInstance().setChargeingState();
        }else {
            if(LightMgr.getInstance().isLowPower()){
                LightMgr.getInstance().setLowPowerState();
            }else {
                LightMgr.getInstance().setEmotionState();
            }
        }
    }


    /**
     * 收到下位机能过Can发送来的电池信息
     */
    public void onElectricityData(byte[] data) {
        int state = get(data,10);//充电状态 1在充电
        int electricity = get(data,11);//电量信息
        onElectricity(electricity,state);
    }

    private void onElectricity(int electricity,int state){
        onElectricityChange(electricity);
        boolean isChargin = (state== ElectricityState.Chargeing.getValue() || state == ElectricityState.Full.getValue());
        if(isChargin){//充电中(用于处理,开机过程中下位机插着电源的情况
            chargingStateChange(true);
        }else{
            if(isChargeing){
                chargingStateChange(false);
            }
        }
    }


    /**
     * 电量发生变化
     */
    private void onElectricityChange(int electricity){
        this.electricity = electricity;
        LightMgr.getInstance().setElectricity(electricity);
        electricityDefineMgr.updateElectricity(electricity);
        controlLight();
    }



    @Override
    public void handleData(byte[] data) {

    }

    @Override
    public void terminate(Context context) {

    }

    @Override
    public void touchHeadReset() {
        if(mPowerOperationDialog!=null && mPowerOperationDialog.isShowing()){
            mPowerOperationDialog.dismiss();
        }
    }

    private void showChargeUi(int charge){
        RobotActionManager.stopWheel();
        //显示充电
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = charge;
        mHandler.sendMessage(msg);
    }

    public void popPowerOperation(){
        mHandler.obtainMessage(1).sendToTarget();
    }

    public void showPowerOperation() {
        SystemUtil.screenOn(context);
//        ActionDefine.screenOn(ConnectJniService.getInstance().getApplicationContext());
        if (mPowerOperationDialog == null) {
            mPowerOperationDialog = new PowerOperationDialog(context);
        }
        if (!mPowerOperationDialog.isShowing()) {
            mPowerOperationDialog.show();
        }

    }

    /**
     * 显示电量
     * @param showTTS
     */
    public void queryPower(boolean showTTS) {

    }

    public void reset(){
        Observable.just(mPowerPromptDialog)
                .filter(new Func1<PowerPromptDialog, Boolean>() {
                    @Override
                    public Boolean call(PowerPromptDialog powerPromptDialog) {
                        return mPowerPromptDialog != null && mPowerPromptDialog.isShowing();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PowerPromptDialog>() {
                    @Override
                    public void call(PowerPromptDialog powerPromptDialog) {
                        mPowerPromptDialog.dismiss();
                    }
                });
    }


    class ElectricityDefineMgr{
        public static final int NEVER = -1;//从不
        private List<ElectricityDefine> electricityDefineList = new ArrayList<>();//各段位电量定义
        private ElectricityDefine lastWarnElectricityDefine;//最后一次发出警告的电量定义区间
        private Subscription lastWarnElectricityObserver;//当次播报完电量后,根据频率值正在计时下一次的播报

        public ElectricityDefineMgr() {
            electricityDefineList.add(new ElectricityDefine(60,false,"电量还有百分之%d，小贝能量充足，请畅快使用吧！","",NEVER,NEVER));
            electricityDefineList.add(new ElectricityDefine(20,false,"电量只有百分之%d，充充电，小贝会更开心哦！","",NEVER,NEVER));
            electricityDefineList.add(new ElectricityDefine(15,true,"电量仅剩百分之%d，快帮我连上充电器吧！","警报警报！电量仅有百分之%d，快帮我连上充电器吧！",180*1000,NEVER));
            electricityDefineList.add(new ElectricityDefine(5,true,"警报警报！电量不足，请尽快充电！","当前电量仅余百分之%d，系统即将自动关机！",60*1000,NEVER));
            electricityDefineList.add(new ElectricityDefine(1,true,"电量快用完啦，再不充电就要关机了哦！","能量已耗尽，拜拜！",10*1000,5000));
        }

        /**
         * 根据电量查找电量对应的定义
         * @param electricity
         * @return
         */
        private ElectricityDefine queryElectricityDefine(int electricity){
            for(int i=0;i<electricityDefineList.size();i++){
                ElectricityDefine electricityDefine = electricityDefineList.get(i);
                if(electricity>=electricityDefine.electricity){
                    return electricityDefine;
                }
            }
            return null;
        }

        /**
         * 电量查询时的TTS
         * @param electricity
         * @return
         */
        public String queryElectricityTts(int electricity){
            ElectricityDefine electricityDefine = queryElectricityDefine(electricity);
            if(electricityDefine!=null){
                String tts = electricityDefine.queryTts;
                if(tts.contains("%d")){
                    tts = String.format(tts,electricity);
                }
                return tts;
            }
            return "";
        }

        /**
         * 更新电量
         * @param electricity
         */
        public void updateElectricity(int electricity){
            ElectricityDefine electricityDefine = queryElectricityDefine(electricity);
            if(electricityDefine!=null){
                if(!isChargeing){//不在充电中

                    warnTts(electricityDefine);//电量警告
                    if(electricityDefine.delayShutdown>0){
                        Observable.timer(electricityDefine.delayShutdown, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                if(!isChargeing){
                                    shutDown();
                                }
                            }
                        });
                    }
                }

            }
        }

        /**
         * 关机
         */
        private void shutDown(){
            SystemUtil.shutDown(context);
            RobotActionManager.send(RobotConstants.Robot_Close);
        }

        /**
         * 播报电量警告
         * @param electricityDefine
         */
        private void warnTts(ElectricityDefine electricityDefine){
            if(!TextUtils.isEmpty(electricityDefine.warnTts)){
                if(!electricityDefine.equals(lastWarnElectricityDefine)){//同一段位,执行频率警告
                    cancelLastObserver();
                    lastWarnElectricityDefine = electricityDefine;
                    postDelayNextWarnTts();
                }
            }
        }

        private void cancelLastObserver(){
            if(lastWarnElectricityObserver!=null) {
                lastWarnElectricityObserver.unsubscribe();
                lastWarnElectricityObserver = null;
            }
        }

        private void playLastElectricityDefineTts(){

        }

        /**
         * 根据播报频率进行下一次播报计时
         */
        private void postDelayNextWarnTts() {
            if(lastWarnElectricityDefine!=null) {
                playLastElectricityDefineTts();
                cancelLastObserver();
                lastWarnElectricityObserver = Observable.timer(lastWarnElectricityDefine.warnRate, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(!isChargeing){//充电中则不进行播报
                            postDelayNextWarnTts();
                        }else{
                            cancelLastObserver();
                        }
                    }
                });
            }
        }
    }

    public void release() {
        if(mScreenReceiver!=null){
            context.unregisterReceiver(mScreenReceiver);
        }
    }

    /**
     * 电量段位定义
     */
    class ElectricityDefine{
        private int electricity;//开始电量
        private boolean needStopAction;//进入区间值时是否打断当前动作执行
        private String queryTts;//电量查询提示语
        private String warnTts;//警告语,充电状态不报警
        private long warnRate;//警告频率毫秒
        private long delayShutdown;//N秒后关机

        public ElectricityDefine(int electricity, boolean needStopAction,String queryTts, String warnTts,long warnRate, long delayShutdown) {
            this.electricity = electricity;
            this.needStopAction = needStopAction;
            this.queryTts = queryTts;
            this.warnTts = warnTts;
            this.warnRate = warnRate;
            this.delayShutdown = delayShutdown;
        }
    }

}
