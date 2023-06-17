package com.bearya.robot.base.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;


import java.util.ArrayList;
import java.util.List;


public class AnimationUtil {

    private static List<ObjectAnimator> objectAnimators = new ArrayList<>();
    private static List<AnimatorSet> animatorSets = new ArrayList<>();

    private static final String TAG = AnimationUtil.class.getName();

    public interface OnAnimationCompletionListener{
        void onComplete();
    }

    public static void translateSingle(final View view, long timeMilli, AnimationDirector director, float distance,OnAnimationCompletionListener listener) {
        translate(view, timeMilli, director, distance, false, false,listener);
    }

    public static void translateSingle(final View view, long timeMilli, AnimationDirector director, float distance) {
        translate(view, timeMilli, director, distance, false, false,null);
    }

    public static void translateInfinite(final View view, long timeMilli, AnimationDirector director, float distance) {
        translate(view, timeMilli, director, distance, false, true,null);
    }

    public static void translate(final View view, long timeMilli, AnimationDirector director, float distance, boolean isRecover, boolean needRepeat, final OnAnimationCompletionListener listener) {
        String propertyName = "translationX";
        switch (director) {
            case TOP:
                propertyName = "translationY";
                distance = -distance;
                break;
            case BOTTOM:
                propertyName = "translationY";
                break;
            case LEFT:
                propertyName = "translationX";
                distance = -distance;
                break;
            case RIGHT:
                propertyName = "translationX";
                break;
        }
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, propertyName, distance);
        objectAnimators.add(animation);
        if (needRepeat) {
            animation.setRepeatMode(ValueAnimator.REVERSE);
            animation.setRepeatCount(ValueAnimator.INFINITE);
        }
        animation.setDuration(timeMilli);
        if (isRecover) {
            addRecover(animation);
        }
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener!=null){
                    listener.onComplete();
                }
            }
        });
        animation.start();
    }

    public static AnimationObject scaleInfinite(View view, long timeMilli, float scale) {
        return scaleInfinite(view, timeMilli, scale, ValueAnimator.REVERSE);
    }

    public static AnimationObject scaleInfiniteRestart(View view, long timeMilli, float scale) {
        return scaleInfinite(view, timeMilli, scale, ValueAnimator.RESTART);
    }

    public static AnimationObject scaleSingle(View view, long timeMilli, float scale) {
        return scaleSingle(view, timeMilli, scale, false);
    }

    public static AnimationObject scaleXSingle(View view, long timeMilli, float scale) {
        AnimationObject animationObject = new AnimationObject();
        AnimatorSet bouncer = new AnimatorSet();
        animatorSets.add(bouncer);
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        animationX.setDuration(timeMilli);
        bouncer.play(animationX);
        bouncer.start();
        animationObject.addElement(animationX);
        return animationObject;
    }

    public static AnimationObject scaleYSingle(View view, long timeMilli, float scale) {
        AnimationObject animationObject = new AnimationObject();
        AnimatorSet bouncer = new AnimatorSet();
        animatorSets.add(bouncer);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        animationY.setDuration(timeMilli);
        bouncer.play(animationY);
        bouncer.start();
        animationObject.addElement(animationY);
        return animationObject;
    }

    public static AnimationObject scaleSingle(View view, long timeMilli, float scale, boolean isRecover) {
        AnimationObject animationObject = new AnimationObject();
        AnimatorSet bouncer = new AnimatorSet();
        animatorSets.add(bouncer);
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        animationX.setDuration(timeMilli);
        animationY.setDuration(timeMilli);
        bouncer.play(animationX).with(animationY);
        if (isRecover) {
            addRecover(animationX);
            addRecover(animationY);
        }
        bouncer.start();
        animationObject.addElement(animationX);
        animationObject.addElement(animationY);
        return animationObject;
    }

    public static AnimationObject scaleInfinite(View view, long timeMilli, float scale,int repeatMode) {
        AnimationObject animationObject = new AnimationObject();
        AnimatorSet bouncer = new AnimatorSet();
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        animationX.setRepeatMode(repeatMode);
        animationX.setRepeatCount(ValueAnimator.INFINITE);
        animationY.setRepeatMode(repeatMode);
        animationY.setRepeatCount(ValueAnimator.INFINITE);
        animationX.setDuration(timeMilli);
        animationY.setDuration(timeMilli);
        bouncer.play(animationX).with(animationY);
        bouncer.start();
        animationObject.addElement(animationX);
        animationObject.addElement(animationY);
        return animationObject;
    }

    public static void rotateSingle(View view, long timeMilli, float begin, float end) {
        rotate(view,timeMilli,begin,end,false);
    }

    public static void rotate(View view, long timeMilli, float begin, float end, boolean isRecover) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotation", begin, end);
        objectAnimators.add(animation);
        animation.setDuration(timeMilli);
        if (isRecover) {
            addRecover(animation);
        }
        animation.start();
    }

    public static void alphaSingle(View view, long timeMilli, float begin, float end){
        alpha(view,timeMilli,begin,end,false);
    }

    public static void alpha(View view, long timeMilli, float begin, float end, boolean isRecover) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", begin, end);
        objectAnimators.add(animation);
        animation.setDuration(timeMilli);
        if (isRecover) {
            addRecover(animation);
        }
        animation.start();
    }

    public static void playSerialInfinite(View view, List<BaseAction> baseActionList) {
        playSerial(view, baseActionList, false, true, 0);
    }

    public static void playSerialInfinite(View view, List<BaseAction> baseActionList, int delay) {
        playSerial(view, baseActionList, false, true, delay);
    }

    public static void playSerialSingle(View view, List<BaseAction> baseActionList, boolean isRecover){
        playSerial(view, baseActionList, isRecover, false, 0);
    }

    public static void playSerial(View view, List<BaseAction> baseActionList, boolean isRecover, boolean needRepeat, int delay) {
        AnimatorSet bouncer = new AnimatorSet();
        animatorSets.add(bouncer);
        List<ObjectAnimator> objectAnimators = new ArrayList<>();
        for (BaseAction baseAction : baseActionList) {
            if (baseAction instanceof Alpha) {
                Alpha alpha = (Alpha) baseAction;
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", alpha.getBegin(), alpha.getEnd());
                animation.setDuration(alpha.getTimeMilli());
                objectAnimators.add(animation);
            } else if (baseAction instanceof Scale) {
                Scale scale = (Scale) baseAction;
                ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", scale.getRatio());
                animationX.setDuration(scale.getTimeMilli());
                objectAnimators.add(animationX);
            } else if (baseAction instanceof Rotation) {
                Rotation rotation = (Rotation) baseAction;
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotation", rotation.getBegin(), rotation.getEnd());
                animation.setDuration(rotation.getTimeMilli());
                objectAnimators.add(animation);
            } else if (baseAction instanceof Translation) {
                Translation translation = (Translation) baseAction;
                String propertyName = "translationX";
                float distance = translation.getDistance();
                switch (translation.getDirector()) {
                    case TOP:
                        propertyName = "translationY";
                        distance = -distance;
                        break;
                    case BOTTOM:
                        propertyName = "translationY";
                        break;
                    case LEFT:
                        propertyName = "translationX";
                        distance = -distance;
                        break;
                    case RIGHT:
                        propertyName = "translationX";
                        break;
                }
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, propertyName, distance);
                animation.setDuration(translation.getTimeMilli());
                objectAnimators.add(animation);
            }
        }
        for (int i = 0; i < objectAnimators.size(); i++) {
            ObjectAnimator objectAnimator = objectAnimators.get(i);
            if (i == 0) {
                if (("scaleX").equals(objectAnimator.getPropertyName())) {
                    ObjectAnimator scaleYObjectAnimator = objectAnimator.clone();
                    scaleYObjectAnimator.setPropertyName("scaleY");
                    bouncer.play(objectAnimator).with(scaleYObjectAnimator);
                } else {
                    bouncer.play(objectAnimator);
                }
            } else {
                if (("scaleX").equals(objectAnimator.getPropertyName())) {
                    ObjectAnimator scaleYObjectAnimator = objectAnimator.clone();
                    scaleYObjectAnimator.setPropertyName("scaleY");
                    bouncer.play(objectAnimator).with(scaleYObjectAnimator).after(objectAnimators.get(i - 1));
                } else {
                    bouncer.play(objectAnimator).after(objectAnimators.get(i - 1));
                }
            }
        }
        bouncer.start();
        if (isRecover) {
            addRecover(bouncer);
        }
        if (needRepeat) {
            addInfinite(view, bouncer, delay);
        }
    }

    public static void playParallel(View view, List<BaseAction> baseActionList, boolean isRecover) {
        final AnimatorSet bouncer = new AnimatorSet();
        animatorSets.add(bouncer);
        List<Animator> objectAnimators = new ArrayList<>();
        for (BaseAction baseAction : baseActionList) {
            if (baseAction instanceof Alpha) {
                Alpha alpha = (Alpha) baseAction;
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", alpha.getBegin(), alpha.getEnd());
                animation.setDuration(alpha.getTimeMilli());
                objectAnimators.add(animation);
            } else if (baseAction instanceof Scale) {
                Scale scale = (Scale) baseAction;
                ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", scale.getRatio());
                ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", scale.getRatio());
                animationX.setDuration(scale.getTimeMilli());
                animationY.setDuration(scale.getTimeMilli());
                objectAnimators.add(animationX);
                objectAnimators.add(animationY);
            } else if (baseAction instanceof Rotation) {
                Rotation rotation = (Rotation) baseAction;
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotation", rotation.getBegin(), rotation.getEnd());
                animation.setDuration(rotation.getTimeMilli());
                objectAnimators.add(animation);
            } else if (baseAction instanceof Translation) {
                Translation translation = (Translation) baseAction;
                String propertyName = "translationX";
                float distance = translation.getDistance();
                switch (translation.getDirector()) {
                    case TOP:
                        propertyName = "translationY";
                        break;
                    case BOTTOM:
                        propertyName = "translationY";
                        distance = -distance;
                        break;
                    case LEFT:
                        propertyName = "translationX";
                        distance = -distance;
                        break;
                    case RIGHT:
                        propertyName = "translationX";
                        break;
                }
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, propertyName, distance);
                animation.setDuration(translation.getTimeMilli());
                objectAnimators.add(animation);
            }
        }
        bouncer.playTogether(objectAnimators);
        bouncer.start();
        if (isRecover) {
            addRecover(bouncer);
        }
    }

    public static void endAll(){
        for (ObjectAnimator objectAnimator : objectAnimators){
            objectAnimator.end();
        }
        for (AnimatorSet animatorSet : animatorSets){
            animatorSet.end();
        }
        objectAnimators.clear();
        animatorSets.clear();
    }


    private static void addRecover(ObjectAnimator animation) {
        animation.setRepeatCount(1);
//        如果需要反方向平滑地回到原位，这个注释开启，并且把下面的添加监听注释掉
//        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                animation.pause();
            }
        });
    }

    private static void addRecover(final AnimatorSet bouncer) {
        bouncer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bouncer.start();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                bouncer.pause();
            }
        });

    }

    private static void addInfinite(final View view, final AnimatorSet bouncer, final int delay) {
        bouncer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //位移可以不恢复，缩放大小必须恢复
                        view.setScaleX(1);
                        view.setScaleY(1);
                        bouncer.start();
                    }
                }, delay);

            }
        });

    }

}
