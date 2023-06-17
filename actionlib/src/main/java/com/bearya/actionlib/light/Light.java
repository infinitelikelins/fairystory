package com.bearya.actionlib.light;

import com.bearya.actionlib.utils.RobotActionManager;

/**
 * Created by yexifeng on 2018/3/3.
 */

public class Light {

    /**
     * 低电量灯光
     * @return
     */
    private static Light lowPowerLight = new Light(RobotActionManager.LightMode.STROBE, RobotActionManager.LightColor.RED,0);

    /**
     * 休眠灯光
     * @return
     */
    private static Light sleepLight = new Light(RobotActionManager.LightMode.SINGLE_BREATHE, RobotActionManager.LightColor.BLUE, 1000);

    /**
     * 常状灯光
     * @return
     */
    private static Light normalLight = new Light(RobotActionManager.LightMode.STROBE, RobotActionManager.LightColor.GREEN, 0);
    /**
     * 充电状态(充电未满)
     */
    private static Light chargeingLightWithElectricityUnFullLight = new Light(RobotActionManager.LightMode.SINGLE_BREATHE, RobotActionManager.LightColor.RED, 1000);
    /**
     * 充电状态(充电已满)
     */
    private static Light chargeingLightWithElectricityFullLight = new Light(RobotActionManager.LightMode.SINGLE_BREATHE, RobotActionManager.LightColor.GREEN, 1000);

    private static Light mCurrentLight;

    RobotActionManager.LightMode mode;
    RobotActionManager.LightColor color;
    int frequency;

    private Light(RobotActionManager.LightMode mode, RobotActionManager.LightColor color, int frequency) {
        this.mode = mode;
        this.color = color;
        this.frequency = frequency;
    }


    public static void setLowPowerLight(){
        setLight(lowPowerLight);
    }


    public static void setSleepLight(){
        setLight(sleepLight);
    }


    public static void setNormalLight() {
        setLight(normalLight);
    }


    public static void setChargeingLightWithElectricityUnFullLight() {
        setLight(chargeingLightWithElectricityUnFullLight);
    }


    public static void setChargeingLightWithElectricityFullLight() {
        setLight(chargeingLightWithElectricityFullLight);
    }

    private static void setLight(Light light){
        AutoLightHandle.getInstance().stopAutoLightThread();
        if(mCurrentLight == light){
            return;
        }
        mCurrentLight = light;
        updateLight();
    }

    /**
     * 设置下位机身体灯光
     */
    private static void updateLight(){
        if(mCurrentLight!=null) {
            setHomeBackLight(mCurrentLight);
            RobotActionManager.ctrlLeftHandLight(mCurrentLight.mode, mCurrentLight.color, mCurrentLight.frequency);
            RobotActionManager.ctrlRightHandLight(mCurrentLight.mode, mCurrentLight.color, mCurrentLight.frequency);
        }
    }

    public static void resetLight(){
        mCurrentLight = null;
    }

    /**
     * 设置前胸与后背灯光
     */
    private static void setHomeBackLight(Light light) {
        RobotActionManager.ctrlBackLight(light.mode, light.color, light.frequency);
        RobotActionManager.ctrlHomeLight(light.mode, light.color, light.frequency);
    }

    public static void setScreenOffLight(){
        resetLight();
        RobotActionManager.closeRobotLight();
        AutoLightHandle.getInstance().stopAutoLightThread();
    }






}
