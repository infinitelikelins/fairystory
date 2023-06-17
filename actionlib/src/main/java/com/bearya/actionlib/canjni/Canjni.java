package com.bearya.actionlib.canjni;

public class Canjni {

    static {
        System.loadLibrary("canjni");
    }

    public static int CAN_CTRLMODE_LOOPBACK             = 0x01;
    public static int CAN_CTRLMODE_LISTENONLY           = 0x02;
    public static int CAN_CTRLMODE_3_SAMPLES             = 0x04;
    public static int CAN_CTRLMODE_ONE_SHOT              = 0x08;
    public static int CAN_CTRLMODE_BERR_REPORTING   = 0x10;

    public static int CAN_FRAME_STANDARD             = 0x01;
    public static int CAN_FRAME_EXTEND           		  = 0x02;
    public static int CAN_FRAME_DATA                	  = 0x04;
    public static int CAN_FRAME_REMOTE              	  = 0x08;
    public static int CAN_FRAME_DEFAULT              	  = CAN_FRAME_STANDARD | CAN_FRAME_DATA;

    public static  native int  open_can(int ctrl_mode,int bitrate);
    public static  native int  close_can();
    public static  native int  read_can(byte[] buffer, int length);
    public static  native int  write_can(byte[] buffer, int length,int frame_type);
    //add by zl for nrf24l01
    public static native int  OpenRf24l01();
    public static native int  CloseRf24l01();
    public static native int  SendRfData(byte[] Txbuf ,int length);
    public static native int    ReceiveRfData(byte[] Txbuf);
}
