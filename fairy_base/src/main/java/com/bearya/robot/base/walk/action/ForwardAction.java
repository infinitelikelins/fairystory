package com.bearya.robot.base.walk.action;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.card.PropCard;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.base.util.CodeUtils;
import com.bearya.robot.base.walk.Direct;

/**
 * 前进
 */
public class ForwardAction extends Action {
    private int id;

    public ForwardAction(int step) {
        super(Direct.Forward, step);
        if (CodeUtils.random(0, 10) < 3) {
            setAdditional(new Additional(EquipmentCard.Boat));
        }
    }

    /**
     * 前进 附加 道具卡
     *
     * @param mStep 步数
     * @param card  道具卡
     */
    public ForwardAction(int id ,int mStep, PropCard card) {
        super(Direct.Forward, mStep);
        this.id = id;
        if (card != null) {
            setAdditional(new Additional(card));
        }
    }

    @Override
    public String getName() {
        return "前进";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}