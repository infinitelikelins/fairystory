package com.bearya.robot.fairystory.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bearya.actionlib.utils.SharedPreferencesUtil;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.adapter.CardActionsAdapter;
import com.bearya.robot.fairystory.ui.popup.impl.CountPopup;
import com.bearya.robot.fairystory.ui.popup.impl.DefineFunctionCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.DefineFunctionCardUpdatePopup;
import com.bearya.robot.fairystory.ui.popup.impl.DeleteConfirmPopup;
import com.bearya.robot.fairystory.ui.popup.impl.ErrorParallelCardPopup;
import com.bearya.robot.fairystory.ui.popup.impl.PropCardPopup;
import com.bearya.robot.fairystory.ui.res.CardChildAction;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardResource;
import com.bearya.robot.fairystory.ui.res.CardType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DefineFunctionActivity extends BaseActivity implements View.OnClickListener {

    public static void start(Context context, int functionType) {
        context.startActivity(new Intent(context, DefineFunctionActivity.class).putExtra("define_function_type", functionType));
    }

    private String functionKey;
    private RecyclerView cardActionRecyclerView;
    private CardActionsAdapter adapter;
    private Animator animator; // 尾部添加+的属性动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_define_function);

        int defineFunctionType = getIntent().getIntExtra("define_function_type", CardType.ACTION_FUNCTION_1);
        functionKey = "DEFINE_FUNCTION_" + defineFunctionType;

        withClick(R.id.btnBack, view -> finish());
        withClick(R.id.save_function, view -> saveFunction());
        withClick(R.id.clean, view -> cleanFunction());

        ((AppCompatTextView) findViewById(R.id.function_type))
                .setText(String.format(getString(R.string.define_function_num), defineFunctionType - CardType.ACTION_FUNCTION_1 + 1));

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
                showPopupDeleteChild(position, adapter.getItem(position));
            }
            return true;
        });
        adapter.bindToRecyclerView(cardActionRecyclerView);

        String functionData = SharedPreferencesUtil.getInstance(this).getString(functionKey);
        if (!TextUtils.isEmpty(functionData)) {
            adapter.setNewData((new Gson().fromJson(functionData, new TypeToken<List<CardParentAction>>() {}.getType())));
        } else {
            adapter.setNewData(new ArrayList<>());
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        animator.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.card_add) {
            MusicUtil.playAssetsAudio("card/zh/p_add.mp3");
            showPopupCardActions(-1, false);
        }
    }

    /**
     * 弹出框，选择行动卡
     */
    private void showPopupCardActions(final int insertPosition, final boolean isUpdate) {
        DefineFunctionCardPopup actionCardPopup = new DefineFunctionCardPopup(this);
        actionCardPopup.setPopupViewClickListener(cardType -> {
            switch (cardType) {
                case CardType.ACTION_BACKWARD:
                case CardType.ACTION_FORWARD:
                case CardType.ACTION_LEFT:
                case CardType.ACTION_RIGHT:
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
                case CardType.ACTION_LOOP:
                case CardType.ACTION_CLOSURE:
                case CardType.ACTION_FUNCTION_CALL:
                    break;
            }
        });
        actionCardPopup.showPopupWindow();
    }

    /**
     * 弹出框 ，选择附属卡
     */
    private void showPopupProp(final int position) {
        final CardParentAction parentAction = adapter.getItem(position);
        if (parentAction != null) {
            PropCardPopup propCardPopup = new PropCardPopup(this);
            propCardPopup.setPopupViewClickListener(cardType -> {
                parentAction.childAction = new CardChildAction(cardType);
                parentAction.childAction.status = true;
                adapter.notifyItemChanged(position);
            });
            propCardPopup.showPopupWindow();
        }
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
     * 行动指令 的 选择 / 替换 / 添加 / 并行
     */
    private void showPopupParent(final int position) {
        CardParentAction item = adapter.getItem(position);
        if (item != null) {
            if (item.parentActionId != -1) {
                DefineFunctionCardUpdatePopup popup = new DefineFunctionCardUpdatePopup(this);
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

    private void saveFunction() {
        String data = new Gson().toJson(adapter.getData());
        SharedPreferencesUtil.getInstance(this).put(functionKey, data);
        finish();
    }

    private void cleanFunction() {
        SharedPreferencesUtil.getInstance(this).remove(functionKey);
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        animator.cancel();
    }

}