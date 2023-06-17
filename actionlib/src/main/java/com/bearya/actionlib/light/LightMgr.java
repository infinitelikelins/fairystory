package com.bearya.actionlib.light;


/**
 * Created by yexifeng on 2018/2/28.
 * 灯光处理
 */

public class LightMgr {
    private static LightMgr mInstance;
    private ILight emotionState;
    private ILight sleepState;
    private ILight screenOffState;
    private ILight workState;
    private ILight lowPowerState;
    private ILight chargeingState;
    private ILight mState;

    private int mElectricity;//电量
    private boolean mCharging;

    private LightMgr() {
        init();
    }

    public static LightMgr getInstance() {
        if(mInstance==null){
            mInstance = new LightMgr();
        }
        return mInstance;
    }

    public void init (){
        emotionState = new EmotionStateLight();
        sleepState = new SleepStateLight();
        screenOffState = new ScreenOffStateLight();
        workState = new WorkStateLight();
        lowPowerState = new LowPowerStateLight();
        chargeingState = new ChargeingStateLight();
    }

    private void setState(ILight state){
        if(mCharging && state!=chargeingState){//充电时状态不可变,只能是充电状态
            return;
        }
        mState = state;
    }


    /**
     * 设置电量信息
     * @param electricity
     */
    public void setElectricity(int electricity) {
        this.mElectricity = electricity;
        if(isLowPower() && mState!=screenOffState){
            setLowPowerState();
        }
    }

    public boolean isLowPower(){
        return mElectricity< ElectricityCanDataHandler.LOW_POWER_ELECTRICITY && mElectricity>0;
    }

    public void emotionLight (){
        if(mState!=null) {
            mState.onEmotionLight();
        }
    }

    public void sleepLight (){
        if(mState!=null) {
            mState.onSleepLight();
        }
    }

    public void screenOffLight (){
        if(mState!=null) {
            mState.onScreenOffLight();
        }
    }

    public void workLight (){
        if(mState!=null) {
            mState.onWorkLight();
        }
    }

    public void lowPowerLight (){
        if(mState!=null && !isScreenOff()) {//熄屏状态下不亮灯光
            mState.onLowPowerLight();
        }
    }

    private boolean isScreenOff() {
        return false;
    }

    public void chargeingLight (){
        if(mState!=null) {
            mState.onChargeingLight();
        }
    }

    public void setEmotionState (){
        setState(emotionState);
//        Light.resetLight();
        emotionLight();
    }

    public void setSleepState (){
        setState(sleepState);
        sleepLight();
    }
    public void setScreenOffState (){
        setState(screenOffState);
        screenOffLight();
    }
    public void setWorkState (){
        setState(workState);
        Light.resetLight();
        workLight();
    }
    public void setLowPowerState (){
        setState(lowPowerState);
        lowPowerLight();
    }
    public void setChargeingState (){
        setState(chargeingState);
        chargeingLight();
    }

    private int getElectricity(){
        return mElectricity;
    }

    public void onChargeStateChange(boolean isChargeing) {
        mCharging = isChargeing;
        if(isChargeing){
            setChargeingState();
        }else{
            if(isLowPower()){
                setLowPowerState();
            }else{
                setWorkState();
            }
        }
    }

    /**
     * 表情状态灯光控制
     */
    private class EmotionStateLight implements ILight{

        @Override
        public void onEmotionLight() {
            setNormalLight();
        }

        @Override
        public void onSleepLight() {
            LightMgr.this.setSleepState();
        }

        @Override
        public void onScreenOffLight() {
            LightMgr.this.setScreenOffState();
        }

        @Override
        public void onWorkLight() {
            LightMgr.this.setWorkState();
        }

        @Override
        public void onLowPowerLight() {
            LightMgr.this.setLowPowerState();
        }

        @Override
        public void onChargeingLight() {
            LightMgr.this.setChargeingState();
        }
    }
    /**
     *
     * 休眠状态灯光控制
     */
    private class SleepStateLight implements ILight{

