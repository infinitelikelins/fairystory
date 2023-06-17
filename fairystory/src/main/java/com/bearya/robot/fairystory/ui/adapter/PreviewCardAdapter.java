package com.bearya.robot.fairystory.ui.adapter;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardResource;
import com.bearya.robot.fairystory.ui.res.CardType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class PreviewCardAdapter extends BaseQuickAdapter<CardParentAction, BaseViewHolder> {

    public PreviewCardAdapter() {
        super(R.layout.item_preview_action);
    }

    /**
     * 正常填充界面内容
     */
    @Override
    protected void convert(final BaseViewHolder helper, CardParentAction item) {

        helper.setImageResource(R.id.parent_action, CardResource.parentImage(item.parentActionId));

        if (item.parentActionId == CardType.ACTION_DEFAULT) { // 这是没有选择行动指令的默认填充方式
            helper.setVisible(R.id.step, false);
            helper.setVisible(R.id.step_bg, false);
            helper.setVisible(R.id.child_action, false);
            helper.setVisible(R.id.link, false);
        } else {
            if (item.parentActionId == CardType.ACTION_FORWARD || item.parentActionId == CardType.ACTION_LOOP) {
                helper.setVisible(R.id.step, true);
                helper.setVisible(R.id.step_bg, true);
                helper.setText(R.id.step, String.valueOf(item.stepCount > 0 ? item.stepCount : 1));
            } else if (item.parentActionId == CardType.ACTION_CLOSURE) {
                helper.setVisible(R.id.step, false);
                helper.setVisible(R.id.step_bg, false);
                item.stepCount = 1;
            } else {
                helper.setVisible(R.id.step, false);
                helper.setVisible(R.id.step_bg, false);
                item.stepCount = 1;
            }
            if (item.parentActionId == CardType.ACTION_FORWARD) {
                boolean isShowChildAction = item.childAction != null;
                helper.setVisible(R.id.child_action, isShowChildAction);
                helper.setVisible(R.id.link, isShowChildAction);
                helper.setImageResource(R.id.child_action, CardResource.childImage(isShowChildAction ? item.childAction.childActionId : CardType.ACTION_DEFAULT));
            } else if (item.parentActionId == CardType.ACTION_FUNCTION_CALL) {
                boolean isShowChildAction = item.childAction != null;
                helper.setVisible(R.id.child_action, true);
                helper.setVisible(R.id.link, true);
                helper.setImageResource(R.id.child_action, CardResource.functionImage(isShowChildAction ? item.childAction.childActionId : CardType.ACTION_DEFAULT));
            } else {
                helper.setVisible(R.id.child_action, false);
                helper.setVisible(R.id.link, false);
            }
        }
    }

}