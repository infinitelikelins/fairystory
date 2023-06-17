package com.bearya.robot.base.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class CodeUtils {
    public static boolean isEmpty(Collection collection){
        return collection==null || collection.isEmpty();
    }

    public static long random(long min,long max){
        return Math.round(Math.random()*(max-min)+min);
    }
}
