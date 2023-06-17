package com.bearya.robot.base.play;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.util.CodeUtils;
import com.bearya.robot.base.util.DebugUtil;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.ResourceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bearya.robot.base.play.PlayData.ONLY_ACTION;

public class TellStory implements MediaPlayer.OnCompletionListener {

    private LottieAnimationView view;
    private static final int FRAME_DURING = 100;
    private LoadPlay loadPlay;
    private PlayData playData;
    private PlayListener listener;
    private Map<String, LoadPlay> loadPlayMap = new HashMap<>();

    private Runnable frameAnimationCompleteRunnable = new Runnable() {
        @Override
        public void run() {
            DebugUtil.debug("执行定时complete====================>");
            complete(PlayData.ONLY_FRAME_ANIMATION);
        }
    };

    private List<Runnable> actionRunnables = new ArrayList<>();

    private static TellStory instance;
    private String soundParam;
    private AnimationsContainer.FramesSequenceAnimation animation;

    public static TellStory getInstance() {
        if (instance == null) {
            instance = new TellStory();
        }
        return instance;
    }

    public void register(String key, LoadPlay play) {
        if (!loadPlayMap.containsKey(key)) {
            loadPlayMap.put(key, play);
        }
    }

    public void director(String key, PlayListener listener) {
        director(key, null, listener);
    }

    public void director(String key, String soundParam, PlayListener listener) {
        this.listener = listener;
        this.soundParam = soundParam;
        BaseApplication.getInstance().getHandler().removeCallbacks(frameAnimationCompleteRunnable);
        DebugUtil.error("获取key=%s ,数据", key);
        this.loadPlay = loadPlayMap.remove(key);
        if (loadPlay != null) {
            directNext();
        }
    }

    private void directNext() {
        if (loadPlay != null) {
            playData = loadPlay.getPlay();
        }
        if (playData == null && listener != null) {
            loadPlay = null;
            listener.onComplete();
        } else {
            directPlay(playData);
        }
    }

    private void directPlay(PlayData playData) {
        if (view != null) {
            view.removeCallbacks(imageCompleteRunnable);
        }
        if (playData != null) {
            playData.countCompleteCondition();
            playFace(playData.getFacePlay());
            playSound(playData.getSound());
            playLight(playData.getColor(), playData.getMode());
            playAction(playData.getRobotAction());
        }
    }

    public void setView(LottieAnimationView view) {
        this.view = view;
    }

    public LottieAnimationView getView() {
        return view;
    }

    private void playEmotion(String fileName) {
        view.setAnimation(String.format("emotion/%s.json", fileName));
        view.loop(true);
        view.playAnimation();
        view.postDelayed(imageCompleteRunnable, 3000);
    }

