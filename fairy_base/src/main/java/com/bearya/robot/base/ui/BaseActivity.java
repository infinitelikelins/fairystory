package com.bearya.robot.base.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.util.DebugUtil;

import java.util.ArrayList;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    private ViewGroup mRootVie;
    private static final ArrayList<BaseActivity> mActivityList = new ArrayList<>();

    public static void finishAllActivity() {
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(BaseApplication.isEnglish ? Locale.ENGLISH : Locale.CHINESE);
        resources.updateConfiguration(configuration, displayMetrics);
        DebugUtil.error("%s onCreate", getClass().getSimpleName());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mRootVie = getRootView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mRootVie = getRootView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        add();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mActivityList.remove(this);
        super.onDestroy();
        DebugUtil.error("%s onDestroy", getClass().getSimpleName());
    }

    private void add() {
        if (!mActivityList.contains(this)) {
            mActivityList.add(this);
        }
    }

    protected final ViewGroup getRootView() {
        ViewGroup rootView = null;
        try {
            rootView = (ViewGroup) findViewById(android.R.id.content);
        } catch (Exception ignored) {
        }
        return rootView;
    }

    protected void addView(View view, Object tag) {
        if (view != null) {
            addView(view, tag, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    protected void addView(View view, Object tag, ViewGroup.LayoutParams params) {
        if (mRootVie != null && !isExist(tag)) {
            view.setTag(tag);
            mRootVie.addView(view, params);
        }
    }

    protected boolean isExist(Object tag) {
        return mRootVie.findViewWithTag(tag) != null;
    }

    public void onBackClicked(View view) {
        super.onBackPressed();
    }

    protected final void withClick(@IdRes int id, View.OnClickListener onClickListener) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
    }
    protected final void withClick(View view, View.OnClickListener onClickListener) {
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
    }

}
