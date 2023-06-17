package com.bearya.robot.fairystory.ui.res;

import androidx.annotation.DrawableRes;

import com.bearya.robot.base.card.BoatPropCard;
import com.bearya.robot.base.card.BulletPropCard;
import com.bearya.robot.base.card.FlutePropCard;
import com.bearya.robot.base.card.MagicWandPropCard;
import com.bearya.robot.base.card.NeedlesPropCard;
import com.bearya.robot.base.card.PropCard;
import com.bearya.robot.base.card.SoldierPropCard;
import com.bearya.robot.base.card.StickPropCard;
import com.bearya.robot.base.card.WaterPropCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.walk.load.ArmorLoad;
import com.bearya.robot.fairystory.walk.load.CompassLoad;
import com.bearya.robot.fairystory.walk.load.CrystalShoesLoad;
import com.bearya.robot.fairystory.walk.load.DanceSkirtLoad;
import com.bearya.robot.fairystory.walk.load.FatTonnyLoad;
import com.bearya.robot.fairystory.walk.load.KeyLoad;
import com.bearya.robot.fairystory.walk.load.PegasusLoad;
import com.bearya.robot.fairystory.walk.load.SwordLoad;
import com.bearya.robot.fairystory.walk.load.TreasureMapLoad;

/**
 * 卡片的数据资源
 */
public class CardResource {

    /**
     * 道具卡片
     */
    @DrawableRes
    public static int childImage(int cardType) {

        switch (cardType) {
            case CardType.ACTION_BOAT: return R.mipmap.ic_boat;
            case CardType.ACTION_MAGIC: return R.mipmap.ic_magic;
            case CardType.ACTION_SOLDIER: return R.mipmap.ic_soldie;
            case CardType.ACTION_NEEDLES: return R.mipmap.ic_needles;
            case CardType.ACTION_WATER: return R.mipmap.ic_water;
            case CardType.ACTION_FLUTE: return R.mipmap.ic_flute;
            case CardType.ACTION_BULLET: return R.mipmap.ic_bullet;
            case CardType.ACTION_STICK: return R.mipmap.ic_stick;
            default: return R.mipmap.ic_add_no_focus_2;
        }
    }

    /**
     * 函数卡片
     */
    @DrawableRes
    public static int functionImage(int cardType){
        switch (cardType) {
            case CardType.ACTION_FUNCTION_1: return R.drawable.ic_function_1;
            case CardType.ACTION_FUNCTION_2: return R.drawable.ic_function_2;
            case CardType.ACTION_FUNCTION_3: return R.drawable.ic_function_3;
            case CardType.ACTION_FUNCTION_4: return R.drawable.ic_function_4;
            case CardType.ACTION_FUNCTION_5: return R.drawable.ic_function_5;
            case CardType.ACTION_FUNCTION_6: return R.drawable.ic_function_6;
            case CardType.ACTION_FUNCTION_7: return R.drawable.ic_function_7;
            case CardType.ACTION_FUNCTION_8: return R.drawable.ic_function_8;
            case CardType.ACTION_FUNCTION_9: return R.drawable.ic_function_9;
            case CardType.ACTION_FUNCTION_10: return R.drawable.ic_function_10;
            default: return R.mipmap.ic_add_no_focus_2;
        }
    }

    /**
     * 指令卡片的配音文件
     */
    public static String cardVoice(int cardType) {
        switch (cardType) {
            case CardType.ACTION_FORWARD: return "card/zh/p_forward.mp3";
            case CardType.ACTION_BACKWARD: return "card/zh/p_turn_back.mp3";
            case CardType.ACTION_LEFT: return "card/zh/p_turn_left.mp3";
            case CardType.ACTION_RIGHT: return "card/zh/p_turn_right.mp3";
            case CardType.ACTION_PARALLEL: return "card/zh/p_Juxtaposition.mp3";
            case CardType.ACTION_BOAT: return "card/zh/p_boat.mp3";
            case CardType.ACTION_MAGIC: return "card/zh/p_wand.mp3";
            case CardType.ACTION_SOLDIER: return "card/zh/p_sunflower.mp3";
            case CardType.ACTION_NEEDLES: return "card/zh/p_needles.mp3";
            case CardType.ACTION_WATER: return "card/zh/p_water.mp3";
            case CardType.ACTION_FLUTE: return "card/zh/p_flute.mp3";
            case CardType.ACTION_BULLET: return "card/zh/p_bomb.mp3";
            case CardType.ACTION_STICK: return "card/zh/p_sticks.mp3";
            case CardType.ACTION_LOOP: return "card/zh/p_loop.mp3";
            case CardType.ACTION_CLOSURE: return "card/zh/p_closure.mp3";
            case CardType.ACTION_INSERT_LEFT:
            case CardType.ACTION_INSERT_RIGHT: return "card/zh/p_insert.mp3";
            case CardType.ACTION_FUNCTION_CALL: return "card/zh/p_fun_call.mp3";
            default: return "";
        }
    }

    /**
     * 主指令卡片图片
     */
    @DrawableRes
    public static int parentImage(int cardType) {
        switch (cardType) {
            case CardType.ACTION_FORWARD: return R.mipmap.ic_forward;
            case CardType.ACTION_BACKWARD: return R.mipmap.ic_backward;
            case CardType.ACTION_LEFT: return R.mipmap.ic_left;
            case CardType.ACTION_RIGHT: return R.mipmap.ic_right;
            case CardType.ACTION_LOOP: return R.drawable.ic_loop;
            case CardType.ACTION_CLOSURE: return R.drawable.ic_closure;
            case CardType.ACTION_FUNCTION_CALL: return R.drawable.ic_function_call;
            default: return R.mipmap.ic_add_no_focus_2;
        }
    }

    /**
     * 装备地垫的名称转换成对应的动画资源ID
     *
     * @param equipmentName 装备地垫的名称
     */
    @DrawableRes
    public static int transformEquipmentName(String equipmentName) {
        switch (equipmentName) {
            case ArmorLoad.NAME: return R.drawable.armor;
            case CompassLoad.NAME: return R.drawable.compass;
            case CrystalShoesLoad.NAME: return R.drawable.shoes;
            case DanceSkirtLoad.NAME: return R.drawable.skirt;
            case FatTonnyLoad.NAME: return R.drawable.carriage;
            case KeyLoad.NAME: return R.drawable.key;
            case PegasusLoad.NAME: return R.drawable.pegasus;
            case SwordLoad.NAME: return R.drawable.sword;
            case TreasureMapLoad.NAME: return R.drawable.map;
            default: return R.drawable.def;
        }
    }

    /**
     * 创建前进方向的并行道具卡
     */
    public static PropCard createChildAction(CardChildAction cardChildAction) {
        if (cardChildAction != null) {
            switch (cardChildAction.childActionId) {
                case CardType.ACTION_MAGIC: return new MagicWandPropCard(); // 魔法棒
                case CardType.ACTION_BOAT: return new BoatPropCard(); // 小船
                case CardType.ACTION_FLUTE: return new FlutePropCard(); // 跳舞笛
                case CardType.ACTION_BULLET: return new BulletPropCard(); // 粘粘弹
                case CardType.ACTION_WATER: return new WaterPropCard(); // 变身水
                case CardType.ACTION_NEEDLES: return new NeedlesPropCard(); // 毛衣针
                case CardType.ACTION_SOLDIER: return new SoldierPropCard(); // 战士
                case CardType.ACTION_STICK: return new StickPropCard(); // 逗猫棒
                default: return null;
            }
        }
        return null;
    }


}
