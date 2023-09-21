package com.bearya.robot.fairystory.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.can.Body;
import com.bearya.robot.base.can.CanDataListener;
import com.bearya.robot.base.can.CanManager;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.adapter.CardActionsAdapter;
import com.bearya.robot.fairystory.ui.popup.impl.ActionCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.CardUpdatePopup;
import com.bearya.robot.fairystory.ui.popup.impl.CountPopup;
import com.bearya.robot.fairystory.ui.popup.impl.DeleteConfirmPopup;
import com.bearya.robot.fairystory.ui.popup.impl.EmptyActionPopup;
import com.bearya.robot.fairystory.ui.popup.impl.ErrorParallelCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.ErrorPropCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.FunctionCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.FunctionPreviewPopup;
import com.bearya.robot.fairystory.ui.popup.impl.PropCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.PutCenterPopup;
import com.bearya.robot.fairystory.ui.res.CardChildAction;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardResource;
import com.bearya.robot.fairystory.ui.res.CardType;
import com.bearya.robot.fairystory.ui.res.Command;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 行动指令 添加 / 刷卡，附加并行卡或者道具卡
 */
public class CardControllerActivity extends BaseActivity implements View.OnClickListener, CanDataListener {

    private RecyclerView cardActionRecyclerView;
    private CardActionsAdapter adapter;
    private Animator animator; // 尾部添加+的属性动画
    private long lastOidUpdateTime = System.currentTimeMillis(); // 最新一次读取oid的时间
    private boolean isInStartLoad = false; // 是否在起点位置
    private final Runnable repeatRefreshCardAudioRunnable = new Runnable() {
        @Override
        public void run() {
            // 每30秒提示一次小朋友刷卡或者点击添加行动指令，有行动指令不提示
            if (adapter.getData().size() == 0) {
                MusicUtil.playAssetsAudio("card/zh/p_guide.mp3");
            }
            repeatRefreshCardAudio();
        }
    };
    private final Handler handler = new Handler();

    /**
     * 刷卡执行内容
     *
     * @param actions 这个必须是List<CardParentAction>转换成的JSON字符串，或者直接为空
     */
    public static void start(Context context, String actions) {
        context.startActivity(new Intent(context, CardControllerActivity.class).putExtra("actions", actions));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_controller);

        cardActionRecyclerView = findViewById(R.id.card_action);
        cardActionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new CardActionsAdapter();
        // 添加底部会放大缩小的添加框
        View footer = getLayoutInflater().inflate(R.layout.item_card_add, cardActionRecyclerView, false);
        animator = AnimatorInflater.loadAnimator(this, R.animator.card_add_animator);
        animator.setTarget(footer);
        footer.setOnClickListener(this);
        adapter.addFooterView(footer, 0, LinearLayout.HORIZONTAL);
        adapter.setFooterViewAsFlow(false);
        adapter.isFirstOnly(false);
        adapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
            if (view.getId() == R.id.parent_action) {// 行动指令
                showPopupParent(position);
            } else if (view.getId() == R.id.step) {// 步数指令
                showPopupStep(position);
            } else if (view.getId() == R.id.child_action) {// 道具指令
                showPopupProp(position);
            }
        });
        adapter.setOnItemChildLongClickListener((baseQuickAdapter, view, position) -> {
            if (view.getId() == R.id.parent_action || view.getId() == R.id.step) {// 行动指令和步数指令
                showPopupDeleteParent(position);
            } else if (view.getId() == R.id.child_action) {// 道具指令
                CardParentAction item = adapter.getItem(position);
                if (item != null && item.parentActionId == CardType.ACTION_FUNCTION_CALL) {
                    showPopupPreviewFunction(item);
                } else if (item != null) {
                    showPopupDeleteChild(position, item);
                }
            }
            return true;
        });
        if (getIntent() != null) {
            String actions = getIntent().getStringExtra("actions");
            if (actions != null) {
                Type type = new TypeToken<List<CardParentAction>>() {
                }.getType();
                adapter.setNewData((new Gson().fromJson(actions, type)));
            }
        } else {
            adapter.setNewData(new ArrayList<>());
        }

        adapter.bindToRecyclerView(cardActionRecyclerView);

        withClick(R.id.btnBack, view -> {
            CanManager.getInstance().removeListener(CardControllerActivity.this);
            handler.removeCallbacks(repeatRefreshCardAudioRunnable);
            finish();
        });
        // 执行
        withClick(R.id.doRun, view -> {
            List<CardParentAction> data = adapter.getData();
            String lastCommand = new Gson().toJson(data);
            MMKV.defaultMMKV().encode("lastCommand", lastCommand);
            checkStartLoadToRun();
        });
        // 函数定义
//        withClick(R.id.function, view -> FunctionActivity.start(this));
        // 编程积木
