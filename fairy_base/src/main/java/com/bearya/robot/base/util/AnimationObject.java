package com.bearya.robot.base.util;

import android.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

public class AnimationObject {

    List<ObjectAnimator> animators = new ArrayList<>();

    public void addElement(ObjectAnimator objectAnimator){
        animators.add(objectAnimator);
    }

    public void stop(){
        for (ObjectAnimator objectAnimator : animators){
            objectAnimator.end();
        }
    }
}
