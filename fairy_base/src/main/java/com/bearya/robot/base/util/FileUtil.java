package com.bearya.robot.base.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

    public static String stringFromAssetsFile(Context context, String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(filePath)));
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

}