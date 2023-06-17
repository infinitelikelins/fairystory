package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.bearya.actionlib.utils.SharedPreferencesUtil;
import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.play.Director;
import com.bearya.robot.base.protocol.DriveResult;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.ui.view.FrameSurfaceView;
import com.bearya.robot.base.util.DebugUtil;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.walk.Section;
import com.bearya.robot.base.walk.action.Action;
import com.bearya.robot.base.walk.action.ActionSet;
import com.bearya.robot.base.walk.action.BackwardAction;
import com.bearya.robot.base.walk.action.ForwardAction;
import com.bearya.robot.base.walk.action.LeftAction;
import com.bearya.robot.base.walk.action.RightAction;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.impl.ResultFailPopup;
import com.bearya.robot.fairystory.ui.popup.impl.ResultSuccessPopup;
import com.bearya.robot.fairystory.ui.popup.impl.SaveGamePopup;
import com.bearya.robot.fairystory.ui.res.CardChildAction;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardResource;
import com.bearya.robot.fairystory.ui.res.CardType;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.HistoryRecord;
import com.bearya.robot.fairystory.ui.res.IntroduceImageArrays;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.ui.res.ThemeConfig;
import com.bearya.robot.fairystory.walk.action.RobotCarAction;
import com.bearya.robot.fairystory.walk.car.ICar;
import com.bearya.robot.fairystory.walk.car.LoadMgr;
import com.bearya.robot.fairystory.walk.car.RobotCar;
import com.bearya.robot.fairystory.walk.load.ArmorLoad;
import com.bearya.robot.fairystory.walk.load.CastleEndLoad;
import com.bearya.robot.fairystory.walk.load.CompassLoad;
import com.bearya.robot.fairystory.walk.load.CrystalShoesLoad;
import com.bearya.robot.fairystory.walk.load.DanceSkirtLoad;
import com.bearya.robot.fairystory.walk.load.DragonEndLoad;
import com.bearya.robot.fairystory.walk.load.EndLoad;
import com.bearya.robot.fairystory.walk.load.FatTonnyLoad;
import com.bearya.robot.fairystory.walk.load.IdeaEndLoad;
import com.bearya.robot.fairystory.walk.load.KeyLoad;
import com.bearya.robot.fairystory.walk.load.MineEndLoad;
import com.bearya.robot.fairystory.walk.load.PegasusLoad;
import com.bearya.robot.fairystory.walk.load.StationLoad;
import com.bearya.robot.fairystory.walk.load.SwordLoad;
import com.bearya.robot.fairystory.walk.load.TreasureMapLoad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 指令执行运行状态，执行结果
 */
public class RuntimeActivity extends BaseActivity implements ICar.DriveListener {

    // 这个类初始化了数据Can的读取和驱动控制
    private final RobotCar robotCar = new RobotCar(this);

    private LottieAnimationView lottieView;

    // 指令集合
    private ArrayList<CardParentAction> data;

    /**
     * 播放表情的配音
     */
    private final String[] emotionMp3 = new String[]{
            "music/zh/emotion_1.mp3"
    };

    /**
     * 播放表情的名称
     */
    private final String[] emotionNames = new String[]{
            "my", "axy", "dx", "kx", "ja"
    };

    /**
     * 启动执行内容
     *
     * @param actions 这个必须是List<CardParentAction>转换成的JSON字符串，或者直接为空
     */
    public static void start(Context context, String actions) {
        context.startActivity(new Intent(context, RuntimeActivity.class).putExtra("actions", actions));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runtime);

        lottieView = findViewById(R.id.lottie_view);
        lottieView.loop(true);
        Director.getInstance().setView(lottieView);

        try {
            // 执行行动的数据
            String actions = getIntent().getStringExtra("actions");
            Type type = new TypeToken<List<CardParentAction>>() {
            }.getType();
            data = new Gson().fromJson(actions, type);
        } catch (Exception e) {
            DebugUtil.debug("-- 动作序列化失败 -- ");
        }