//        findViewById(R.id.blocks).setVisibility(BaseApplication.isEnglish ? View.GONE : View.VISIBLE);
//        if (!BaseApplication.isEnglish)
//            withClick(R.id.blocks, v -> handyBlocks());
//        findViewById(R.id.blocks).setVisibility(BaseApplication.isEnglish ? View.GONE : View.VISIBLE);
//        if (!BaseApplication.isEnglish)
//            withClick(R.id.blocks, v -> handyBlocks());
        // 导入上一次编程的指令
        withClick(R.id.load_command, v -> loadLastCommand());

//        HandyBlockObserver handyBlockObserver = new HandyBlockObserver(this);
//        handyBlockObserver.setBlockCallback(new HandyBlockObserver.BlockCallback() {
//            @Override
//            public void onBlockDef(List<CardParentAction> cards) {
//                adapter.setNewData(cards);
//                MusicUtil.playAssetsAudio("tts/zh/handy_block_accept.mp3", mp -> checkStartLoadToRun());
//            }
//
//            @Override
//            public void onBlockMessage(String message, int icon) {
//                DebugUtil.info("message = %s , icon = %d", message, icon);
//            }
//        });

        RobotActionManager.reset();
    }


    @Override
    protected void onStart() {
        super.onStart();
        BaseApplication.sendAction(Command.FairyStoryEditCard);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        animator.start();
        // 读取oid事件的时间间隔
        lastOidUpdateTime = System.currentTimeMillis();
        CanManager.getInstance().addListener(this);
        handler.post(repeatRefreshCardAudioRunnable);
    }

    /**
     * 定时30s播放刷卡提示
     */
    private void repeatRefreshCardAudio() {
        handler.removeCallbacks(repeatRefreshCardAudioRunnable);
        handler.postDelayed(repeatRefreshCardAudioRunnable, 30 * 1000);
    }

