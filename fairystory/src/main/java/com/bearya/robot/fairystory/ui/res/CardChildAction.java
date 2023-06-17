package com.bearya.robot.fairystory.ui.res;

/**
 * 并行指令/子指令/动作配件指令，控制障碍应对运动
 */
public class CardChildAction {

    public CardChildAction(int childActionId) {
        this.childActionId = childActionId;
    }

    /**
     * 子指令标记
     */
    public int childActionId;

    /**
     * 指令状态 ： null 或者是 true 就是正常的状态 ， false 是异常的状态
     */
    public boolean status = true;

}
