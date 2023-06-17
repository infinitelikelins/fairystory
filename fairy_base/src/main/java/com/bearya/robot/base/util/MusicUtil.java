package com.bearya.robot.base.util;

import android.media.MediaPlayer;
import android.text.TextUtils;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.musicplayer.LocalMusicPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicUtil {

    private static LocalMusicPlayer localMusicPlayer = null;
    private static LocalMusicPlayer localMusicPlayerBg = null;

    public static void init() {
        localMusicPlayer = new LocalMusicPlayer(BaseApplication.getInstance());
        localMusicPlayerBg = new LocalMusicPlayer(BaseApplication.getInstance(), true);
    }

    private static List<String> audios = null;

    private static void playAudios() {
        if (audios != null && audios.size() > 0) {
            playAssetsAudio(audios.remove(0), mediaPlayer -> playAudios());
        }
    }

    public static void playAssetsAudio(String name) {
        playAssetsAudio(name, null);
    }

    public static void playAssetsAudios(String... names) {
        audios = new ArrayList<>(Arrays.asList(names));
        playAudios();
    }

    public static void stopMusic() {
        if (localMusicPlayer != null) {
            localMusicPlayer.stop();
        }
    }

    public static void playAssetsAudio(String mp3, final MediaPlayer.OnCompletionListener listener) {
        if (TextUtils.isEmpty(mp3)) {
            if (listener != null) {
                listener.onCompletion(null);
            }
            return;
        }
        try {
            String name = BaseApplication.isEnglish ? mp3.replace("/zh/", "/en/") : mp3;
            DebugUtil.error("play tts:" + name);
            stopMusic();
            localMusicPlayer.setOnCompletionListener(listener);
            localMusicPlayer.setLoop(false);
            localMusicPlayer.play(name != null && !name.startsWith("/storage/emulated") ? String.format("android_asset/%s", name) : name);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onCompletion(null);
            }
        }
    }

    public static void playTravelBgMusic(String mp3) {
        playAssetsBgMusic(mp3);
    }

    public static void playAssetsBgMusic(String mp3) {
        if (TextUtils.isEmpty(mp3)) {
            return;
        }
        try {
            localMusicPlayerBg.stop();
            String name = BaseApplication.isEnglish ? mp3.replace("/zh/", "/en/") : mp3;
            localMusicPlayerBg.play(String.format("android_asset/%s", name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBgMusic() {
        localMusicPlayerBg.stop();
    }

    public static void play(String file, final MediaPlayer.OnCompletionListener listener) {
        if (TextUtils.isEmpty(file)) {
            if (listener != null) {
                listener.onCompletion(null);
            }
            return;
        }
        try {
            stopMusic();
            localMusicPlayer.setOnCompletionListener(listener);
            localMusicPlayer.play(file);
        } catch (Exception e) {
            if (listener != null) {
                listener.onCompletion(null);
            }
        }
    }
}