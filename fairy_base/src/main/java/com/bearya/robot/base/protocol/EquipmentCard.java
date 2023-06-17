package com.bearya.robot.base.protocol;

public enum EquipmentCard {
    Boat(12708),//    小船卡
    MagicWand(12709),//    魔法棒卡
    SunflowerWarrior(12710),//    葵花战士
    SweaterNeedle(12711),//    毛衣针
    PolyJuicePotion(12712),//    变身水
    DancingFlute(12713),//    跳舞笛
    StickyBullet(12714),//    粘粘弹
    PlayingCatSticks(12715),//    逗猫棒
    ;
    private int value;

    EquipmentCard(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
