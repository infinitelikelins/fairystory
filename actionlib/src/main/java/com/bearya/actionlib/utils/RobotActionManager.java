package com.bearya.actionlib.utils;

import android.util.Log;
import com.bearya.actionlib.canjni.Canjni;
import com.bearya.actionlib.constants.RobotConstants;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import static com.bearya.actionlib.utils.WriteLightUtil.writeEarLight;
import static com.bearya.actionlib.utils.WriteLightUtil.writeLight;
import static com.bearya.actionlib.utils.WriteLightUtil.writeTouchedLight;
import static com.bearya.actionlib.utils.WriteLightUtil.writeUsbLight;

public class RobotActionManager {
    public static final int REPEAT_SEND_TIMES = 2;//重复发送次数
    public static final int MIN_SEND_DURING = 10;

    private static byte[] buffer = new byte[]{0x00, 0x00, 0x15, 0x55, 0x04, 0x40, 0x01, 0x00, 0x00, 0, 0, 0, 0, 0, 0, 0};
    private static boolean locked = false;

    public static final int MAX_HISTORY = 300;

    public static boolean isLocked() {
        return locked;
    }

    public static void setLocked(boolean locked) {
        RobotActionManager.locked = locked;
    }

    private static CanSender canSender = new CanSender();

    public static String toHexString(byte b) {
        return "0x" + Integer.toString(b >> 4 & 0xF, 16).toUpperCase() +
                Integer.toString(b & 0xF, 16).toUpperCase();
    }