        if (data != null && data.size() > 0) {
            // 前进指令起始范围
            AtomicInteger startId = new AtomicInteger(1);

            for (int i = 0; i < data.size(); i++) {
                CardParentAction cardParentAction = data.get(i);

                if (cardParentAction.parentActionId == CardType.ACTION_FORWARD) { // 如果是前进卡
                    // 根据步数设置反馈id的范围值，闭区间 [开始，结束]
                    cardParentAction.idSection = new Section(startId.get(), startId.addAndGet(cardParentAction.stepCount) - 1);
                } else if (cardParentAction.parentActionId == CardType.ACTION_FUNCTION_CALL) {
                    cardParentAction.idSection = new Section(startId.get(), startId.addAndGet(1) - 1);
                }

                // 运行前,所有指令状态修改为正确
                cardParentAction.status = true;

                if (cardParentAction.childAction != null) {
                    if (cardParentAction.childAction.childActionId == CardType.ACTION_DEFAULT) {
                        cardParentAction.childAction = null;
                    } else {
                        cardParentAction.childAction.status = true;
                    }
                }
            }
        }
        prepare();
    }

    /**
     * 播放LottieAnimation的表情
     *
     * @param emotion 表情
     */
    private void playLottieAnimation(String emotion) {
        if (TextUtils.isEmpty(emotion)) {
            lottieView.cancelAnimation();
            lottieView.setImageDrawable(null);
            return;
        }
        lottieView.setAnimation(String.format("emotion/%s.json", emotion));
        lottieView.playAnimation();
    }

    private void prepare() {
        String emotion, action;
        switch (ThemeConfig.CURRENT_THEME) {
            case ThemeConfig.THEME_YXWH:
                emotion = "sq";
                action = Command.FairyStoryEmotionHero;
                break;
            case ThemeConfig.THEME_QHXB:
                emotion = "ja";
                action = Command.FairyStoryEmotionTreasure;
                break;
            case ThemeConfig.THEME_MHWH:
                emotion = "axy";
                action = Command.FairyStoryEmotionBall;
                break;
            default:
                emotion = "hg";
                action = Command.FairyStoryEmotionBall;
                break;
        }
        Director.getInstance().setThemeEmotion(emotion, action);

        // 显示出哼歌的表情后开始行走
        playLottieAnimation(emotion);

        // 出发了
        Pair<String, String> pair = ThemeConfig.travelReadyBgm();
        String travelReadyBgm = pair.first;
        String playAction = pair.second;
        if (TextUtils.isEmpty(travelReadyBgm)) {
            doRun();
        } else {
            if (!TextUtils.isEmpty(playAction)) {
                BaseApplication.sendAction(playAction);
            }
            MusicUtil.playAssetsAudio(travelReadyBgm, mediaPlayer -> runOnUiThread(this::doRun));
        }

    }

    /**
     * 动起来
     */
    private void doRun() {
        // 这是行动指令对接集合
        RobotCarAction robotCarAction = new RobotCarAction();
        ActionSet actionSet = new ActionSet();
        actionSet.clear();
        int loopIndex = -1;
        int loopCount = -1;
        for (int i = 0; i < data.size(); i++) {
            CardParentAction parentAction = data.get(i);
            // 将行动指令转换成行走可识别的指令
            if (parentAction.parentActionId == CardType.ACTION_CLOSURE) {
                loopCount--;
                if (loopCount > 0) {
                    i = loopIndex;
                }
            } else if (parentAction.parentActionId == CardType.ACTION_LOOP) {
                loopCount = parentAction.stepCount;
                loopIndex = i;
            } else if (parentAction.parentActionId == CardType.ACTION_FUNCTION_CALL) {
                if (parentAction.childAction != null) {
                    String actions = SharedPreferencesUtil.getInstance(this).getString("DEFINE_FUNCTION_" + parentAction.childAction.childActionId);
                    if (!TextUtils.isEmpty(actions)) {
                        Type type = new TypeToken<List<CardParentAction>>() {}.getType();
                        List<CardParentAction> parentActions = new Gson().fromJson(actions, type);
                        for (CardParentAction action : parentActions) {
                            if (action.parentActionId != CardType.ACTION_DEFAULT) {
                                if (action.parentActionId == CardType.ACTION_FORWARD) {
                                    action.idSection = new Section(parentAction.idSection.getStart(), parentAction.idSection.getStart() + action.stepCount);
                                }
                                actionSet.add(createParentAction(action));
                            }
                        }
                    }
                }
            } else if (parentAction.parentActionId != CardType.ACTION_DEFAULT) {
                actionSet.add(createParentAction(parentAction));
            }
        }

        while (actionSet.getActionList().size() > 0) {
            Action action = actionSet.getActionList().get(0);
            if (action instanceof ForwardAction) {
                break;
            } else {
                actionSet.getActionList().remove(0);
            }
        }

        // 驱动行驶数据指向
        robotCarAction.set(actionSet);

        // 设置规划好的行动指令转换
        robotCar.setRobotAction(robotCarAction);

        // 启动
        robotCar.drive();

    }

    /**
     * 创建方向行动指令
     *
     * @param action 行动指令的位置
     */
    private List<Action> createParentAction(CardParentAction action) {
        List<Action> actions = new ArrayList<>();
        switch (action.parentActionId) {
            case CardType.ACTION_BACKWARD: // 后退
                actions.add(new BackwardAction(1));
                break;
            case CardType.ACTION_FORWARD: // 前进
                int id = action.idSection.getStart();
                // 根据前进指令步数，直接转化成步数为1的前进指令告诉RobotCar
                for (int i = 0; i < action.stepCount; i++) {
                    actions.add(new ForwardAction(id++, 1, CardResource.createChildAction(action.childAction)));
                }
                break;
            case CardType.ACTION_LEFT: //  左转
                actions.add(new LeftAction(1));
                break;

            case CardType.ACTION_RIGHT: // 右转
                actions.add(new RightAction(1));
                break;
            default:
                return null;
        }
        return actions;
    }


    /**
     * 运行的时候 ， 发生的一些异常情况
     *
     * @param exception 异常原因
     * @param param     异常信息
     */
    @Override
    public void onException(final ICar.DriveException exception, Object param) {
        runOnUiThread(() -> {
            if (exception == ICar.DriveException.OutOfLoad) { // 小贝不在地垫上,可能是行走过程中走出去的或者是人为的抱离地垫
                BaseApplication.sendAction(Command.FairyStoryWarning1);
                showResultErrorPopup(getApplicationContext(), getString(R.string.mission_error_no_load), "music/zh/w_warning1.mp3");
            }
        });
    }

    /**
     * 运行时，发生的一些结果处理，包含错误处理 ， 终点处理 ， 装备处理
     *
     * @param result    结果
     * @param stepIndex 出错的指令位置
     * @param load      当前道路
     * @param param     1.可以是装备的状态List ,  2.可以是正确的终点道路名称
     */
    @Override
    public void onDriveResult(final DriveResult result, final int stepIndex, final BaseLoad load, @Nullable final Object param) {
        DebugUtil.debug("得到结果%s,当前地垫:%s,前进步子:%d,装备状态%s", result.name(), load == null ? "null" : load.getName(), stepIndex, param != null ? param.toString() : "null");

        runOnUiThread(() -> {

            switch (result) {

                case Success:
                    if (ThemeConfig.THEME_CXTD.equals(ThemeConfig.CURRENT_THEME) && (load instanceof IdeaEndLoad || load instanceof StationLoad)) {
                        DebugUtil.debug("Success 到达终点目标终点创想小站");
                        showWhenAnyCondition(getApplicationContext());
                    } else {
                        DebugUtil.debug("Success 到达终点目标终点显示动画");
                        showEndAnimation(getApplicationContext(), load);
                    }
                    break;

                case FailLessAction:

                    // TODO: 2020/6/18 根据当前最新需要 ， 将 终点地点的逻辑 设置为 主题对应的终点地点才算到达
                    if ((load instanceof IdeaEndLoad || load instanceof StationLoad) && ThemeConfig.THEME_CXTD.equals(ThemeConfig.CURRENT_THEME)) {
                        DebugUtil.debug("FailLessAction -- 当前主题是创想天地,并且停在了创想小站地垫上");
                        showWhenAnyCondition(getApplicationContext());
                    } else {
                        DebugUtil.debug("FailLessAction -- 当前主题不是创想天地,反正就是没有到目标终点");
                        String rightEndLoad = "";
                        String errorMp3 = "";
                        switch (ThemeConfig.CURRENT_THEME) {
                            case ThemeConfig.THEME_MHWH:
                                rightEndLoad = CastleEndLoad.NAME;
                                errorMp3 = "music/zh/w_warning8.mp3";
                                break;
                            case ThemeConfig.THEME_QHXB:
                                rightEndLoad = MineEndLoad.NAME;
                                errorMp3 = "music/zh/w_warning7.mp3";
                                break;
                            case ThemeConfig.THEME_YXWH:
                                rightEndLoad = DragonEndLoad.NAME;
                                errorMp3 = "music/zh/w_warning6.mp3";
                                break;
                            case ThemeConfig.THEME_CXTD:
                                rightEndLoad = "创想小站";
                                errorMp3 = "music/zh/w_warning4.mp3";
                                break;
                        }
                        onLoadAnimation(stepIndex, load, param);
                        BaseApplication.sendAction(Command.FairyStoryWarning2);
                        showResultErrorPopup(getApplicationContext(), getString(R.string.error_target_end_load) + rightEndLoad, errorMp3);
                    }
                    break;

                case FailMoreAction:
                    DebugUtil.debug("FailMoreAction");
                    onLoadAnimation(stepIndex, load, param);
                    if (load instanceof EndLoad && ThemeConfig.THEME_CXTD.equals(ThemeConfig.CURRENT_THEME)) {
                        showResultErrorPopup(getApplicationContext(), getString(R.string.error_target_end_load) + IdeaEndLoad.NAME, "music/zh/w_warning4.mp3");
                    } else {
                        BaseApplication.sendAction(Command.FairyStoryWarning3);
                        showResultErrorPopup(getApplicationContext(), getString(R.string.mission_error_no_load), "music/zh/w_warning9.mp3");
                    }
                    break;

                case FailObstacleAdditionalLost:
                    DebugUtil.debug("FailObstacleAdditionalLost");
                    onLoadAnimation(stepIndex, load, new Object());
                    BaseApplication.sendAction(Command.FairyStoryWarning4);
                    showResultErrorPopup(getApplicationContext(), getString(R.string.mission_error_prop_none), "music/zh/w_warning5.mp3");
                    break;

                case FailObstacleAdditionalUnMatch:
                    DebugUtil.debug("FailObstacleAdditionalUnMatch");
                    onLoadAnimation(stepIndex, load, param);
                    BaseApplication.sendAction(Command.FairyStoryWarning5);
                    showResultErrorPopup(getApplicationContext(), getString(R.string.mission_error_prop_fail), "music/zh/w_warning5.mp3");
                    break;

                case FailNoEntry:
                    DebugUtil.debug("FailNoEntry");
                    onLoadAnimation(stepIndex, load, param);
                    BaseApplication.sendAction(Command.FairyStoryWarning6);
                    showResultErrorPopup(getApplicationContext(), getString(R.string.mission_error_load_stop), "music/zh/w_warning9.mp3");
                    break;

                case FailEndLoadUnMatch:
                    if (param instanceof String) {
                        // TODO: 2020/6/18 根据当前最新需要 ， 将 终点地点的逻辑 设置为 主题对应的终点地点才算到达
                        if (load instanceof StationLoad) {
                            DebugUtil.debug("FailEndLoadUnMatch -- 1");
                            showWhenAnyCondition(getApplicationContext());
                        } else if (ThemeConfig.THEME_CXTD.equals(ThemeConfig.CURRENT_THEME)) {
                            DebugUtil.debug("FailEndLoadUnMatch -- 2");
                            String rightEndLoad = "创想小站";
                            // TODO: 2020/6/19 这里需要配音 ：没有到达目标终点，目标终点是创想小站
                            String rightEndLoadMp3 = "music/zh/w_warning4.mp3";
                            showResultErrorPopup(getApplicationContext(), getString(R.string.error_target_end_load) + rightEndLoad, rightEndLoadMp3);
                        } else {
                            DebugUtil.debug("FailEndLoadUnMatch -- 3");
                            String rightEndLoadMp3 = "";
                            String action = "";
                            String rightEndLoad = (String) param;// 正确的终点道路名称
                            if (DragonEndLoad.NAME.equals(rightEndLoad)) {
                                rightEndLoadMp3 = "music/zh/w_warning6.mp3";
                                action = Command.FairyStoryWarning7;
                            } else if (MineEndLoad.NAME.equals(rightEndLoad)) {
                                rightEndLoadMp3 = "music/zh/w_warning7.mp3";
                                action = Command.FairyStoryWarning8;
                            } else if (CastleEndLoad.NAME.equals(rightEndLoad)) {
                                rightEndLoadMp3 = "music/zh/w_warning8.mp3";
                                action = Command.FairyStoryWarning9;
                            }
                            BaseApplication.sendAction(action);
                            showResultErrorPopup(getApplicationContext(), getString(R.string.error_target_end_load) + rightEndLoad, rightEndLoadMp3);
                        }

                    }

                    if (load != null) {
                        String wrongEndLoad = load.getName();// 当前走到的不匹配道路的名称
                        DebugUtil.debug("wrongEndLoad = %s", wrongEndLoad);
                    }

                    break;

                case FailLostEquipmentLoads: // 到达终点但缺少装备地垫
                    DebugUtil.debug("FailLostEquipmentLoads");
                    if (ThemeConfig.THEME_CXTD.equals(ThemeConfig.CURRENT_THEME) && load instanceof EndLoad) {
                        showResultErrorPopup(getApplicationContext(), getString(R.string.error_target_end_load) + IdeaEndLoad.NAME, "music/zh/w_warning4.mp3");
                    } else {
                        List<String> lostEquipmentLoads = new ArrayList<>(); // 缺少的装备地垫列表
                        if (param instanceof List) {
                            // 实际在运行的时候收到与主题匹配终点但还缺少的装备地垫列表
                            List params = (List) param;
                            if (params.size() > 0) {
                                for (int i = 0; i < params.size(); i++) {
                                    Object paramObj = params.get(i);
                                    if (paramObj instanceof String) {
                                        String lostEquipmentLoad = (String) paramObj;
                                        lostEquipmentLoads.add(lostEquipmentLoad);
                                    }
                                }
                            }
                        }
                        // 当丢失/未收集的装备为0时，就是正确的
                        if (lostEquipmentLoads.size() == 0) {
                            //发现终点地垫所需的装备都收集完成，显示正确的动画
                            showEndAnimation(getApplicationContext(), load);
                        } else {
                            // 发现未收集的装备大于0,那么应该就是显示错误的未收集齐装备的动画
                            showEndErrorAnimation(getApplicationContext(), load, lostEquipmentLoads);
                        }
                    }

                    break;

            }
        });
    }

    /**
     * 根据步数反馈的id,判断前进指令在哪一个卡片上
     *
     * @param id 步数反馈的id
     */
    private CardParentAction findCardParentActionByForwardId(int id) {
        if (id > 0) {
            for (CardParentAction action : data) {
                if (action.idSection != null && action.idSection.in(id)) {
                    return action;
                }
            }
        }
        return null;
    }

    /**
     * 加载动画内容识别,卡片异常
     *
     * @param stepIndex  卡片位置
     * @param baseLoad   道路
     * @param additional 道具卡片
     */
    private void onLoadAnimation(int stepIndex, @Nullable BaseLoad baseLoad, @Nullable Object additional) {
        DebugUtil.error("stepIndex = " + stepIndex);
        DebugUtil.error("BaseLoad name = " + (baseLoad != null ? baseLoad.getName() : "null"));
        DebugUtil.error("Additional class = %s , name = %s",
                additional != null ? additional.getClass().getSimpleName() : "null",
                additional instanceof Additional ? ((Additional) additional).getCards().get(0).getOid() : "null");

        if (stepIndex == -1) {
            return;
        }

        // 在所有的前进路径中找找错误的那个指令
        CardParentAction action = findCardParentActionByForwardId(stepIndex);

        if (action != null) {
            // 行动指令卡片
            if (additional != null) {
                // 当additional不为空的时候 就是 道具卡错误 , 但是同时如果是在障碍地垫上的话,也判断一下道具卡是没有使用
                if (action.childAction == null) {
                    action.childAction = new CardChildAction(CardType.ACTION_DEFAULT);
                }
                // 道具卡的状态修改成false
                action.childAction.status = false;
            } else {
                // 当additional为空的时候 就是 行动指令错误/或者是没有添加道具指令
                action.status = false;
            }
        }
    }

    /**
     * 结果页面，显示错误结果
     *
     * @param context      上下文
     * @param errorMessage 错误的时候界面上显示的信息
     * @param errorMp3     错误的时候提示的配音
     */
    private void showResultErrorPopup(final Context context, String errorMessage, @Nullable String errorMp3) {
        // 先取消Lottie动画置空
        playLottieAnimation(null);
        ResultFailPopup popup = new ResultFailPopup(this, errorMessage, errorMp3);
        popup.withEvent(v -> {
            CardControllerActivity.start(context, new Gson().toJson(data));
            MusicUtil.stopMusic();
            HistoryRecord.getInstance().clearSavePlayDataMemory();
            // 返回刷卡指令页面需清除本次记录的信息
            LoadMgr.getInstance().clear();
            finish();
        }, v -> {
            // 返回主页需清除本次记录的信息
            ThemesActivity.start(getApplicationContext());
            HistoryRecord.getInstance().clearSavePlayDataMemory();
            LoadMgr.getInstance().clear();
            finish();
        });
        popup.showPopupWindow();
    }

    /**
     * 走上6个彩色小站的地垫的时候需要做的事情
     */
    private void showWhenAnyCondition(final Context context) {

        int nameIndex = emotionNames.length > 0 ? new Random().nextInt(emotionNames.length) : -1;
        int mp3Index = emotionMp3.length > 0 ? new Random().nextInt(emotionMp3.length) : -1;
        DebugUtil.debug("nameIndex = %d , mp3Index = %d", nameIndex, mp3Index);
        if (nameIndex > -1 && mp3Index > -1) {
            try {
                playLottieAnimation(emotionNames[nameIndex]);
                MusicUtil.playAssetsAudio(emotionMp3[mp3Index], mediaPlayer -> runOnUiThread(() -> showResultSuccessPopup(context)));
            } catch (Exception e) {
                DebugUtil.error(e.getMessage());
                showResultSuccessPopup(context);
            }
        } else {
            showResultSuccessPopup(context);
        }

    }

    /**
     * 收集到所有正确的装备，显示最终到达的动画和结果
     *
     * @param context 上下文
     * @param load    终点
     */
    private void showEndAnimation(final Context context, final BaseLoad load) {

        // 先取消Lottie动画置空
        playLottieAnimation(null);

        // 加载播放的动画
        View view = LayoutInflater.from(context).inflate(R.layout.popup_result_end_view, getRootView(), false);

        FrameSurfaceView frameSurfaceView = view.findViewById(R.id.frame_surface);

        frameSurfaceView.setIsRepeat(false);

        int endType = -1;

        if (load instanceof CastleEndLoad) {
            frameSurfaceView.setGapTime(IntroduceTime.ballEndTime);
            frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.danceEndImages);
        } else if (load instanceof MineEndLoad) {
            switch (new Random().nextInt(300) % 3) {
                case 0:
                    endType = 0;
                    frameSurfaceView.setGapTime(IntroduceTime.seafloorEndTime);
                    frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.seafloorEndImages);
                    break;
                case 1:
                    endType = 1;
                    frameSurfaceView.setGapTime(IntroduceTime.spiritEndTime);
                    frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.spiritEndImages);
                    break;
                case 2:
                    endType = 2;
                    frameSurfaceView.setGapTime(IntroduceTime.universeEndTime);
                    frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.universeEndImages);
                    break;
            }
        } else if (load instanceof DragonEndLoad) {
            frameSurfaceView.setGapTime(IntroduceTime.heroEndTime);
            frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.fairEndImages);
        }

        final Pair<String, String> endLoadResult = translateEndLoadResultMp3(true, load, endType, null);

        frameSurfaceView.setOnFrameFinishedListener(new FrameSurfaceView.OnFrameFinishedListener() {

            private boolean isMusicFinished = false;
            private boolean isFrameFinished = false;

            @Override
            public void onFrameStart() {

                // 结束动画配音
                MusicUtil.playAssetsAudio(endLoadResult.first, mediaPlayer -> {
                    DebugUtil.debug("onStart isMusicFinished = " + isMusicFinished + ", isFrameFinished = " + isFrameFinished);
                    isMusicFinished = true;
                    if (isFrameFinished) {
                        runOnUiThread(() -> {
                            // removeView("RESULT_POPUP_END_RIGHT");
                            showResultSuccessPopup(context);
                        });
                    }
                });

            }

            @Override
            public void onFrameFinish() {
                DebugUtil.debug("onFinish isMusicFinished = " + isMusicFinished + ", isFrameFinished = " + isFrameFinished);
                isFrameFinished = true;
                if (isMusicFinished) {
                    runOnUiThread(() -> showResultSuccessPopup(context));
                }
            }
        });

        BaseApplication.sendAction(endLoadResult.second);

        // 附加到界面显示
        addView(view, "RESULT_POPUP_END_RIGHT");

        frameSurfaceView.start();

    }

    /**
     * 到达终点地垫，但是出现了错误
     *
     * @param context       上下文
     * @param load          终点地垫
     * @param lostEquipment 对应没有收集到的装备地垫
     */
    private void showEndErrorAnimation(final Context context, final BaseLoad load, final List<String> lostEquipment) {

        // 先取消Lottie动画置空
        playLottieAnimation(null);

        // 加载播放的动画
        View view = LayoutInflater.from(context).inflate(R.layout.popup_result_end_lost_equipment_view, getRootView(), false);

        // 背景底图动画
        FrameSurfaceView frameSurfaceView = view.findViewById(R.id.frame_surface);
        frameSurfaceView.setIsRepeat(false);

        int endType = -1;
        if (load instanceof CastleEndLoad) {
            frameSurfaceView.setGapTime(IntroduceTime.ballFailEndTime);
            frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.danceUnEndImages);
        } else if (load instanceof MineEndLoad) {
            // 在宝藏终点地垫中，需要播放三种不同动画
            switch (new Random().nextInt(300) % 3) {
                case 0:
                    endType = 0;
                    frameSurfaceView.setGapTime(IntroduceTime.seafloorFailEndTime);
                    frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.seafloorUnEndImages);
                    break;
                case 1:
                    endType = 1;
                    frameSurfaceView.setGapTime(IntroduceTime.spiritFailEndTime);
                    frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.spiritUnEndImages);
                    break;
                case 2:
                    endType = 2;
                    frameSurfaceView.setGapTime(IntroduceTime.universeFailEndTime);
                    frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.universeUnEndImages);
                    break;
            }
        } else if (load instanceof DragonEndLoad) {
            frameSurfaceView.setGapTime(IntroduceTime.heroFailEndTime);
            frameSurfaceView.setBitmapResoursID(IntroduceImageArrays.fairUnEndImages);
        }
        final Pair<String, String> endLoadResult = translateEndLoadResultMp3(false, load, endType, lostEquipment);

        DebugUtil.debug("endLoadBgm = %s", endLoadResult.first);

        // 到达终点地垫的时候，需要的所有装备
        String[] equipmentLoads = ((EndLoad) load).getEquipmentLoads();

        ImageView equipment1 = view.findViewById(R.id.equipment_1);
        ImageView equipment2 = view.findViewById(R.id.equipment_2);
        ImageView equipment3 = view.findViewById(R.id.equipment_3);

        // 创建动画的时候需要判断这个装备是否收集到了
        // 如果丢失的列表里包含需要的装备，那么就是丢失 , 需要显示问号
        // 如果丢失的列表里不包含需要的装备，那么就是收集到了 , 需要显示收集到的这个装备
        String equipmentName = lostEquipment.contains(equipmentLoads[0]) ? "" : equipmentLoads[0];
        final Animation anim1 = createEquipmentFrameAnim(context, equipment1, equipmentName);

        String equipmentName1 = lostEquipment.contains(equipmentLoads[1]) ? "" : equipmentLoads[1];
        final Animation anim2 = createEquipmentFrameAnim(context, equipment2, equipmentName1);

        String equipmentName2 = lostEquipment.contains(equipmentLoads[2]) ? "" : equipmentLoads[2];
        final Animation anim3 = createEquipmentFrameAnim(context, equipment3, equipmentName2);

        frameSurfaceView.setOnFrameFinishedListener(new FrameSurfaceView.OnFrameFinishedListener() {

            private final AtomicBoolean isMusicFinished = new AtomicBoolean(false);
            private final AtomicBoolean isFrameFinished = new AtomicBoolean(false);

            @Override
            public void onFrameStart() {

                runOnUiThread(() -> {
                    // 开始装备的动画
                    anim1.start();
                    anim2.start();
                    anim3.start();
                });

                MusicUtil.playAssetsAudio(endLoadResult.first, mediaPlayer -> runOnUiThread(() -> {
                    isMusicFinished.set(true);
                    if (isFrameFinished.get()) {
                        // 为了鼓励小朋友继续游戏,现在显示成功的提示结果
                        showResultSuccessPopup(context);
                    }
                }));
            }

            @Override
            public void onFrameFinish() {

                runOnUiThread(() -> {
                    // 结束装备的动画
                    anim1.cancel();
                    anim2.cancel();
                    anim3.cancel();
                    // 显示错误的结果
                    // showResultErrorPopup(context, "我还没有收集齐装备\n快去收集装备吧","");
                    isFrameFinished.set(true);
                    if (isMusicFinished.get()) {
                        // 为了鼓励小朋友继续游戏,现在显示成功的提示结果
                        showResultSuccessPopup(context);
                    }
                });
            }
        });

        // 根据不同的游戏场景，选择播放音乐的下标，接收端将会根据游戏场景来选择不同的音乐
        BaseApplication.sendAction(endLoadResult.second);

        // 附加到界面显示
        addView(view, "RESULT_POPUP_END_ERROR");

        frameSurfaceView.start();

    }

    /**
     * 正确到达终点时显示成功完成游戏的数据统计
     */
    private void showResultSuccessPopup(final Context context) {
        // 先取消Lottie动画置空
        ResultSuccessPopup popup = new ResultSuccessPopup(this, data);
        popup.withEvent(v -> {
            HistoryRecord.getInstance().clearSavePlayDataMemory();
            LoadMgr.getInstance().clear();
            BaseApplication.getInstance().release();
        }, v -> {
            HistoryRecord.getInstance().clearSavePlayDataMemory();
            CardControllerActivity.start(context, new Gson().toJson(data));
            LoadMgr.getInstance().clear();
            finish();
        }, v -> {
            v.setEnabled(false);
            final long saveId = HistoryRecord.getInstance().save();
            SaveGamePopup popup1 = new SaveGamePopup(RuntimeActivity.this, saveId);
            popup1.withConfirm(v1 -> {
                TellStoryActivity.start(RuntimeActivity.this, HistoryRecord.getInstance().getItem(String.valueOf(saveId)));
                HistoryRecord.getInstance().clearSavePlayDataMemory();
                LoadMgr.getInstance().clear();
            }, null);
            popup1.showPopupWindow();
        });
        popup.showPopupWindow();
    }

    /**
     * 到达终点的时候，根据状态和装备的数量判断使用的终点音频和动作
     *
     * @param isSuccess      是否成功
     * @param endLoad        终点地垫
     * @param endLoadType    终点动画类型（只有宝藏地垫有三种不同的动画，其他的值可为-1）
     * @param lostEquipments 终点动画缺少装备
     * @return pair = first 播放的音频，second 播放的动作
     */
    private Pair<String, String> translateEndLoadResultMp3(boolean isSuccess, BaseLoad endLoad, int endLoadType, @Nullable List<String> lostEquipments) {

        // 成功收集齐装备地垫到达终点播放文件
        if (isSuccess) {
            if (endLoad instanceof CastleEndLoad) {
                return new Pair<>("tts/zh/ball_success1.mp3", Command.FairyStoryEndBall);
            } else if (endLoad instanceof MineEndLoad) {
                switch (endLoadType) {
                    case 0:
                        return new Pair<>("tts/zh/treasure_success2.mp3", Command.FairyStoryEndSeafloor);
                    case 1:
                        return new Pair<>("tts/zh/treasure_success3.mp3", Command.FairyStoryEndSpirit);
                    case 2:
                        return new Pair<>("tts/zh/treasure_success1.mp3", Command.FairyStoryEndUniverse);
                }
            } else if (endLoad instanceof DragonEndLoad) {
                return new Pair<>("tts/zh/hero_success1.mp3", Command.FairyStoryEndHero);
            }
        } else {
            // 没有完整收集齐装备地垫到达终点播放文件
            if (endLoad instanceof CastleEndLoad) {
                if (lostEquipments != null) {
                    if (lostEquipments.size() == 3) {
                        return new Pair<>("tts/zh/ball_fail7.mp3", Command.FairyStoryBallFail7);
                    } else if (lostEquipments.size() == 2) {
                        if (!lostEquipments.contains(DanceSkirtLoad.NAME)) {
                            return new Pair<>("tts/zh/ball_fail6.mp3", Command.FairyStoryBallFail6);
                        } else if (!lostEquipments.contains(CrystalShoesLoad.NAME)) {
                            return new Pair<>("tts/zh/ball_fail5.mp3", Command.FairyStoryBallFail5);
                        } else if (!lostEquipments.contains(FatTonnyLoad.NAME)) {
                            return new Pair<>("tts/zh/ball_fail4.mp3", Command.FairyStoryBallFail4);
                        }
                    } else if (lostEquipments.size() == 1) {
                        if (FatTonnyLoad.NAME.equals(lostEquipments.get(0))) {
                            return new Pair<>("tts/zh/ball_fail3.mp3", Command.FairyStoryBallFail3);
                        } else if (CrystalShoesLoad.NAME.equals(lostEquipments.get(0))) {
                            return new Pair<>("tts/zh/ball_fail2.mp3", Command.FairyStoryBallFail2);
                        } else if (DanceSkirtLoad.NAME.equals(lostEquipments.get(0))) {
                            return new Pair<>("tts/zh/ball_fail1.mp3", Command.FairyStoryBallFail1);
                        }
                    }
                }
            } else if (endLoad instanceof MineEndLoad) {
                if (lostEquipments != null) {
                    if (lostEquipments.size() == 3) {
                        if (endLoadType == 0) {
                            return new Pair<>("tts/zh/treasure_fail7.mp3", Command.FairyStorySeafloorFail7);
                        } else if (endLoadType == 1) {
                            return new Pair<>("tts/zh/treasure_fail7.mp3", Command.FairyStorySpiritFail7);
                        } else {
                            return new Pair<>("tts/zh/treasure_fail7.mp3", Command.FairyStoryTreasureFail7);
                        }
                    } else if (lostEquipments.size() == 2) {

                        if (!lostEquipments.contains(TreasureMapLoad.NAME)) {
                            if (endLoadType == 0) {
                                return new Pair<>("tts/zh/treasure_fail6.mp3", Command.FairyStorySeafloorFail6);
                            } else if (endLoadType == 1) {
                                return new Pair<>("tts/zh/treasure_fail6.mp3", Command.FairyStorySpiritFail6);
                            } else {
                                return new Pair<>("tts/zh/treasure_fail6.mp3", Command.FairyStoryTreasureFail6);
                            }
                        } else if (!lostEquipments.contains(CompassLoad.NAME)) {
                            if (endLoadType == 0) {
                                return new Pair<>("tts/zh/treasure_fail5.mp3", Command.FairyStorySeafloorFail5);
                            } else if (endLoadType == 1) {
                                return new Pair<>("tts/zh/treasure_fail5.mp3", Command.FairyStorySpiritFail5);
                            } else {
                                return new Pair<>("tts/zh/treasure_fail5.mp3", Command.FairyStoryTreasureFail5);
                            }
                        } else if (!lostEquipments.contains(KeyLoad.NAME)) {
                            if (endLoadType == 0) {
                                return new Pair<>("tts/zh/treasure_fail4.mp3", Command.FairyStorySeafloorFail4);
                            } else if (endLoadType == 1) {
                                return new Pair<>("tts/zh/treasure_fail4.mp3", Command.FairyStorySpiritFail4);
                            } else {
                                return new Pair<>("tts/zh/treasure_fail4.mp3", Command.FairyStoryTreasureFail4);
                            }
                        }
                    } else if (lostEquipments.size() == 1) {
                        if (KeyLoad.NAME.equals(lostEquipments.get(0))) {
                            if (endLoadType == 0) {
                                return new Pair<>("tts/zh/treasure_fail3.mp3", Command.FairyStorySeafloorFail3);
                            } else if (endLoadType == 1) {
                                return new Pair<>("tts/zh/treasure_fail3.mp3", Command.FairyStorySpiritFail3);
                            } else {
                                return new Pair<>("tts/zh/treasure_fail3.mp3", Command.FairyStoryTreasureFail3);
                            }
                        } else if (CompassLoad.NAME.equals(lostEquipments.get(0))) {
                            if (endLoadType == 0) {
                                return new Pair<>("tts/zh/treasure_fail2.mp3", Command.FairyStorySeafloorFail2);
                            } else if (endLoadType == 1) {
                                return new Pair<>("tts/zh/treasure_fail2.mp3", Command.FairyStorySpiritFail2);
                            } else {
                                return new Pair<>("tts/zh/treasure_fail2.mp3", Command.FairyStoryTreasureFail2);
                            }
                        } else if (TreasureMapLoad.NAME.equals(lostEquipments.get(0))) {
                            if (endLoadType == 0) {
                                return new Pair<>("tts/zh/treasure_fail1.mp3", Command.FairyStorySeafloorFail1);
                            } else if (endLoadType == 1) {
                                return new Pair<>("tts/zh/treasure_fail1.mp3", Command.FairyStorySpiritFail1);
                            } else {
                                return new Pair<>("tts/zh/treasure_fail1.mp3", Command.FairyStoryTreasureFail1);
                            }
                        }
                    }
                }
            } else if (endLoad instanceof DragonEndLoad) {
                if (lostEquipments != null) {
                    if (lostEquipments.size() == 3) {
                        return new Pair<>("tts/zh/hero_fail7.mp3", Command.FairyStoryHeroFail7);
                    } else if (lostEquipments.size() == 2) {
                        if (!lostEquipments.contains(PegasusLoad.NAME)) {
                            return new Pair<>("tts/zh/hero_fail6.mp3", Command.FairyStoryHeroFail6);
                        } else if (!lostEquipments.contains(SwordLoad.NAME)) {
                            return new Pair<>("tts/zh/hero_fail5.mp3", Command.FairyStoryHeroFail5);
                        } else if (!lostEquipments.contains(ArmorLoad.NAME)) {
                            return new Pair<>("tts/zh/hero_fail4.mp3", Command.FairyStoryHeroFail4);
                        }
                    } else if (lostEquipments.size() == 1) {
                        if (ArmorLoad.NAME.equals(lostEquipments.get(0))) {
                            return new Pair<>("tts/zh/hero_fail3.mp3", Command.FairyStoryHeroFail3);
                        } else if (SwordLoad.NAME.equals(lostEquipments.get(0))) {
                            return new Pair<>("tts/zh/hero_fail2.mp3", Command.FairyStoryHeroFail2);
                        } else if (PegasusLoad.NAME.equals(lostEquipments.get(0))) {
                            return new Pair<>("tts/zh/hero_fail1.mp3", Command.FairyStoryHeroFail1);
                        }
                    }
                }
            }
        }

        return new Pair<>("", "");
    }

    /**
     * 创建装备显示的动画
     *
     * @param equipment     需要填充装备的View
     * @param equipmentName 装备的名称
     */
    @NonNull
    private Animation createEquipmentFrameAnim(Context context, ImageView equipment, String equipmentName) {
        equipment.setImageResource(CardResource.transformEquipmentName(equipmentName));
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.equipment_anim);
        equipment.setAnimation(animation);
        return animation;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Director.getInstance().setView(null);
        robotCar.release();
    }

}