package com.bearya.robot.fairystory.ui.res;

import com.bearya.robot.base.walk.Section;

/**
 * 主指令/父指令/方向指令，这个刷卡指令是主流程，控制单次运动方向
 */
public class CardParentAction {

    public CardParentAction(int parentActionId) {
        this.parentActionId = parentActionId;
    }

    /**
     * 主指令标记
     */
    public int parentActionId;
    /**
     * 步数
     */
    public int stepCount = 1;
    /**
     * 指令状态 ： null 或者是 true 就是正常的状态 ， false 是异常的状态
     */
    public Boolean status;
    /**
     * 子指令集合
     */
    public CardChildAction childAction;
    /**
     * 根据步数设置反馈id的范围值
     */
    public Section idSection;

}