    /**
     * 开始发送数据
     *
     * @param bytes 长度为4的byte数组
     */
    public static int send(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return -1;
        }
       return send(bytes[0],bytes[1],bytes[2],bytes[3],"");
    }


    /**
     * 开始发送数据
     */
    public static int sendForTimes(int arg1, int arg2, int arg3, int arg4) {
        for(int i=0;i<REPEAT_SEND_TIMES;i++){
            send(arg1,arg2,arg3,arg4,"");
        }
        return 1;
    }
    public static int send(int arg1, int arg2, int arg3, int arg4) {
        return send(arg1,arg2,arg3,arg4,"");
    }
    public static int send(int arg1, int arg2, int arg3, int arg4,String caller) {
        byte[] data = new byte[]{(byte) arg1,(byte) arg2,(byte) arg3,(byte) arg4};
        Caller c = new Caller(data,caller);
        canSender.add(c);
        return 1;
    }


    /**
     * 复位
     */
    public static void resetForTimes() {
        for(int i=0;i<REPEAT_SEND_TIMES;i++){
            send(RobotConstants.Robot_Reset);
        }
    }
    public static void reset() {
        resetSpeed();
        send(RobotConstants.Robot_Reset);
        send(RobotConstants.Robot_Reset);
        send(RobotConstants.Robot_Reset);
    }

    public static void resetSpeed(){
        MOVE_MODE = 0;
        MOVE_LEFT_SPEED = 0;
        MOVE_RIGHT_SPEED = 0;
    }

    /**
     * 转头
     *
     * @param position 颈部位置0～8 位置0最右，位置4居中
     * @param speed    运动速度0～100 无级调速 0最慢 100最快
     * @param times    运动次数
     *                 0x00:停在目标位置不返回
     *                 0x01～0xFF:从原位置到目标位置，再从目标位置回到原位置的来回次数
     */
    public static void turnHead(int position, int speed, int times) {
        send(0x20, position, speed, times);
    }

    /**
     * 停止轮子
     */
    public static void stopWheelForTimes() {
        for(int i=0;i<REPEAT_SEND_TIMES;i++){
            stopWheel();
        }
    }

    public static void stopWheel() {
        MOVE_MODE = 0;
        MOVE_LEFT_SPEED = 0;
        MOVE_RIGHT_SPEED = 0;
        goAhead(0x0, 0x0,"stopWhell");
    }

    private static int MOVE_MODE = 0;
    private static int MOVE_LEFT_SPEED = 0;
    private static int MOVE_RIGHT_SPEED = 0;

    /**
     * 移动
     *
     * @param mode       移动模式   1前进模式(可用于前进转弯)
     *                   2后退模式(可用于后退转弯)
     *                   3原地左转模式
     *                   4原地右转模式
     * @param leftSpeed  左腿速度0～100 速度0停转
     * @param rightSpeed 右腿速度0～100 速度0停转
     */
    public static void move(int mode, int leftSpeed, int rightSpeed,String caller) {
//        if ((mode == 0x1 || mode == 0x2)) {
//            if (leftSpeed == 0 && rightSpeed ==0) {
//                closeSensor();
//            } else {
//                openSensor();
//            }
//        }
        if(MOVE_MODE!= mode|| MOVE_LEFT_SPEED!=leftSpeed || MOVE_RIGHT_SPEED!=rightSpeed) {
            MOVE_MODE = mode;
            MOVE_LEFT_SPEED = leftSpeed;
            MOVE_RIGHT_SPEED = rightSpeed;
            send(0x40, mode, leftSpeed, rightSpeed,caller);
        }
    }

    /**
     * 前进
     *
     * @param leftSpeed  左腿速度0～100 速度0停转
     * @param rightSpeed 右腿速度0～100 速度0停转
     */
    public static void goAhead(int leftSpeed, int rightSpeed,String caller) {
        move(0x1, leftSpeed, rightSpeed,caller);
    }

    /**
     *
     * 后退
     *
     * @param leftSpeed  左腿速度0～100 速度0停转
     * @param rightSpeed 右腿速度0～100 速度0停转
     */
    public static void goBack(int leftSpeed, int rightSpeed,String caller) {
        move(0x2, leftSpeed, rightSpeed,caller);
    }

    /**
     * 原地左转
     *
     * @param leftSpeed  左腿速度0～100 速度0停转
     * @param rightSpeed 右腿速度0～100 速度0停转
     */
    public static void turnLeft(int leftSpeed, int rightSpeed,String caller) {
        move(0x3, leftSpeed, rightSpeed,caller);
    }

    /**
     * 原地右转
     *
     * @param leftSpeed  左腿速度0～100 速度0停转
     * @param rightSpeed 右腿速度0～100 速度0停转
     */
    public static void turnRight(int leftSpeed, int rightSpeed,String caller) {
        move(0x4, leftSpeed, rightSpeed,caller);
    }

    /**
     * 控制左手
     *
     * @param position 头部位置0～8 位置0最低，位置4居中
     * @param speed    运动速度0～100 无级调速 0最慢 100最快
     * @param times    运动次数
     *                 0x00:停在目标位置不返回
     *                 0x01～0xFF:从原位置到目标位置，再从目标
     */
    public static void ctrlLeftHand(int position, int speed, int times) {
        send(0x50, position, speed, times);
    }

    /**
     * 控制右手
     *
     * @param position 头部位置0～8 位置0最低，位置4居中
     * @param speed    运动速度0～100 无级调速 0最慢 100最快
     * @param times    运动次数
     *                 0x00:停在目标位置不返回
     *                 0x01～0xFF:从原位置到目标位置，再从目标
     */
    public static void ctrlRighttHand(int position, int speed, int times) {
        send(0x60, position, speed, times);
    }


    /**
     * 灯光颜色
     */
    public enum LightColor {
        RED(0x0),
        YELLO(0x1),
        GREEN(0x2),
        CYAN(0x3),
        BLUE(0x4),
        PURPLE(0x5),
        WHITE(0x6),
        RANDOM(0x7);

        private int value;

        LightColor(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 灯光模式
     */
    public enum LightMode {
        CLOSE(0X0),     //关闭
        STROBE(0X1),    //频闪
        BREATHE(0X2),   //呼吸
        SINGLE_BREATHE(0X3);   //单色呼吸

        private int value;

        LightMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 控制灯光
     *
     * @param where     灯光部位，HOME键灯	0x21
     * @param mode      模式    0：呼吸灯关闭
     *                  1：频闪打开
     *                  2：呼吸打开，七种颜色的依次循环变化
     *                  3：单色呼吸
     * @param color     颜色；0红、1黄、2绿、3青、4蓝、5紫、6白
     * @param frequency 频闪周期，单位ms，0常亮（写入底层时，单位为20ms）
     */
    public static int ctrlLight(int where, LightMode mode, LightColor color, int frequency) {
        return sendForTimes(where, mode.getValue(), color == LightColor.RANDOM ? new Random().nextInt(7) : color.getValue(), frequency / 20);
    }

    public static int ctrlLight(int where, int mode, int color, int frequency) {
        return sendForTimes(where, mode, color == 7 ? new Random().nextInt(7) : color, frequency / 20);
    }


    /**
     * 控制home键灯光
     *
     * @param mode      模式    0：呼吸灯关闭
     *                  1：频闪打开
     *                  2：呼吸打开，七种颜色的依次循环变化
     * @param color     颜色；0红、1黄、2绿、3青、4蓝、5紫、6白
     * @param frequency 频闪周期，单位ms，0常亮
     */
    public static void ctrlHomeLight(LightMode mode, LightColor color, int frequency) {
        ctrlHomeLight(mode.getValue(), color.getValue(), frequency);
    }

    public static void ctrlHomeLight(int mode, int color, int frequency) {
        int result = ctrlLight(0x21, mode, color, frequency);
    }


    /**
     * 控制左手臂灯光
     *
     * @param mode      模式    0：呼吸灯关闭
     *                  1：频闪打开
     *                  2：呼吸打开，七种颜色的依次循环变化
     * @param color     颜色；0红、1黄、2绿、3青、4蓝、5紫、6白
     * @param frequency 频闪周期，单位ms，0常亮
     */
    public static void ctrlLeftHandLight(LightMode mode, LightColor color, int frequency) {
        ctrlLeftHandLight(mode.getValue(), color.getValue(), frequency);
    }

    public static void ctrlLeftHandLight(int mode, int color, int frequency) {
        int result = ctrlLight(0x51, mode, color, frequency);
    }
    /**
     * 控制右手臂灯光
     *
     * @param mode      模式    0：呼吸灯关闭
     *                  1：频闪打开
     *                  2：呼吸打开，七种颜色的依次循环变化
     * @param color     颜色；0红、1黄、2绿、3青、4蓝、5紫、6白
     * @param frequency 频闪周期，单位ms，0常亮
     */
    public static void ctrlRightHandLight(LightMode mode, LightColor color, int frequency) {
        ctrlRightHandLight(mode.getValue(), color.getValue(), frequency);
    }

    public static void ctrlRightHandLight(int mode, int color, int frequency) {
        int result = ctrlLight(0x61, mode, color, frequency);
    }

    /**
     * 控制尾部灯光
     *
     * @param mode      模式    0：呼吸灯关闭
     *                  1：频闪打开
     *                  2：呼吸打开，七种颜色的依次循环变化
     * @param color     颜色；0红、1黄、2绿、3青、4蓝、5紫、6白
     * @param frequency 频闪周期，单位ms，0常亮
     */
    public static void ctrlBackLight(LightMode mode, LightColor color, int frequency) {
        ctrlLight(0x70, mode, color, frequency);
    }

    public static void ctrlBackLight(int mode, int color, int frequency) {
        send(0x70, mode, color, frequency / 20);
    }

    public static void closeRobotLight() {
        closeArmLight();
        closeEarLight();
        closeHomeLight();
        closeBackLight();
    }

    public static void sleepRobotLight() {
        closeEarLight();
        ctrlHomeLight(LightMode.SINGLE_BREATHE, LightColor.BLUE, 1000);
        ctrlBackLight(LightMode.SINGLE_BREATHE, LightColor.BLUE, 1000);
        closeArmLight();
    }

    public static void closeHomeLight() {
        ctrlHomeLight(LightMode.CLOSE, LightColor.GREEN, 0x00);
    }

    public static void closeBackLight() {
        ctrlBackLight(LightMode.CLOSE, LightColor.GREEN, 0x00);
    }

    public static void closeArmLight() {
        ctrlLeftHandLight(LightMode.CLOSE, LightColor.RED, 0x00);
        ctrlRightHandLight(LightMode.CLOSE, LightColor.RED, 0x00);
    }

    public static void closeUsbLight() {
        ThreadPoolManager.getRobotActionPool().execute(new Runnable() {
            @Override
            public void run() {
                //关闭左右耳呼吸灯
                writeUsbLight(0, 0);
                writeUsbLight(0,  1);
                writeUsbLight(0,  2);
            }
        });

    }
    public static void closeEarLight() {
        ThreadPoolManager.getRobotActionPool().execute(new Runnable() {
            @Override
            public void run() {
                //关闭左右耳呼吸灯
                writeEarLight(0, 0, 0);
                writeEarLight(0, 0, 1);
                writeEarLight(0, 0, 2);
                writeEarLight(0, 1, 0);
                writeEarLight(0, 1, 1);
                writeEarLight(0, 1, 2);
            }
        });

    }

    public static void openRobotLight() {
        openHomeLight();
        openHandLight();
        openBackLight();
    }

    public static void openHomeLight() {
        ctrlHomeLight(LightMode.STROBE, LightColor.GREEN, 0);
    }

    public static void openHandLight() {
        ctrlLeftHandLight(LightMode.BREATHE, LightColor.RED, 1000);
        ctrlRightHandLight(LightMode.BREATHE, LightColor.RED, 1000);
    }

    public static void openBackLight() {
        ctrlBackLight(LightMode.STROBE, LightColor.GREEN, 0);
    }

    /**
     * 重置灯光：关闭耳朵灯光，关闭手臂灯光，打开home灯光，关闭后脑三色灯
     */
    public static void resetLight() {
        //关闭头部触摸灯
        AutoLightHandle.getInstance().stopAutoLightThread();
        ThreadPoolManager.getRobotActionPool().execute(new Runnable() {
            @Override
            public void run() {
                writeTouchedLight(0, 1);
                writeTouchedLight(0, 2);
                writeTouchedLight(0, 3);
            }
        });

        closeArmLight();
        ctrlLeftHandLight(LightMode.SINGLE_BREATHE, LightColor.GREEN, 1000);
        ctrlRightHandLight(LightMode.SINGLE_BREATHE, LightColor.GREEN, 1000);
        openHomeLight();
        openBackLight();

    }

    /**
     * 耳朵位置
     */
    public enum Ear {
        LEFT(0),
        RIGHT(1);
        private int value;

        Ear(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 耳朵灯光颜色
     */
    public enum EarLightColor {
        RED(0),
        GREEN(1),
        BLUE(2);

        private int value;

        EarLightColor(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

//    /**
//     * 控制头部灯光
//     *
//     * @param color 颜色
//     * @param mode  模式，0暗 1亮
//     */
//    public static void ctrlHeadLight(int color, final int mode) {
//        final String colorStr;
//        if (color == 0) {
//            colorStr = "red";
//        } else if (color == 1) {
//            colorStr = "green";
//        } else {
//            colorStr = "blue";
//        }
//        FileOutputStream out = null;
//        try {
//            out = writeLight(colorStr);
//            out.write((mode + "\n").getBytes());
//            out.close();
//        } catch (Exception e) {
//
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }

//    /**
//     * 控制耳朵灯光
//     *
//     * @param ear        耳朵部位
//     * @param color      耳朵灯光颜色
//     * @param brightness 耳朵灯光颜色亮度，0暗，1亮
//     */
//    public static void ctrlEarLight(final Ear ear, final EarLightColor color, final int brightness) {
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(new File("/sys/class/leds/i2c" + ear.getValue() + "_led" + color.getValue() + "/brightness"));
//            out.write((brightness + "\n").getBytes());
//            out.close();
//        } catch (IOException e) {
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }

    /**
     * 控制耳朵灯光
     *
     * @param ear        耳朵部位
     * @param color      耳朵灯光颜色 0红 1绿 2蓝
     * @param brightness 耳朵灯光颜色亮度，0暗，1亮
     */
    public static void ctrlEarLight(Ear ear, int color, int brightness) {
        writeEarLight(brightness, ear.getValue(), color);
//            FileOutputStream out;
//            out = new FileOutputStream(new File("/sys/class/leds/i2c" + ear.getValue() + "_led" + color + "/brightness"));
//            out.write((brightness + "\n").getBytes());
//            out.close();

    }

    /**
     * 同时控制左右耳灯光
     *
     * @param color      颜色
     * @param brightness 亮度
     */
    public static void ctrlEarLight(final int color, final int brightness) {
        ThreadPoolManager.getRobotActionPool().execute(new Runnable() {
            @Override
            public void run() {
                FileOutputStream out = null;
                try {
                    String colorStr;
                    switch (color) {
                        case 0:
                            colorStr = "red";
                            break;
                        case 1:
                            colorStr = "green";
                            break;
                        case 2:
                        default:
                            colorStr = "blue";
                            break;
                    }
                    out = writeLight(colorStr);
                    out.write((brightness + "\n").getBytes());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });

    }

    /**
     * 所有灯光开始呼吸
     *
     * @param frequency 频闪周期，单位ms，0常亮
     */
    public static void startAllLightBreathe(int frequency) {
        ctrlHomeLight(LightMode.BREATHE, LightColor.RED, frequency);
        ctrlLeftHandLight(LightMode.BREATHE, LightColor.RED, frequency);
        ctrlRightHandLight(LightMode.BREATHE, LightColor.RED, frequency);
        ctrlBackLight(LightMode.BREATHE, LightColor.RED, frequency);
        AutoLightHandle.getInstance().startAutoLightThread(frequency);
    }

    /**
     * 所有灯光开始单色呼吸
     *
     * @param frequency 频闪周期，单位ms，0常亮
     */
    public static void startAllLightSingleBreathe(LightColor color, int frequency) {
        ctrlHomeLight(LightMode.SINGLE_BREATHE, color, frequency);
        ctrlLeftHandLight(LightMode.SINGLE_BREATHE, color, frequency);
        ctrlRightHandLight(LightMode.SINGLE_BREATHE, color, frequency);
        ctrlBackLight(LightMode.SINGLE_BREATHE, color, frequency);
        AutoLightHandle.getInstance().startAutoLightThread(color.getValue(), true, frequency, true);
    }


    private static boolean isSensorOpen = false;

    public static void switchSensor(boolean on) {
        if(on){
            openSensor();
        }else{
            closeSensor();
        }
    }
    public static void openSensor() {
        byte[] openByte = new byte[]{(byte) 0xf0, 0x07, 0x01, 0x01};
        RobotActionManager.send(openByte);
        openByte[3] = 0x2;
        RobotActionManager.send(openByte);

        isSensorOpen = true;
        Log.e("openSensor", "打开避障传感器");
    }

    public static void closeSensor() {
        byte[] openByte = new byte[]{(byte) 0xf0, 0x07, 0x00, 0x01};
        RobotActionManager.send(openByte);
        openByte[3] = 0x02;
        RobotActionManager.send(openByte);
        isSensorOpen = false;
        Log.e("openSensor", "关闭避障传感器");
    }

    public static void handShake(int speed){
        handleHand((byte)70,(byte)0x02);
        handleHand((byte)70,(byte)0x02);
        handleHand((byte)70,(byte)0x02);
    }

    public static void handStop(){
        handleHand((byte)0,(byte)0x00);
    }

    private static void handleHand(byte speed,byte flag){
        if(speed<0){
            speed =0;
        }
        if(speed>100){
            speed = 100;
        }
        byte[] bytes = new byte[]{(byte) 0xf0, 0x56, speed, flag};
        RobotActionManager.send(bytes);
    }


    public static void headerStop(){
        byte[] bytes = new byte[]{(byte) 0x22, 0x00, 0x00, 0x00};
        RobotActionManager.send(bytes);
    }
    public static void headerShake(byte times){
        if(times<0){
            times =0;
        }
        byte[] bytes = new byte[]{(byte) 0x22, 0x08, 0x50, times};
        RobotActionManager.send(bytes);
    }

    public static void sendHeartbeat(){
        byte[] bytes = new byte[]{0x73,0x01,0x01,0x00};
        send(bytes);
    }

    static class Caller{
        public byte[] data;
        public String caller;

        public Caller(byte[] data, String caller) {
            this.data = data;
            this.caller = caller;
        }
    }

   static class CanSender extends Thread{
       public CanSender() {
           start();
       }

       public void add(Caller caller){
           list.add(caller);
       }

       private Vector<Caller> list = new Vector<>();
        @Override
        public void run() {
            while (true){
                if(list.size()>0){
                    Caller caller = list.remove(0);
                    doSend(caller);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void doSend(Caller caller){

            buffer[5] = caller.data[0];
            buffer[6] = caller.data[1];
            buffer[7] = caller.data[2];
            buffer[8] = caller.data[3];
            String sData = String.format("%s,%s,%s,%s",toHexString(buffer[5]),toHexString(buffer[6]),toHexString(buffer[7]),toHexString(buffer[8]));
            System.out.println("发送Can数据 "+sData);
            int result = Canjni.write_can(buffer, 9, Canjni.CAN_FRAME_DEFAULT);
            if (result < 0 ) {//can未打开，重新打开
                int state = Canjni.open_can(Canjni.CAN_CTRLMODE_3_SAMPLES, 250000);
                if(state !=0){//开启失败
                }else{
                    Canjni.write_can(buffer, 9, Canjni.CAN_FRAME_DEFAULT);
                }

            }
        }
    }


}
