package com.bearya.robot.fairystory.ui.popup;

import android.content.Context;

import com.bearya.robot.base.can.Body;
import com.bearya.robot.base.can.CanDataListener;
import com.bearya.robot.base.can.CanManager;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.ui.res.CardResource;
import com.bearya.robot.fairystory.ui.res.CardType;

public abstract class CardPopup extends AbsBasePopup {

    private CanDataListener canDataListener = new CanDataListener() {
        @Override
        public void onFrontOid(final int oid) {
            if (System.currentTimeMillis() - lastOidUpdateTimeAction > 1200L && allowOidRange(oid)) {
                lastOidUpdateTimeAction = System.currentTimeMillis();
                getContext().runOnUiThread(() -> {
                    popupWithClick(oid);
                    dismiss(true);
                });
            }
        }

        @Override
        public void onBackOid(int oid) {

        }

        @Override
        public void onTouchBody(Body body) {

        }
    };

    private Long lastOidUpdateTimeAction = 0L;
    private PopupViewClickListener popupViewClickListener;

    public CardPopup(Context context) {
        super(context);
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        CanManager.getInstance().addListener(canDataListener);
    }

    @Override
    protected void onPopupDismiss() {
        super.onPopupDismiss();
        CanManager.getInstance().removeListener(canDataListener);
        canDataListener = null;
    }

    protected boolean allowOidRange(int oid){
        return oid >= CardType.ACTION_FORWARD && oid <= CardType.ACTION_STICK;
    }

    protected final void popupWithClick(int cardType) {
        if (popupViewClickListener != null) {
            popupViewClickListener.onPopupChildViewClick(cardType);
            MusicUtil.playAssetsAudio(CardResource.cardVoice(cardType));
        }
    }

    public interface PopupViewClickListener {
        void onPopupChildViewClick(int cardType);
    }

    public final void setPopupViewClickListener(PopupViewClickListener popupViewClickListener) {
        this.popupViewClickListener = popupViewClickListener;
    }

}