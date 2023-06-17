package com.bearya.robot.base.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;


/**
 * Created by yexifeng on 17/9/1.
 */

public abstract class BYGroupView extends FrameLayout {

    public BYGroupView(Context context, int layoutResId) {
        this(context,null,layoutResId);
    }

    public BYGroupView(Context context, @Nullable AttributeSet attrs, int layoutResId) {
        this(context, attrs,0,layoutResId);
    }

    public BYGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int layoutResId) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(layoutResId, this, true);
        initSubView();
    }

    protected TextView findTextViewById(int id){
        return (TextView)findViewById(id);
    }

    protected ImageView findImageViewById(int id){
        return (ImageView)findViewById(id);
    }

    protected Button findButtonById(int id){
        return (Button)findViewById(id);
    }

    public abstract void initSubView();

}
