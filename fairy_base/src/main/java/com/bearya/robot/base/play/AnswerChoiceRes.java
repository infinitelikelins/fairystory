package com.bearya.robot.base.play;

/**
 * @name Programme
 * @class name：com.bearya.robot.base.play
 * @Description: 分辨弹窗的选择图片
 * @author: yujun
 * @date 2019/5/10 上午10:13
 * @emall 614977826@qq.com
 */
public class AnswerChoiceRes {

    private int[] ids;
    private int[] imgs;
    private String startPlay;//开始动画
    private String endPlay;//结束动画
    private String startSound;//引导音
    private String[] quizs;//问答语音
    private String guide;//结束音
    private String outTimeShort;//30秒超时
    private String outTimeLong;//50秒超时
    private int random;

    public AnswerChoiceRes(int[] ids, int[] imgs, String startPlay, String endPlay, String startSound, String[] quizs, String guide,String outTimeShort,String outTimeLong) {
        this.ids = ids;
        this.imgs = imgs;
        this.startPlay = startPlay;
        this.endPlay = endPlay;
        this.startSound = startSound;
        this.quizs = quizs;
        this.guide = guide;
        this.outTimeShort = outTimeShort;
        this.outTimeLong = outTimeLong;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public int[] getImgs() {
        return imgs;
    }

    public void setImgs(int[] imgs) {
        this.imgs = imgs;
    }

    public String getStartPlay() {
        return startPlay;
    }

    public void setStartPlay(String startPlay) {
        this.startPlay = startPlay;
    }

    public String getEndPlay() {
        return endPlay;
    }

    public void setEndPlay(String endPlay) {
        this.endPlay = endPlay;
    }

    public String getStartSound() {
        return startSound;
    }

    public void setStartSound(String startSound) {
        this.startSound = startSound;
    }

    public String[] getQuizs() {
        return quizs;
    }

    public void setQuizs(String[] quizs) {
        this.quizs = quizs;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getOutTimeShort() {
        return outTimeShort;
    }

    public void setOutTimeShort(String outTimeShort) {
        this.outTimeShort = outTimeShort;
    }

    public String getOutTimeLong() {
        return outTimeLong;
    }

    public void setOutTimeLong(String outTimeLong) {
        this.outTimeLong = outTimeLong;
    }

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }
}
