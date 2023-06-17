package com.bearya.robot.fairystory.ui.res;

/**
 * 开始介绍动画/游戏结束动画 播放的帧动画时间间隔
 * 数值越小 ， 动画的播放速度越快
 */
public interface IntroduceTime {

    /**
     * 帧动画时间间隔 -- 默认
     */
    int def = 50;

    /**
     * 帧动画时间间隔 -- 英雄舞会 -- 开始
     */
    int heroStartTime = 42;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 开始
     */
    int treasureStartTime = 20;

    /**
     * 帧动画时间间隔 -- 梦幻舞会 -- 开始
     */
    int ballStartTime = 42;

    /**
     * 帧动画时间间隔 -- 英雄舞会 -- 结束 -- 成功
     */
    int heroEndTime = 10;

    /**
     * 帧动画时间间隔 -- 英雄舞会 -- 结束 -- 失败
     */
    int heroFailEndTime = 10;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 结束 -- 成功 -- 海底世界
     */
    int seafloorEndTime = 15;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 结束 -- 失败 -- 海底世界
     */
    int seafloorFailEndTime = 5;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 结束 -- 成功 -- 精灵王国
     */
    int spiritEndTime = 5;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 结束 -- 失败 -- 精灵王国
     */
    int spiritFailEndTime = 5;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 结束 -- 成功 -- 宇宙之旅
     */
    int universeEndTime = 20;

    /**
     * 帧动画时间间隔 -- 奇幻寻宝 -- 结束 -- 失败 -- 宇宙之旅
     */
    int universeFailEndTime = 5;

    /**
     * 帧动画时间间隔 -- 梦幻舞会 -- 结束 -- 成功
     */
    int ballEndTime = 20;

    /**
     * 帧动画时间间隔 -- 梦幻舞会 -- 结束 -- 失败
     */
    int ballFailEndTime = 10;

    /**
     * 帧动画时间间隔 -- 僵尸地垫 -- 失败
     */
    int zombieFailPlayTime = 5;

    /**
     * 帧动画时间间隔 -- 僵尸地垫 -- 成功
     */
    int zombieSuccessPlayTime = 15;

    /**
     * 帧动画时间间隔 -- 巨怪地垫 -- 失败
     */
    int monsterFailPlayTime = 10;

    /**
     * 帧动画时间间隔 -- 巨怪地垫 -- 成功
     */
    int monsterSuccessPlayTime = 10;

    /**
     * 帧动画时间间隔 -- 食人花地垫 -- 失败
     */
    int flowerFailPlayTime = 20;

    /**
     * 帧动画时间间隔 -- 食人花地垫 -- 成功
     */
    int flowerSuccessPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 鳄鱼湖地垫 -- 失败
     */
    int crocodileFailPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 鳄鱼湖地垫 -- 成功
     */
    int crocodileSuccessPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 九尾猫地垫 -- 失败
     */
    int catFailPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 九尾猫地垫 -- 成功
     */
    int catSuccessPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 蜘蛛地垫 -- 失败
     */
    int spiderFailPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 蜘蛛地垫 -- 成功
     */
    int spiderSuccessPlayTime = 22;

    /**
     * 帧动画时间间隔 -- 千年树妖地垫 -- 失败
     */
    int treeFailPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 千年树妖地垫 -- 成功
     */
    int treeSuccessPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 女巫地垫 -- 失败
     */
    int witchFailPlayTime = 20;

    /**
     * 帧动画时间间隔 -- 女巫地垫 -- 成功
     */
    int witchSuccessPlayTime = 10;

    /**
     * 帧动画时间间隔 -- 火山地垫
     */
    int volcanicPlayTime = 0;

    /**
     * 帧动画时间间隔 -- 铠甲地垫
     */
    int armorPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 指南针地垫
     */
    int compassPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 水晶鞋地垫
     */
    int crystalShoesPlayTime = 5;

    /**
     * 帧动画时间间隔 -- 舞裙地垫
     */
    int danceSkirtPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 南瓜车地垫
     */
    int fatTonnyPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 钥匙地垫
     */
    int keyPlayTime = 5;

    /**
     * 帧动画时间间隔 -- 飞马地垫
     */
    int pegasusPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 宝剑地垫
     */
    int swordPlayTime = 8;

    /**
     * 帧动画时间间隔 -- 藏宝图地垫
     */
    int treasurePlayTime = 8;

}