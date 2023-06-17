package com.bearya.robot.base.can;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yexifeng on 17/2/5.
 */

public class Messager {

    public static final String ACTION_TALKYPEN_FROM_CLIENT = "action_talkypen_from_client";
    public static final String ACTION_TALKYPEN_FROM_SERVICE = "action_talkypen_from_service";
    public static final String VALUE = "value";

    public static final int SHOW_EMOTION = 202;

    private static final String KEY_MSG_ID = "msg_id";
    public static final int APP_RESUME = 2001;

    private int msgId;
    private Bundle bundle;

    public Messager(int msgId, Bundle bundle) {
        this.msgId = msgId;
        if(bundle==null){
            bundle = new Bundle();
        }
        this.bundle = bundle;
        this.bundle.putInt(KEY_MSG_ID,msgId);
    }

    /**
     * 根据ID创建
     * 发端使用
     * @param msgId
     */
    public Messager(int msgId) {
        this(msgId,new Bundle());
    }

    /**
     *
     * @param msgId
     * @param keyValuePair key1,value1,key2,value2.....
     */
    public Messager(int msgId, Object... keyValuePair){
        this(msgId);
        bundle.putAll(arrayToBundle(keyValuePair));
    }

    public Bundle getBundle() {
        return bundle;
    }


    /**
     *将key-value成对写入到Bundle中
     * @param data key,value,key,value
     */
    public static Bundle arrayToBundle(Object... data){
        Bundle bundle = new Bundle();
        for(int i=0;i<data.length/2;i++){
            String key = String.valueOf(data[2*i]);
            Object value = data[2*i+1];
            if(value instanceof Integer){
                bundle.putInt(key,(Integer)value);
            }else if(value instanceof Float){
                bundle.putFloat(key,(Float)value);
            }else if(value instanceof Double){
                bundle.putDouble(key,(Double) value);
            }else if(value instanceof String){
                bundle.putString(key,(String)value);
            }else if(value instanceof Boolean){
                bundle.putBoolean(key,(Boolean)value);
            }else if(value instanceof Long){
                bundle.putLong(key,(Long)value);
            }else if(value instanceof ArrayList){
                bundle.putParcelableArrayList(key,(ArrayList)value);
            }else if(value  instanceof Parcelable){
                bundle.putParcelable(key,(Parcelable)value);
            }
        }
        return bundle;
    }
}
