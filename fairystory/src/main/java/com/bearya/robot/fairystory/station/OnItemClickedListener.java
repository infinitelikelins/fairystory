package com.bearya.robot.fairystory.station;

import android.view.View;

import com.bearya.robot.fairystory.R;

/**
 * description :
 * author : yexifeng
 * email : ye_xi_feng@163.com
 * date : 2019/5/8 15:03
 */
public abstract class OnItemClickedListener<T> implements View.OnClickListener {
    public abstract void onItemClicked(T data,int flag);

    @Override
    public void onClick(View view) {
        T d = (T) view.getTag();
        int flag = (int) view.getTag(R.id.item_click_flag);
        onItemClicked(d,flag);
    }
}
