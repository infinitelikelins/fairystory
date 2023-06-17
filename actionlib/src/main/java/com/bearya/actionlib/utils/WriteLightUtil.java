package com.bearya.actionlib.utils;

import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteLightUtil {

    /**
     * 写入耳朵颜色
     *
     * @param brightness 亮度 0灭 1亮 其他
     * @param ear        左右耳 0左 1右
     * @param color      颜色 0红 1绿 2蓝
     */
    public static void writeEarLight(long brightness, int ear, int color) {
        FileOutputStream out = null;
        try {
            String name = "i2c" + ear + "_led" + color;
            if (Build.BOARD.startsWith("rk")) {
                name = (ear == 0?"left" : "right") + (color ==0?"red":color == 1?"green":"blue");
            }
            out = writeLight(name);
            out.write((brightness + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeUsbLight(long brightness,int color){
        FileOutputStream out = null;
        try {
            String name = "usb" + (color ==0?"red":color == 1?"green":"blue");
            out = writeLight(name);
            out.write((brightness + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 触摸灯
     * @param body 1右耳2左耳3头顶
     */
    public static void writeTouchedLight(long brightness,int body) {
        FileOutputStream out = null;
        try {
            String name = "LED_K" + body;
            if (Build.BOARD.startsWith("rk")) {
                name = body == 1?"rightstateled" : (body == 2?"leftstateled":"topred");
                if (brightness > 0) {
                    brightness = 255;
                }
            }
            out = writeLight(name);
            out.write((brightness + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeEarLightByColor(int color, int v, long brightness) {
        switch (color) {
            case 0://单红
                writeEarLight(0, v, 1);
                writeEarLight(0, v, 2);

                writeEarLight(brightness, v, 0);
                break;
            case 1://红绿
                writeEarLight(0, v, 2);

                writeEarLight(brightness, v, 0);
                writeEarLight(brightness, v, 1);
                break;
            case 2://单绿
                writeEarLight(0, v, 0);
                writeEarLight(0, v, 2);

                writeEarLight(brightness, v, 1);

                break;
            case 3://蓝绿
                writeEarLight(0, v, 0);

                writeEarLight(brightness, v, 1);
                writeEarLight(brightness, v, 2);
                break;
            case 4://单蓝
                writeEarLight(0, v, 0);
                writeEarLight(0, v, 1);

                writeEarLight(brightness, v, 2);
                break;
            case 5://红蓝
                writeEarLight(0, v, 1);

                writeEarLight(brightness, v, 0);
                writeEarLight(brightness, v, 2);
                break;
            case 6://红绿蓝
                writeEarLight(brightness, v, 0);
                writeEarLight(brightness, v, 1);
                writeEarLight(brightness, v, 2);
                break;
            default:
                break;
        }
    }
    public static void writeUsbLightByColor(int color, long brightness) {
        switch (color) {
            case 0://单红
                writeUsbLight(0, 1);
                writeUsbLight(0,  2);

                writeUsbLight(brightness,  0);
                break;
            case 1://红绿
                writeUsbLight(0, 2);

                writeUsbLight(brightness,  0);
                writeUsbLight(brightness,  1);
                break;
            case 2://单绿
                writeUsbLight(0,  0);
                writeUsbLight(0,  2);

                writeUsbLight(brightness,  1);

                break;
            case 3://蓝绿
                writeUsbLight(0,  0);

                writeUsbLight(brightness,  1);
                writeUsbLight(brightness,  2);
                break;
            case 4://单蓝
                writeUsbLight(0,  0);
                writeUsbLight(0,  1);

                writeUsbLight(brightness,  2);
                break;
            case 5://红蓝
                writeUsbLight(0,  1);

                writeUsbLight(brightness,  0);
                writeUsbLight(brightness,  2);
                break;
            case 6://红绿蓝
                writeUsbLight(brightness, 0);
                writeUsbLight(brightness, 1);
                writeUsbLight(brightness, 2);
                break;
            default:
                break;
        }
    }

    public static FileOutputStream writeLight(String name) throws IOException {
        return new FileOutputStream(new File("/sys/class/leds/" + name+ "/brightness"));
    }
}
