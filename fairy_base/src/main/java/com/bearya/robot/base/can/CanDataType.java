package com.bearya.robot.base.can;

/**
 * Created by yexifeng on 17/11/11.
 */

public enum CanDataType {
    UNKNOW(0),
    PRODUCT_CODE(0xf2),              //产品序列号
    TALKPEN(0X13),                  //点读笔
    TALKPEN_KEY(0X18),              //点读笔按键
    HEAD_SCANNER(0X11),             //前扫描
    TAIL_SCANNER(0X12),             //后扫描
    DICE(0X15),                     //骰子
    TOUCH_BODY(0X3),                //触摸身体
    LEFT_FRONT_OBSTACLE(0xa2),      //左障碍
    RIGHT_FRONT_OBSTACLE(0xa3),     //右障碍
    FRONT_OBSTACLE(0xa1),           //前障碍
    BACK_OBSTACLE(0xa4),            //后障碍
    FRONT_VACANT(0xa5),             //前悬空
    INC_VOICE_BTN(0X10),             //音量+按钮
    DES_VOICE_BTN(0X9),            //音量-按钮
    ELECTRICITY(0X05),              //电量信息
    POWER_BTN(0X08),                //电源按钮
    EXCEPTION(0X02),                //异常情况(如手脚被按住)
    FM(0X37),                       //FM
    VERSION_CODE(0X07),             //版本号
    HIGH_ADDRESS(0xb1),             //高位地址
    LOW_ADDRESS(0xb2),              //低位地址
    CUTOFF_CHARGING(0x06),          //断开充电
    SYNC_DANCE(0x14),               //同步跳舞
    LEFT_HAND_ARRIVE(0x50),         //左手到达
    RIGHT_HAND_ARRIVE(0x60),        //右手到达
    HEAD_ARRIVE(0x20),              //头部到达
    CAN_TEST(0xFE),                 //Can测试

    GUO_DICE1(0x1a),
    GUO_DICE2(0x1b),
    GUO_DICE3(0x1c),
    GUO_DICE4(0x1d)
    ;
    private int value;

    CanDataType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CanDataType getType(int value){
        for(CanDataType dataType:CanDataType.values()){
            if(value == dataType.getValue()){
                return dataType;
            }
        }
        return UNKNOW;
    }
}