    private void playFace(final FacePlay facePlay) {
        if (facePlay != null && !TextUtils.isEmpty(facePlay.getFace())) {
            if (view != null) {
                Drawable drawable = view.getDrawable();
                if (drawable instanceof AnimationDrawable) {
                    AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                    animationDrawable.stop();
                }
            }

            DebugUtil.debug("playFace type=%s,file=%s", facePlay.getFaceType().name(), facePlay.getFace());
            BaseApplication.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    switch (facePlay.getFaceType()) {
                        case Lottie:
                            if (animation != null) {
                                animation.stop();
                            }

                            playEmotion(facePlay.getFace());
                            break;
                        case Image:
                            int mipmapId = 0;
                            try {
                                mipmapId = Integer.parseInt(facePlay.getFace());
                            } catch (Exception e) {
                                if (facePlay.getFace() != null) {
                                    if ((facePlay.getFace().endsWith(".png") || facePlay.getFace().endsWith(".jpg"))) {
                                        playImage(facePlay.getFace());
                                    } else {
                                        mipmapId = ResourceUtil.getMipmapId(facePlay.getFace());
                                    }
                                }
                            }
                            if (mipmapId > 0) {
                                playImage(mipmapId);
                            }
                            break;
                        case FrameAnimate:
                            int drawable = Integer.parseInt(facePlay.getFace());
                            playAnimation(drawable);
                            break;
                        case Arrays:
                            int array = Integer.parseInt(facePlay.getFace());
                            animation = AnimationsContainer.getInstance(array, facePlay.getTime()).createProgressDialogAnim(view);
                            animation.start();
                            animation.setOnAnimStopListener(new AnimationsContainer.OnAnimationStoppedListener() {
                                @Override
                                public void AnimationStopped() {
                                    DebugUtil.debug("动画播放完毕");
                                    complete(PlayData.ONLY_FRAME_ANIMATION);
                                }
                            });
                            break;
                    }
                }
            });
        } else {
            view.setAnimation("emotion/sh.json");
            view.playAnimation();
        }
    }

    private void playImage(int res) {
        Bitmap bitmap = BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), res);
        playImage(bitmap);
    }

    private void playImage(String file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file);
        playImage(bitmap);
    }

    private Runnable imageCompleteRunnable = new Runnable() {
        @Override
        public void run() {
            complete(PlayData.ONLY_IMAGE);
        }
    };

    private void playImage(Bitmap bitmap) {
        if (view != null) {
            if (bitmap != null) {
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setImageBitmap(bitmap);
                view.postDelayed(imageCompleteRunnable, 3000);
                return;
            }
        }
        complete(PlayData.ONLY_IMAGE);
    }

    private void playAnimation(int res) {
        AnimationDrawable animationDrawable = (AnimationDrawable) BaseApplication.getInstance().getResources().getDrawable(res, null);
        view.setImageDrawable(animationDrawable);
        long delay = animationDrawable.getNumberOfFrames() * FRAME_DURING;
        animationDrawable.start();
        BaseApplication.getInstance().getHandler().postDelayed(frameAnimationCompleteRunnable, delay);
    }

    private void playSound(String file) {
        if (!TextUtils.isEmpty(file)) {
            MusicUtil.stopMusic();
            if (!TextUtils.isEmpty(soundParam) && file.contains("%s")) {
                file = String.format(file, soundParam);
            }
            DebugUtil.debug("playSound=%s", file);

            if (!TextUtils.isEmpty(file)) {
                if (file.startsWith("/storage/")) {
                    MusicUtil.play(file, this);
                } else {
                    MusicUtil.playAssetsAudio(file, this);
                }
            }
        }
    }

    private void playAction(TimeAction[] actions) {
        removeActionRunnable();
        if (actions != null && actions.length > 0) {
            int during = 0;
            for (TimeAction action : actions) {
                if (action == null) {
                    continue;
                }
                ActionRunnable runnable = new ActionRunnable(action.getAction());
                actionRunnables.add(runnable);
                BaseApplication.getInstance().getHandler().postDelayed(runnable, during);
                during += (action.getTime() * 1000);
            }
            Runnable stopActionRunnable = new Runnable() {
                @Override
                public void run() {
                    RobotActionManager.reset();
                    complete(ONLY_ACTION);
                }
            };
            actionRunnables.add(stopActionRunnable);
            BaseApplication.getInstance().getHandler().postDelayed(stopActionRunnable, during);
        }

    }

    private void playLight(RobotActionManager.LightColor color, RobotActionManager.LightMode mode) {
        if (color != null) {
            DebugUtil.debug("颜色:%s,模式:%s", color.name(), mode.name());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        complete(PlayData.ONLY_SOUND);
    }

    private void complete(int contidion) {
        if (playData != null) {
            playData.complete(contidion);
            if (playData.isComplete()) {
                directNext();
            }
        } else {
            directNext();
        }
    }

    public void reset() {
        playData = null;
        loadPlay = null;
        listener = null;
        loadPlayMap.clear();
        BaseApplication.getInstance().getHandler().removeCallbacks(frameAnimationCompleteRunnable);
    }

    public void release() {
        playData = null;
        loadPlay = null;
        listener = null;
        loadPlayMap.clear();
        view = null;
        instance = null;
    }

    public void stop() {
        MusicUtil.stopMusic();
        removeActionRunnable();
        RobotActionManager.reset();
        removeActionRunnable();
    }

    private void removeActionRunnable() {
        if (!CodeUtils.isEmpty(actionRunnables)) {
            for (Runnable runnable : actionRunnables) {
                BaseApplication.getInstance().getHandler().removeCallbacks(runnable);
            }
        }
        actionRunnables.clear();
    }

    private static class ActionRunnable implements Runnable {
        private int action;

        ActionRunnable(int action) {
            this.action = action;
        }

        @Override
        public void run() {
            RobotActionManager.reset();
            BaseApplication.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doAction();
                }
            }, 1000);
        }

        private void doAction() {
            switch (action) {
                case 1:
                    RobotActionManager.handShake(50);
                    break;
                case 2:
                    RobotActionManager.ctrlLeftHand(0, 50, 10);
                    break;
                case 3:
                    RobotActionManager.ctrlRighttHand(0, 50, 10);
                    break;
                case 4:
                    RobotActionManager.headerShake((byte) 10);
                    break;
                case 5:
                    RobotActionManager.turnHead(8, 50, 10);
                    break;
                case 6:
                    RobotActionManager.turnHead(0, 50, 10);
                    break;
            }
        }

    }

}
