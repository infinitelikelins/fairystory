package com.bearya.robot.base.util;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by yexifeng
 * on 2018/10/26
 */
public class Rater {
    private int DURING = 1000;//监测1秒的数据量
    private int numberOfFull;
    private ArrayList<Long> timestampList;
    private Handler handler = new Handler();

    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            if(listener!=null){
                listener.onRateChange(0);
            }
            handler.postDelayed(this,DURING);
        }
    };

    private RateListener listener;

    public Rater(int numberOfFull){
        this.numberOfFull = numberOfFull;
        timestampList = new ArrayList<>();
    }

    public void addData(){
        handler.removeCallbacks(resetRunnable);
        long now = System.currentTimeMillis();
        timestampList.add(now);
        if(timestampList.size()>numberOfFull){
            timestampList.remove(0);
        }
        float numberOfDataInDuring = 0;
        for(int i=timestampList.size()-1;i>=0;i--){
            if(now-timestampList.get(i)<DURING){
                numberOfDataInDuring+=1;
            }else{
                break;
            }
        }
        if(listener!=null){
            int rate = (int) (numberOfDataInDuring/numberOfFull*100);
            listener.onRateChange(rate);
        }
        handler.postDelayed(resetRunnable,DURING);
    }

    public void setListener(RateListener listener) {
        this.listener = listener;
    }

    public interface RateListener{
        void onRateChange(int precent);
    }

    public void release(){
        handler.removeCallbacks(resetRunnable);
    }
}
