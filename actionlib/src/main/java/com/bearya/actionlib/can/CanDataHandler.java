package com.bearya.actionlib.can;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yexifeng on 17/11/11.
 */

public abstract class CanDataHandler {
    private List<CanDataType> mType = new ArrayList<>();
    private Map<CanDataType,String> mHandleMethod = new HashMap<>();

    public CanDataHandler(CanDataType type) {
        addHandleType(type);
    }


    public List<CanDataType> getTypes() {
        return mType;
    }

    public CanDataHandler(HandlerInfo... handlerInfos) {
        if(handlerInfos!=null && handlerInfos.length>0){
            for(HandlerInfo handlerInfo:handlerInfos){
                registerHandlerInfo(handlerInfo);
            }
        }
    }

    public CanDataHandler() {

    }

    protected void registerHandlerInfo(HandlerInfo handlerInfo){
        addHandleType(handlerInfo.type);
        mHandleMethod.put(handlerInfo.type,handlerInfo.handleMethod);
    }

    public void addHandleType(CanDataType type){
        if(!mType.contains(type)){
            mType.add(type);
        }
    }

    public void handleData(CanDataType type,byte[] data){
        if(mHandleMethod.containsKey(type)){
            String method = mHandleMethod.get(type);
            try {
                Method m = getClass().getMethod(method,new Class[]{byte[].class});
                m.invoke(this,new Object[]{data});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else{
            handleData(data);
        }
    }

    public abstract void handleData(byte[] data);
    public abstract void terminate(Context context);
    public abstract void touchHeadReset();

    public class HandlerInfo{
        public HandlerInfo(CanDataType type, String handleMethod) {
            this.type = type;
            this.handleMethod = handleMethod;
        }

        private CanDataType type;
        private String handleMethod;
    }

    public static int get(byte[] data,int index){
        if(index<0 || index>=data.length){
            return 0;
        }
        int result = data[index];
        if(result<0){
            result = result+256;
        }
        return result;
    }

}
