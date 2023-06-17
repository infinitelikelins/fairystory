package com.bearya.robot.fairystory.station;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.fairystory.R;
import com.google.android.material.tabs.TabLayout;

import io.reactivex.annotations.NonNull;

public class LibActivity extends BaseActivity implements View.OnClickListener {

    public static void start(Activity context, int requestCode, boolean image) {
        Intent starter = new Intent(context, LibActivity.class);
        starter.putExtra("image", image);
        context.startActivityForResult(starter, requestCode);
    }

    private Fragment[] fragments;
    private Libs imageLibs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_lib);

        boolean booleanExtra = getIntent().getBooleanExtra("image", true);
        imageLibs = booleanExtra ? StationsActivity.stationLib.imageLibs : StationsActivity.stationLib.soundLibs;
        findViewById(R.id.bgView).setBackgroundResource(booleanExtra ? R.mipmap.bg_lib_image : R.mipmap.bg_lib_sound);
        findViewById(R.id.ivBack).setOnClickListener(this);

        fragments = new Fragment[imageLibs.getCount()];
        for (int i = 0; i < imageLibs.getCount(); i++) {
            fragments[i] = LibFragment.newInstance(imageLibs.get(i).uuid);
        }
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new LibActivity.MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivBack) {
            finish();
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return imageLibs.get(position).name;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}