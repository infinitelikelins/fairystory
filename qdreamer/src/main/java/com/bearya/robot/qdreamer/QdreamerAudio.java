package com.bearya.robot.qdreamer;

import android.content.Context;
import android.text.TextUtils;

import com.qdreamer.qvoice.QAudio;
import com.qdreamer.qvoice.QSession;
import com.qdreamer.utils.WaveUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class QdreamerAudio {

    private QAudio audio;
    private QSession mSession;
    private long sessionId;
    private boolean startRecord;
    private static QdreamerAudio mInstance;

    private boolean isQDreamInit = false;

    public static QdreamerAudio getInstance() {
        if (mInstance == null) {
            mInstance = new QdreamerAudio();
        }
        return mInstance;
    }

    private QdreamerAudio() {

    }

    public void init(Context context) {
        if (isQDreamInit) return;
        try {
            String appKey = "83999894-37bd-3a0c-b2a6-681405043c61";
            String appId = "0d11b372-9c45-11e7-b526-00163e13c8a2";
            String mPath = context.getFilesDir().getAbsolutePath() + "/qvoice";
            fileExistsOrCopyFromAssets(context, mPath,"audio.cfg", "cfg");
            mSession = new QSession(context);
            mSession.setQSessionCallback(null);
            sessionId = mSession.initSession(appId, appKey);
            audio = new QAudio();
            audio.newAudio(sessionId, mPath + "/audio.cfg");// 创建audio录音引
            isQDreamInit = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (audio != null) {
                audio.deleteAudio();
                audio = null;
            }
            if (mSession != null) {
                mSession.exitSession(sessionId);
                mSession = null;
            }
            isQDreamInit = false;
        }
    }

    public void startRecord(final String filePath) {
        if (startRecord || TextUtils.isEmpty(filePath)) {
            stop();
            return;
        }
        audio.startRecord();
        startRecord = true;
        new Thread(() -> {
            String mPcmTempFilePath = filePath.replace(".wav", ".pcm");
            try (BufferedOutputStream mWavFile = new BufferedOutputStream(new FileOutputStream(new File(mPcmTempFilePath)))) {
                while (startRecord) {
                    byte[] datas = audio.readRecordData();// 3,获取录音数据
                    if (datas != null) {
                        mWavFile.write(datas, 0, datas.length);// 往文件里写音频数据
                        mWavFile.flush();
                    }
                }
                WaveUtils.Pcm2Wave(filePath, mPcmTempFilePath, 16000, (short) 1); //pcm转wav
                if (mPcmTempFilePath.length() > 4) {
                    new File(mPcmTempFilePath).delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        if (audio != null) {
            audio.stopRecord();// 停止录音
        }
        startRecord = false;
    }

    public void release() {
        if (mInstance != null) {
            if (audio != null) {
                audio.deleteAudio();
                audio = null;
            }
            if (mSession != null) {
                mSession.exitSession(sessionId);
                mSession = null;
            }
        }
        mInstance = null;
        isQDreamInit= false;
    }

    private void fileExistsOrCopyFromAssets(Context context, String filePath, String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            File file = new File(filePath, fileName);
            if (file.exists()) continue;
            if (file.getParentFile() == null) throw new IOException("filePath error!!!");
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                throw new IOException("filePath mkdir error!!!");
            try (InputStream is = context.getAssets().open(fileName);
                 FileOutputStream output = new FileOutputStream(file.getAbsolutePath())) {
                byte[] b = new byte[1024 * 2];
                int len;
                while ((len = is.read(b)) != -1) {
                    output.write(b, 0, len);
                }
                output.flush();
            }
        }
    }

}