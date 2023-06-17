package com.bearya.robot.fairystory.station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bearya.robot.base.play.FacePlay;
import com.bearya.robot.base.play.FaceType;
import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.station.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;


public class StationImageFragment extends BaseFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_IMAGE = 1000;

    private ImageView ivTakePhoto;
    private ImageView ivPreView;
    private int REQUEST_TAKE_PHOTO_CODE = 1001;
    private FacePlay mFacePlay;
    private AppCompatTextView btnImageLib;

    public static StationImageFragment newInstance() {
        return new StationImageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_station_config_image;
    }

    @Override
    public void initView(View rootView) {
        btnImageLib = rootView.findViewById(R.id.btnImageLib);
        ivTakePhoto = rootView.findViewById(R.id.ivTakePhoto);
        ivTakePhoto.setOnClickListener(this);
        ivPreView = rootView.findViewById(R.id.ivPreView);
        btnImageLib.setOnClickListener(this);
        ivPreView.setOnClickListener(this);
        ivPreView.setOnLongClickListener(view -> {
            PlayData station = getStation();
            if(station!=null){
                station.facePlay =null;
            }
            saveStation();
            ivPreView.setVisibility(View.GONE);
            ivPreView.setImageResource(0);
            return true;
        });
    }

    @Override
    protected void loadLastStationConfig() {
        PlayData station = getStation();
        if(station!=null && station.facePlay!=null && !TextUtils.isEmpty(station.facePlay.getFace())) {
            mFacePlay = station.facePlay;
            showLastFace(getActivity(),station.facePlay,ivPreView);
        }
    }

    private void takePhoto(Activity activity, int requestChoosePhotoCode) {
        // 步骤一：创建存储照片的文件
        File file = getDefaultTakePhotoFile(activity);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        //步骤四：调取系统拍照
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file.getAbsolutePath());
        activity.startActivityForResult(intent, requestChoosePhotoCode);
    }

    public File getDefaultTakePhotoFile(Context context) {
        String path = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File file = new File(path, System.currentTimeMillis()+".jpg");
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(MediaStore.EXTRA_OUTPUT);
            FacePlay facePlay = new FacePlay(filePath, FaceType.Image);
            showLastFace(getActivity(),facePlay,ivPreView);
            PlayData station = getStation();
            if(station!=null){
                station.facePlay = facePlay;
            }
        }else if(requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK){
            LibItem libItem = data.getParcelableExtra("data");
            if(libItem!=null){
                FaceType faceType = libItem.getFaceType();
                FacePlay facePlay = new FacePlay(libItem.image,faceType);
                getStation().facePlay = facePlay;
            }

        }
        loadLastStationConfig();
    }

    public static void showLastFace(Context context,FacePlay facePlay,ImageView view){
        if(!TextUtils.isEmpty(facePlay.getFace())){
            view.setVisibility(View.VISIBLE);
            Object url = facePlay.getFace();
            if(facePlay.getFaceType() == FaceType.Lottie) {
                url = ResourceUtil.getMipmapId(facePlay.getFace());
            }else if(facePlay.getFaceType() == FaceType.Image && !facePlay.getFace().contains("storage")){
                url = ResourceUtil.getMipmapId(facePlay.getFace());
            }
            try {
                Glide.with(context).load(url).centerCrop()
//                        .placeholder(R.drawable.ic_launcher_background)
                        .bitmapTransform(new GlideCircleTransform(context.getApplicationContext())).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivTakePhoto) {
            takePhoto(getActivity(), REQUEST_TAKE_PHOTO_CODE);
        } else if (view.getId() == R.id.ivPreView) {
            ImagePreViewDialog dialog = new ImagePreViewDialog(getActivity(), mFacePlay);
            dialog.show();
        } else if (view == btnImageLib) {
            LibActivity.start(getActivity(),REQUEST_CODE_IMAGE,true);
        }
    }
}
