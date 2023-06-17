package com.bearya.robot.base.util;

import android.content.Context;
import android.content.Intent;

import com.bearya.robot.base.can.Messager;

import java.io.File;

public class ActionDefine {

    public static final String KEY_EXTRA_BUNDLE = "key_talkypen_bundle";

    public static final String SDCARD= "storage/";
    public static final String SDCARD1= "sdcard/";
    public static final String BACK_SDCARD = SDCARD1+"byassets/";

    public enum  DATA_SOURCE_TYPE{
        ASSETS,SDCARD,NET
    }

    public static void sendMessagerToServices(Context context,Messager msg){
        sendMessagerWithAction(context, Messager.ACTION_TALKYPEN_FROM_CLIENT, msg);
    }

    private static void sendMessagerWithAction(Context context,String action,Messager msg){
        if(context!=null) {
            Intent intent = new Intent(action);
            intent.putExtra(ActionDefine.KEY_EXTRA_BUNDLE, msg.getBundle());
            context.sendBroadcast(intent);
        }
    }

    /**
     * 2019年1月31日由于某些设备解压失败报(permission denied),RK说是不能解压到sdcard/assets目录(没有权限)
     * 目前依赖SD卡资源的有1.舞蹈  2.古诗  3.知识问答   4.绘本
     */
    public static String wrapRootDir(String file){
        if(file!=null){
            String checkAssetsFolderResult = checkNewRootDirExist("assets",file);
            if(checkAssetsFolderResult!=null){
                return checkAssetsFolderResult;
            }
            String checkBeiyaFolderResult = checkNewRootDirExist("bearya",file);
            if(checkBeiyaFolderResult!=null){
                return checkBeiyaFolderResult;
            }
            return SDCARD1+file;
        }
        return file;
    }


    /**
     * 检查新根目录下是否有包含目标文件
     */
    private static String checkNewRootDirExist(String folder,String file){
        if(file.contains(folder+"/")){
            String finalFile = file;
            if(!file.startsWith(String.format("sdcard/%s/",folder)) && file.startsWith(folder+"/")){
                finalFile = BACK_SDCARD+file;
            }
            File f =new File(finalFile);
            if(f.exists()){
                return f.getAbsolutePath();
            }
        }
        return null;
    }

}
