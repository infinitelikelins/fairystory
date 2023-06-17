package com.bearya.actionlib.constants;

/**
 * Created by mars on 2016/10/31.
 */

public class RobotConstants {
    public static final String HEATHY_SONG="heathysong";
    //腿部运动控制参数：
    //    0xAA：1前进模式(可用于前进转弯)
    //    2后退模式(可用于后退转弯)
    //    3原地左转模式
    //    4原地右转模式
    //    0xBB：左腿速度0～100 速度0停转
    //    0xCC：右腿速度0～100 速度0停转"
    public static  byte[] Action_Move={(byte)0x40,0x01,0x00,0x00};
    public static  byte[] Action_Back={(byte)0x40,0x02,0x00,0x00};
    public static  byte[] Origin_Left={(byte)0x40,0x04,0x00,0x00};
    public static  byte[] Origin_Right={(byte)0x40,0x03,0x00,0x00};
    public static  byte[] Head_Shake={(byte)0x40,0x04,0x00,0x00};

    //呼吸灯控制参数：
    //    0xAA：模式0：呼吸灯关闭
    //    模式1：频闪打开
    //    模式2：呼吸打开，七种颜色的依次循环变化
    //    0xBB：颜色；0红、1黄、2绿、3青、4蓝、5紫、6白
    //    0xCC：频闪周期，单位20ms，0常亮"
    //呼吸灯模式一 射频 目前常量
//    public static  byte[] Light_Command={(byte)0x11,0x01,0x00,0x00};
    //呼吸灯模式二 七种颜色循环
    //

//    public static  byte[] Arm_Move={(byte)0x40,0x00,0x50,0x02};
    //头部运动控制参数：
    //    0xAA：头部位置0～4 位置0最低，位置2居中
    //    0xBB：运动速度0～100 无级调速 0最慢 100最快
    //    0xCC：运动次数

    //    0x00:停在目标位置不返回
    //    0x01～0xFF:从原位置到目标位置，再从目标位置回到原位置的来回次数
    public static  byte[] Head_Work={(byte)0x20,0x00,0x00,0x00};
    public static  byte[] Head_Reset={(byte)0x20,0x04,0x00,0x00};
    public static  byte[] left_Shake={(byte)0x20,0x08,0x00,0x01};
    public static  byte[] Left_Offset={(byte)0x20,0x06,0x00,0x01};
    public static  byte[] Right_Shake={(byte)0x20,0x00,0x00,0x01};
    public static  byte[] Right_Offset={(byte)0x20,0x02,0x00,0x01};

    public static  byte[] Left_Arm={(byte)0x50,0x04,0x00,0x00};
    public static  byte[] LeftArm_Reset={(byte)0x50,0x04,0x50,0x00};
    public static  byte[] Right_Arm={(byte)0x60,0x04,0x00,0x00};
    public static  byte[] RightArm_Reset={(byte)0x60,0x04,0x50,0x00};

    public static  byte[] Left_Arm_Light ={(byte)0x51,0x00,0x00,0x00};
    public static  byte[] Light_left_Reset={(byte)0x51,0x00,0x00,0x00};
    public static  byte[] Right_Arm_Light ={(byte)0x61,0x00,0x00,0x00};
    public static byte[]  Light_Right_Reset={(byte)0x61,0x00,0x00,0x00};
    public static  byte[] Home_light={(byte)0x21,0x00,0x00,0x00};

    public static  byte[] Robot_Reset={(byte)0xF0,0x01,0x00,0x00};
    public static  byte[] Robot_Close={(byte)0xF0,0x04,0x00,0x00};//下位机关机
    public static  byte[] Robot_Reboot={(byte)0xF0,0x03,0x00,0x00};//下位机重启
    public static  byte[] Robot_VERSION={(byte)0xF0,0x02,0x01,0x00};//获取版本号
    public static  byte[] Robot_Product_Code={(byte)0xF0,0x02,0x02,0x00};//获取产品生产编码
    public static  byte[] Robot_NEW_VERSION={(byte)0xF0,0x02,0x03,0x00};//新获取版本号
}

