package com.bearya.robot.base.util;

import android.os.Handler;
public class RobotOidReaderRater {
    private Runnable headerEmptyRunnable = new Runnable() {
        @Override
        public void run() {
            headEmpty = true;
            if(listener!=null){
                listener.onHeadEmpty(RobotOidReaderRater.this);
                if(tailEmpty){
                    listener.onTowEmpty(RobotOidReaderRater.this);
                }
            }
        }
    };
    private Runnable tailerEmptyRunnable = new Runnable() {
        @Override
        public void run() {
            tailEmpty = true;
            if(listener!=null){
                listener.onTailEmpty(RobotOidReaderRater.this);
                if(headEmpty){
                    listener.onTowEmpty(RobotOidReaderRater.this);
                }
            }
        }
    };

    public interface OidReaderEmptyListener{
        void onHeadEmpty(RobotOidReaderRater robotOidReaderRater);
        void onTailEmpty(RobotOidReaderRater robotOidReaderRater);
        void onTowEmpty(RobotOidReaderRater robotOidReaderRater);
        void onFull();
    }

    private OidReaderEmptyListener listener;
    private long during;
    private boolean headEmpty;
    private boolean tailEmpty;
    private Handler handler = new Handler();


    public RobotOidReaderRater(int numberOfFull,OidReaderEmptyListener listener) {
        this.listener = listener;
        during = 1000/numberOfFull;
    }


    public void addHeaderOid() {
        headEmpty = false;
        handler.removeCallbacks(headerEmptyRunnable);
        handler.postDelayed(headerEmptyRunnable,during);
        if(!tailEmpty && listener!=null){
            listener.onFull();
        }
    }

    public void addTailerOid() {
        tailEmpty = false;
        handler.removeCallbacks(tailerEmptyRunnable);
        handler.postDelayed(tailerEmptyRunnable,during);
        if(!headEmpty && listener!=null){
            listener.onFull();
        }
    }

    public void release() {
        listener = null;
        if(handler!=null) {
            handler.removeCallbacks(tailerEmptyRunnable);
            handler.removeCallbacks(headerEmptyRunnable);
        }
        handler = null;
    }

    public void start(){
        addHeaderOid();
        addTailerOid();
    }
}