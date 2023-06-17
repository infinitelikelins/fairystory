package com.bearya.robot.fairystory.station;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.base.play.TimeAction;
import com.bearya.robot.fairystory.R;

import java.util.Map;


public class StationActionFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {
    private TextView box1;
    private TextView box2;
    private TextView box3;

    private final TextView[] secondView = new TextView[3];

    private TextView currentEditSecondView;

    private static final Map<Integer,Integer> ACTION_IMAGE_MAP = new ArrayMap<>();

    static {
        ACTION_IMAGE_MAP.put(ActionSetDialog.ACTION_DOUBLE_HAND, R.mipmap.ic_shake_hand_select);
        ACTION_IMAGE_MAP.put(ActionSetDialog.ACTION_LEFT_HAND,R.mipmap.ic_shake_left_hand_select);
        ACTION_IMAGE_MAP.put(ActionSetDialog.ACTION_RIGHT_HAND,R.mipmap.ic_shake_right_hand_select);
        ACTION_IMAGE_MAP.put(ActionSetDialog.ACTION_SHAKE_HEADER,R.mipmap.ic_shake_head_select);
        ACTION_IMAGE_MAP.put(ActionSetDialog.ACTION_SHAKE_HEADER_TO_LEFT,R.mipmap.ic_shake_head_to_left_select);
        ACTION_IMAGE_MAP.put(ActionSetDialog.ACTION_SHAKE_HEADER_TO_RIGHT,R.mipmap.ic_shake_head_to_right_select);
    }

    public static StationActionFragment newInstance() {
        StationActionFragment f = new StationActionFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_station_config_action;
    }

    @Override
    protected void initView(View view) {
        box1 = view.findViewById(R.id.box1);
        box2 = view.findViewById(R.id.box2);
        box3 = view.findViewById(R.id.box3);
        box1.setTag(1);
        box2.setTag(2);
        box3.setTag(3);
        box1.setOnClickListener(this);
        box2.setOnClickListener(this);
        box3.setOnClickListener(this);
        box1.setOnLongClickListener(this);
        box2.setOnLongClickListener(this);
        box3.setOnLongClickListener(this);
        View.OnClickListener secondClickListener = view1 -> {
            final PlayData station = getStation();
            Object tag = view1.getTag();
            int intTag = 0;
            if(tag instanceof Double){
                intTag = ((Double)tag).intValue();
            }else if(tag instanceof Integer){
                intTag = (Integer) tag;
            }
            if (station != null) {
                ArrayMap<Integer, TimeAction> actions = station.actions;
                if (actions != null) {
                    final TimeAction action = actions.get(intTag);
                    if (action != null) {
                        currentEditSecondView = (TextView) view1;
                        ActionTimeDialog dialog = new ActionTimeDialog(requireActivity(), action.getTime());
                        dialog.setListener(second -> {
                            action.setTime(second);
                            currentEditSecondView.setText(String.format(getString(R.string.some_second), second));
                        });
                        dialog.show();
                    }
                }
            }
        };
        secondView[0] = view.findViewById(R.id.tvSecond1);
        secondView[1] = view.findViewById(R.id.tvSecond2);
        secondView[2] = view.findViewById(R.id.tvSecond3);
        secondView[0].setTag(1);
        secondView[1].setTag(2);
        secondView[2].setTag(3);
        secondView[0].setOnClickListener(secondClickListener);
        secondView[1].setOnClickListener(secondClickListener);
        secondView[2].setOnClickListener(secondClickListener);

    }

    @Override
    protected void loadLastStationConfig() {
        PlayData station = getStation();
        if (station != null && station.actions != null && station.actions.size() > 0) {
            ArrayMap<Integer, TimeAction> actionMap = station.actions;

            if (actionMap.indexOfKey(1) >= 0) {
                TimeAction action = actionMap.get(1);
                if (action != null) {
                    setActionToView(action, box1, secondView[0]);
                }
            }
            if (actionMap.indexOfKey(2) >= 0) {
                TimeAction action = actionMap.get(2);
                if (action != null) {
                    setActionToView(action, box2, secondView[1]);
                }
            }
            if (actionMap.indexOfKey(3) >= 0) {
                TimeAction action = actionMap.get(3);
                if (action != null) {
                    setActionToView(action, box3, secondView[2]);
                }
            }
        }
    }

    @Override
    public void onClick(final View view) {
        ActionSetDialog dialog = new ActionSetDialog(requireActivity());
        dialog.setListener(action -> {
            int tag = (int) view.getTag();
            TextView sv = secondView[tag-1];
            int time = getSecondFromSecondView(sv);
            TimeAction ta = new TimeAction(action,time);
            setActionToView(ta,view,sv);
            PlayData station = getStation();
            if (station != null) {
                if (station.actions == null){
                    station.actions  = new ArrayMap<>();
                }
                station.actions.put(tag, ta);
            }
        });
        dialog.show();
    }

    private int getSecondFromSecondView(TextView sv){
        try {
            String text = sv.getText().toString();
            return Integer.parseInt(text.replaceAll("\\D", ""));
        } catch (Exception e) {
            return 3;
        }
    }

    private void setActionToView(TimeAction action, View view, TextView textView) {
        view.setBackgroundResource(ACTION_IMAGE_MAP.get(action.getAction()));
        textView.setVisibility(View.VISIBLE);
        textView.setText(String.format(getString(R.string.some_second), action.getTime()));
    }

    @Override
    public boolean onLongClick(View view) {
        int tag = (Integer)view.getTag();
        view.setBackgroundResource(R.mipmap.ic_add_action);
        PlayData playData = getStation();
        playData.removeAction(tag);
        TextView textView = secondView[tag-1];
        textView.setText(getString(R.string.three_second));
        textView.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1009 && resultCode == Activity.RESULT_OK){
            String content = data.getStringExtra("content");
            if(!TextUtils.isEmpty(content)){
                try{
                    currentEditSecondView.setText(String.format(getString(R.string.some_second) , Integer.parseInt(content)));
                }catch (Exception e){
                    currentEditSecondView.setText(getString(R.string.three_second));
                }
            }
        }
    }
}
