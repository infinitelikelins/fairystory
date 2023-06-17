package com.bearya.robot.fairystory.station;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;

import java.util.List;
import java.util.Locale;

public class LibFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lib, null);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        long libUuid = getArguments().getLong("lib");
        Lib lib = StationsActivity.stationLib.getByUUID(libUuid);
        int spanCount = 3;
        if(lib.getCount()<=4){
            spanCount = 2;
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(createAdapter(lib.items));
        return rootView;
    }

    public static LibFragment newInstance(long libUuid) {

        Bundle args = new Bundle();
        args.putLong("lib",libUuid);
        LibFragment fragment = new LibFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        int index = (int) view.getTag();
        MusicUtil.playAssetsAudio(String.format(Locale.CHINA,"station/zh/station_action_%d.mp3",index));
        Intent intent = new Intent();
//        intent.putExtra("data", sounds[index-1]);
        getActivity().setResult(Activity.RESULT_OK,intent);
        getActivity().finish();
    }

    protected RecyclerView.Adapter createAdapter(List<LibItem> datas){
        RecyclerViewAdapter<LibItemHolder, LibItem> adapter = new  RecyclerViewAdapter<LibItemHolder, LibItem>(datas){};
        adapter.setOnItemClickedListener(new OnItemClickedListener<LibItem>() {
            @Override
            public void onItemClicked(LibItem data, int flag) {
                Intent intent = new Intent();
                intent.putExtra("data",data);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
        return adapter;
    }



}
