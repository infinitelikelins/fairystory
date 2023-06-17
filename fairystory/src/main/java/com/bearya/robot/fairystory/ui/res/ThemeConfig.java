package com.bearya.robot.fairystory.ui.res;

import android.util.Pair;

import java.util.Random;

/**
 * 主题标志配置
 */
public class ThemeConfig {

    /**
     * 当前主题
     */
    public static String CURRENT_THEME = "";

    /**
     * 奇幻寻宝
     */
    public static final String THEME_QHXB = "qhxb";

    /**
     * 梦幻舞会
     */
    public static final String THEME_MHWH = "mhwh";

    /**
     * 英雄舞会
     */
    public static final String THEME_YXWH = "yxwh";

    /**
     * 创想天地
     */
    public static final String THEME_CXTD = "cxtd";

    /**
     * 小贝行走的时候 ，播放的背景音乐
     */
    public static String travelBgm() {
        switch (CURRENT_THEME) {
            case THEME_MHWH: return "music/zh/ball_walk.mp3";
            case THEME_QHXB: return "music/zh/treasure_walk.mp3";
            case THEME_YXWH: return "music/zh/hero_walk.mp3";
            default: return "music/zh/travel_bg.mp3";
        }
    }

    /**
     * 执行命令的时候，将小贝放在地垫上以后，播放准备行走前的音频
     */
    public static Pair<String, String> travelReadyBgm() {
        int randomTipMp3 = new Random().nextInt(600) % 3;
        switch (CURRENT_THEME) {
            case THEME_MHWH:
                return randomTipMp3 == 0 ? new Pair<>("music/zh/w_ready_ball1.mp3", Command.FairyStoryEmotionBall1) :
                        randomTipMp3 == 1 ? new Pair<>("music/zh/w_ready_ball2.mp3", Command.FairyStoryEmotionBall2) :
                                randomTipMp3 == 2 ? new Pair<>("music/zh/w_ready_ball3.mp3", Command.FairyStoryEmotionBall3) : new Pair<>("", "");
            case THEME_QHXB:
                return randomTipMp3 == 0 ? new Pair<>("music/zh/w_ready_treasure1.mp3", Command.FairyStoryEmotionTreasure1) :
                        randomTipMp3 == 1 ? new Pair<>("music/zh/w_ready_treasure2.mp3", Command.FairyStoryEmotionTreasure2) :
                                randomTipMp3 == 2 ? new Pair<>("music/zh/w_ready_treasure3.mp3", Command.FairyStoryEmotionTreasure3) : new Pair<>("", "");
            case THEME_YXWH:
                return randomTipMp3 == 0 ? new Pair<>("music/zh/w_ready_hero1.mp3", Command.FairyStoryEmotionHero1) :
                        randomTipMp3 == 1 ? new Pair<>("music/zh/w_ready_hero2.mp3", Command.FairyStoryEmotionHero2) :
                                randomTipMp3 == 2 ? new Pair<>("music/zh/w_ready_hero3.mp3", Command.FairyStoryEmotionHero3) : new Pair<>("", "");
            default:
                return new Pair<>("", "");
        }
    }

}