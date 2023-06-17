package com.bearya.robot.fairystory.ui.adapter;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardResource;
import com.bearya.robot.fairystory.ui.res.CardType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 界面上的行动指令卡片适配器
 */
public class CardActionsAdapter extends BaseQuickAdapter<CardParentAction, BaseViewHolder> {

    public CardActionsAdapter() {
        super(R.layout.item_card_action);
    }

    /**
     * 正常填充界面内容
     */
    @Override
    protected void convert(final BaseViewHolder helper, CardParentAction item) {

        helper.setText(R.id.position, String.valueOf(helper.getAdapterPosition() + 1));

        helper.getView(R.id.parent_action).setScaleX(1.2f);
        helper.getView(R.id.parent_action).setScaleY(1.2f);
        helper.setImageResource(R.id.parent_action, CardResource.parentImage(item.parentActionId));
        helper.addOnClickListener(R.id.parent_action);
        helper.addOnLongClickListener(R.id.parent_action);

        if (item.parentActionId == CardType.ACTION_DEFAULT) { // 这是没有选择行动指令的默认填充方式
            helper.setVisible(R.id.step, false);
            helper.setVisible(R.id.step_bg, false);
            helper.setVisible(R.id.parent_action_error, false);
            helper.setVisible(R.id.child_action, false);
            helper.setVisible(R.id.child_action_error, false);
            helper.setVisible(R.id.link, false);
        } else {
            if (item.parentActionId == CardType.ACTION_FORWARD || item.parentActionId == CardType.ACTION_LOOP) {
                helper.setVisible(R.id.step, true);
                helper.setVisible(R.id.step_bg, true);
                helper.setVisible(R.id.parent_action_error, item.status != null && !item.status);
                helper.setText(R.id.step, String.valueOf(item.stepCount > 0 ? item.stepCount : 1));
                helper.addOnClickListener(R.id.step);
                helper.addOnLongClickListener(R.id.step);
            } else if (item.parentActionId == CardType.ACTION_CLOSURE) {
                helper.setVisible(R.id.step, false);
                helper.setVisible(R.id.step_bg, false);
                helper.setVisible(R.id.parent_action_error, item.status != null && !item.status);
                item.stepCount = 1;
            } else {
                helper.setVisible(R.id.step, false);
                helper.setVisible(R.id.step_bg, false);
                helper.setVisible(R.id.parent_action_error, false);
                item.stepCount = 1;
            }
            if (item.parentActionId == CardType.ACTION_FORWARD) {
                boolean isShowChildAction = item.childAction != null;
                helper.setVisible(R.id.child_action, isShowChildAction);
                helper.setVisible(R.id.link, isShowChildAction);
                helper.setImageResource(R.id.child_action, CardResource.childImage(isShowChildAction ? item.childAction.childActionId : CardType.ACTION_DEFAULT));
                helper.setVisible(R.id.child_action_error, isShowChildAction && !item.childAction.status);
                if (isShowChildAction) {
                    helper.addOnClickListener(R.id.child_action);
                    helper.addOnLongClickListener(R.id.child_action);
                }
            } else if (item.parentActionId == CardType.ACTION_FUNCTION_CALL) {
                boolean isShowChildAction = item.childAction != null;
                helper.setVisible(R.id.child_action, true);
                helper.setGone(R.id.link, false);
                helper.setGone(R.id.step, false);
                helper.setGone(R.id.step_bg, false);
                helper.setVisible(R.id.child_action_error, isShowChildAction && !item.childAction.status);
                helper.setImageResource(R.id.child_action, CardResource.functionImage(isShowChildAction ? item.childAction.childActionId : CardType.ACTION_DEFAULT));
                helper.addOnClickListener(R.id.child_action);
                helper.addOnLongClickListener(R.id.child_action);
            } else {
                helper.setVisible(R.id.child_action, false);
                helper.setVisible(R.id.link, false);
                helper.setVisible(R.id.child_action_error, false);
            }
        }
    }

}