//    private void handyBlocks() {
//        new ImagePreViewDialog(this,
//                new FacePlay(String.valueOf(R.mipmap.handy_block_combine), FaceType.Image)).show();
//    }

    /**
     * 检查起点位置启动
     */
    private void checkStartLoadToRun() {
        final List<CardParentAction> data = adapter.getData();
        if (data.size() == 0) {
            new EmptyActionPopup(this).showPopupWindow();
            return;
        }
        for (CardParentAction action : data) {
            action.status = true;
            if (action.childAction != null) {
                action.status = true;
            }
        }
        adapter.notifyItemRangeChanged(0, data.size());
        int loopCloseable = 0, errorIndex = -1, actionIndex = -1;
        for (int i = 0; i < data.size(); i++) {
            CardParentAction cardParentAction = data.get(i);
            if (cardParentAction.parentActionId == CardType.ACTION_LOOP) {
                loopCloseable++;
                actionIndex = i;
                if (loopCloseable >= 2) {
                    errorIndex = i;
                    break;
                }
            } else if (cardParentAction.parentActionId == CardType.ACTION_CLOSURE) {
                loopCloseable--;
                actionIndex = i;
                if (loopCloseable < 0) {
                    errorIndex = i;
                    break;
                }
            }
        }
        if (loopCloseable != 0 && errorIndex > -1) {
            showLoopUnclosed(errorIndex);
        } else if (loopCloseable == 1) {
            showLoopUnclosed(actionIndex);
        } else if (isInStartLoad) {
            doRun();
        } else {
            PutCenterPopup putCenterPopup = new PutCenterPopup(this);
            putCenterPopup.setInCenterListener(this::doRun);
            putCenterPopup.showPopupWindow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CanManager.getInstance().removeListener(this);
        handler.removeCallbacks(repeatRefreshCardAudioRunnable);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.card_add) {
            MusicUtil.playAssetsAudio("card/zh/p_add.mp3");
            showPopupCardActions(-1, false);
        }
    }

    private void loadLastCommand() {
        String lastCommand = MMKV.defaultMMKV().decodeString("lastCommand");
        Type type = new TypeToken<List<CardParentAction>>() {
        }.getType();
        adapter.setNewData(new Gson().fromJson(lastCommand, type));
    }

    /**
     * 最终执行的动作
     */
    private void doRun() {
        CanManager.getInstance().removeListener(this);
        BaseApplication.getInstance().moveALittle(true);
        List<CardParentAction> data = adapter.getData();
        String lastCommand = new Gson().toJson(data);
        RuntimeActivity.start(this, lastCommand);
        finish();
    }

    private void showLoopUnclosed(int cardIndex) {
        CardParentAction cardParentAction = adapter.getItem(cardIndex);
        if (cardParentAction != null) cardParentAction.status = false;
        adapter.notifyItemChanged(cardIndex);
        MusicUtil.playAssetsAudio("card/zh/p_warning10.mp3");
    }

    /**
     * 弹出框，选择行动卡
     */
    private void showPopupCardActions(final int insertPosition, final boolean isUpdate) {
        ActionCardPopup actionCardPopup = new ActionCardPopup(this);
        actionCardPopup.setPopupViewClickListener(cardType -> {
            switch (cardType) {
                case CardType.ACTION_BACKWARD:
                case CardType.ACTION_FORWARD:
                case CardType.ACTION_LEFT:
                case CardType.ACTION_RIGHT:
                case CardType.ACTION_LOOP:
                case CardType.ACTION_CLOSURE:
                case CardType.ACTION_FUNCTION_CALL:
                    popupWithClick(insertPosition, cardType, isUpdate);
                    break;
                case CardType.ACTION_BOAT:
                case CardType.ACTION_MAGIC:
                case CardType.ACTION_SOLDIER:
                case CardType.ACTION_NEEDLES:
                case CardType.ACTION_WATER:
                case CardType.ACTION_FLUTE:
                case CardType.ACTION_BULLET:
                case CardType.ACTION_STICK:
                    MusicUtil.playAssetsAudio("card/zh/p_warning2.mp3");
                    break;
                case CardType.ACTION_PARALLEL:
                    MusicUtil.playAssetsAudio("card/zh/p_warning1.mp3");
                    break;
            }
        });
        actionCardPopup.showPopupWindow();
    }

    /**
     * 行动卡选择后填充
     *
     * @param insertPosition -1 默认往后面添加
     */
    private void popupWithClick(int insertPosition, int cardType, boolean isUpdate) {
        if (isUpdate) {
            adapter.setData(insertPosition, new CardParentAction(cardType));
        } else {
            if (insertPosition == -1) {
                adapter.addData(new CardParentAction(cardType));
                cardActionRecyclerView.smoothScrollToPosition(adapter.getData().size());
            } else {
                adapter.addData(insertPosition, new CardParentAction(cardType));
                int itemCount = adapter.getData().size() - insertPosition - 1;
                if (itemCount > 0) {
                    adapter.notifyItemRangeChanged(insertPosition + 1, itemCount);
                }
                cardActionRecyclerView.smoothScrollToPosition(insertPosition);
            }
        }
        MusicUtil.playAssetsAudio(CardResource.cardVoice(cardType));
    }

    @Override
    protected void onStop() {
        super.onStop();
        animator.cancel();
    }

    /**
     * 行动指令 的 选择 / 替换 / 添加 / 并行
     */
    private void showPopupParent(final int position) {
        CardParentAction item = adapter.getItem(position);
        if (item != null) {
            if (item.parentActionId != -1) {
                CardUpdatePopup popup = new CardUpdatePopup(this);
                if (item.parentActionId != CardType.ACTION_FORWARD) {
                    popup.hideParallelCard();
                }
                popup.setPopupViewClickListener(cardType -> updateCardAction(position, cardType));
                popup.showPopupWindow();
            } else {
                showPopupCardActions(position, true);
            }
        }
    }

    /**
     * 弹出框 ，选择附属卡
     */
    private void showPopupProp(final int position) {
        final CardParentAction parentAction = adapter.getItem(position);
        if (parentAction != null) {
            if (parentAction.parentActionId == CardType.ACTION_FUNCTION_CALL) {
                FunctionCardPopup functionCardPopup = new FunctionCardPopup(this);
                functionCardPopup.setPopupViewClickListener(cardType -> {
                    parentAction.childAction = new CardChildAction(cardType);
                    parentAction.childAction.status = true;
                    adapter.notifyItemChanged(position);
                });
                functionCardPopup.showPopupWindow();
            } else {
                PropCardPopup propCardPopup = new PropCardPopup(this);
                propCardPopup.setPopupViewClickListener(cardType -> {
                    parentAction.childAction = new CardChildAction(cardType);
                    parentAction.childAction.status = true;
                    adapter.notifyItemChanged(position);
                });
                propCardPopup.showPopupWindow();
            }
        }
    }

    /**
     * 弹出框, 选择步数
     */
    private void showPopupStep(final int stepPosition) {
        CountPopup countPopup = new CountPopup(this);
        countPopup.setPopupCountClickListener(count -> {
            CardParentAction parentAction = adapter.getItem(stepPosition);
            if (parentAction != null) {
                parentAction.stepCount = count;
            }
            adapter.notifyItemChanged(stepPosition);
        });
        countPopup.showPopupWindow();
    }

    /**
     * 删除整个指令，包含附加道具指令
     *
     * @param position 删除位置
     */
    private void showPopupDeleteParent(final int position) {
        DeleteConfirmPopup deleteConfirmPopup = new DeleteConfirmPopup(this);
        deleteConfirmPopup.applyShowAudio("card/zh/p_delete_certain.mp3");
        deleteConfirmPopup.withConfirm(v -> adapter.remove(position), v -> MusicUtil.stopMusic());
        deleteConfirmPopup.showPopupWindow();
    }

    /**
     * 删除附加道具指令，不删除行动指令
     *
     * @param position 父指令位置
     */
    private void showPopupDeleteChild(final int position, final CardParentAction cardParentAction) {
        DeleteConfirmPopup deleteConfirmPopup = new DeleteConfirmPopup(this);
        deleteConfirmPopup.applyShowAudio("card/zh/p_delete_certain.mp3");
        deleteConfirmPopup.withConfirm(v -> {
            if (cardParentAction != null) {
                cardParentAction.childAction = null;
                adapter.notifyItemChanged(position);
            }
        }, v -> MusicUtil.stopMusic());
        deleteConfirmPopup.showPopupWindow();
    }

    private void showPopupPreviewFunction(final CardParentAction item) {
        FunctionPreviewPopup functionPreviewPopup = new FunctionPreviewPopup(this, item.childAction.childActionId);
        functionPreviewPopup.withDefine(view1 -> DefineFunctionActivity.start(this, item.childAction.childActionId));
        functionPreviewPopup.showPopupWindow();
    }

    private void updateCardAction(int position, int cardType) {
        if (cardType == CardType.ACTION_PARALLEL) {
            CardParentAction item = adapter.getItem(position);
            if (item != null && item.parentActionId == CardType.ACTION_FORWARD) {
                item.childAction = new CardChildAction(CardType.ACTION_DEFAULT);
                item.status = true;
                adapter.notifyItemChanged(position);
            } else {
                new ErrorParallelCardPopup(this).showPopupWindow();
            }
        } else if (cardType == CardType.ACTION_INSERT_LEFT) {
            showPopupCardActions(position, false);
        } else if (cardType == CardType.ACTION_INSERT_RIGHT) {
            showPopupCardActions(position + 1, false);
        } else {
            adapter.setData(position, new CardParentAction(cardType));
        }
    }

    @Override
    public void onFrontOid(final int oid) {
        isInStartLoad = oid >= 26476 && oid <= 27375;
        runOnUiThread(() -> appendCardAction(oid));
    }

    /**
     * 收到前读头oid时处理数据
     */
    @MainThread
    private void appendCardAction(int cardType) {
        if ((System.currentTimeMillis() - lastOidUpdateTime) < 1200L) {
            return;
        }
        lastOidUpdateTime = System.currentTimeMillis();
        switch (cardType) {
            case CardType.ACTION_LOOP:
            case CardType.ACTION_CLOSURE:
            case CardType.ACTION_FORWARD:
            case CardType.ACTION_BACKWARD:
            case CardType.ACTION_LEFT:
            case CardType.ACTION_RIGHT:
                adapter.addData(new CardParentAction(cardType));
                cardActionRecyclerView.smoothScrollToPosition(adapter.getData().size());
                MusicUtil.playAssetsAudio(CardResource.cardVoice(cardType));
                break;
            case CardType.ACTION_PARALLEL:
                List<CardParentAction> data = adapter.getData();
                if (data.size() == 0) {
                    new ErrorParallelCardPopup(this).showPopupWindow();
                    break;
                }
                int position = data.size() - 1;
                CardParentAction cardParentAction = data.get(position);
                if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                    cardParentAction.childAction = new CardChildAction(CardType.ACTION_DEFAULT);
                    cardParentAction.status = true;
                    adapter.notifyItemChanged(position);
                    MusicUtil.playAssetsAudio(CardResource.cardVoice(cardType));
                } else {
                    new ErrorParallelCardPopup(this).showPopupWindow();
                }
                break;
            case CardType.ACTION_BOAT:
            case CardType.ACTION_MAGIC:
            case CardType.ACTION_SOLDIER:
            case CardType.ACTION_NEEDLES:
            case CardType.ACTION_WATER:
            case CardType.ACTION_FLUTE:
            case CardType.ACTION_BULLET:
            case CardType.ACTION_STICK:
                List<CardParentAction> data2 = adapter.getData();
                if (data2.size() == 0) {
                    new ErrorPropCardPopup(this).showPopupWindow();
                } else {
                    int position2 = data2.size() - 1;
                    CardParentAction cardParentAction2 = data2.get(position2);
                    if (cardParentAction2 != null && cardParentAction2.parentActionId == CardType.ACTION_FORWARD && cardParentAction2.childAction != null) {
                        cardParentAction2.childAction = new CardChildAction(cardType);
                        cardParentAction2.status = true;
                        adapter.notifyItemChanged(position2);
                        MusicUtil.playAssetsAudio(CardResource.cardVoice(cardType));
                    } else {
                        new ErrorPropCardPopup(this).showPopupWindow();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackOid(int oid) {

    }

    @Override
    public void onTouchBody(Body body) {

    }

}