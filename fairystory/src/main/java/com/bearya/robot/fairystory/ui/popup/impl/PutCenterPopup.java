package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.can.Body;
import com.bearya.robot.base.can.CanDataListener;
import com.bearya.robot.base.can.CanManager;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class PutCenterPopup extends AbsBasePopup {

    private InCenterListener centerListener;

    private final CanDataListener dataListener = new CanDataListener() {

        private boolean backOidIsInCenter = false;

        private boolean isInStartLoadCenter(int oid) {
            return oid >= 26746 && oid <= 27211;
        }

        @Override
        public void onFrontOid(int oid) {
            if (isInStartLoadCenter(oid) && backOidIsInCenter) {
                CanManager.getInstance().removeListener(dataListener);
                if (centerListener != null) {
                    getContext().runOnUiThread(() -> centerListener.inCenter());
                }
            }
        }

        @Override
        public void onBackOid(int oid) {
            backOidIsInCenter = isInStartLoadCenter(oid);
        }

        @Override
        public void onTouchBody(Body body) {

        }

    }; // 检测是否小贝在起点位置使用的数据监听器

    public PutCenterPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_put_center;
    }

    @Override
    protected void onViewInflated() {
        setWidth(850);
        withClick(R.id.put_center_root, null);
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        CanManager.getInstance().addListener(dataListener);
        MusicUtil.playAssetsAudio("card/zh/put_me_center.mp3");
        BaseApplication.getInstance().moveALittle(true);
    }

    @Override
    protected void onPopupDismiss() {
        super.onPopupDismiss();
        CanManager.getInstance().removeListener(dataListener);
    }

    public void setInCenterListener(InCenterListener centerListener) {
        this.centerListener = centerListener;
    }

    public interface InCenterListener{
        void inCenter();
    }

}