        @Override
        public void onEmotionLight() {
            LightMgr.this.setEmotionState();
        }

        @Override
        public void onSleepLight() {
            setSleepLight();
        }

        @Override
        public void onScreenOffLight() {
            LightMgr.this.setScreenOffState();
        }

        @Override
        public void onWorkLight() {
            LightMgr.this.setWorkState();
        }

        @Override
        public void onLowPowerLight() {
            LightMgr.this.setLowPowerState();
        }

        @Override
        public void onChargeingLight() {
            LightMgr.this.setChargeingState();
        }
    }

    /**
     * 熄屏状态
     */
    private class ScreenOffStateLight implements ILight{

        @Override
        public void onEmotionLight() {
            LightMgr.this.setEmotionState();
        }

        @Override
        public void onSleepLight() {

        }

        @Override
        public void onScreenOffLight() {
            Light.setScreenOffLight();

        }

        @Override
        public void onWorkLight() {

        }

        @Override
        public void onLowPowerLight() {
            LightMgr.this.setLowPowerState();
        }

        @Override
        public void onChargeingLight() {
            LightMgr.this.setChargeingState();
        }
    }

    /**
     * 工作状态
     */
    private class WorkStateLight implements ILight{

        @Override
        public void onEmotionLight() {
            LightMgr.this.setEmotionState();
        }

        @Override
        public void onSleepLight() {

        }

        @Override
        public void onScreenOffLight() {
            LightMgr.this.setScreenOffState();
        }

        @Override
        public void onWorkLight() {
            setNormalLight();
        }

        @Override
        public void onLowPowerLight() {
            LightMgr.this.setLowPowerState();
        }

        @Override
        public void onChargeingLight() {
            LightMgr.this.setChargeingState();
        }
    }

    /**
     * 低电量状态
     */
    private class LowPowerStateLight implements ILight{

        @Override
        public void onEmotionLight() {

        }

        @Override
        public void onSleepLight() {

        }

        @Override
        public void onScreenOffLight() {

        }

        @Override
        public void onWorkLight() {

        }

        @Override
        public void onLowPowerLight() {
            setLowPowerLight();
        }

        @Override
        public void onChargeingLight() {
            LightMgr.this.setChargeingState();
        }
    }

    /**
     * 充电状态
     */
    private class ChargeingStateLight implements ILight{

        @Override
        public void onEmotionLight() {

        }

        @Override
        public void onSleepLight() {

        }

        @Override
        public void onScreenOffLight() {

        }

        @Override
        public void onWorkLight() {

        }

        @Override
        public void onLowPowerLight() {

        }

        @Override
        public void onChargeingLight() {
            if(electricityIsFull()){
                setChargeingLightWithElectricityFull();
            }else{
                setChargeingLightWithElectricityUnFull();
            }

        }
    }

    /**
     * 是否满电
     * @return
     */
    private boolean electricityIsFull() {
        return mElectricity>98;
    }

    /**
     * 设置低电量灯光
     */
    private void setLowPowerLight(){
        Light.setLowPowerLight();
    }

    /**
     * 设置充电状态(充电未满)
     */
    private void setChargeingLightWithElectricityUnFull(){
        Light.setChargeingLightWithElectricityUnFullLight();
    }

    /**
     * 设置充电状态(充电已满)
     */
    private void setChargeingLightWithElectricityFull(){
        Light.setChargeingLightWithElectricityFullLight();
    }

    /**
     * 设置常状灯光
     */
    private void setNormalLight(){
        if(isLowPower()){
            setLowPowerLight();
        }else {
            Light.setNormalLight();
        }
    }

    /**
     * 设置休眠灯光
     */
    private void setSleepLight(){
        if(isLowPower()){
            setLowPowerLight();
        }else {
            Light.setSleepLight();
        }
    }

    


}
