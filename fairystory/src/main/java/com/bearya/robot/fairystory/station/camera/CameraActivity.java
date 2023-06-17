package com.bearya.robot.fairystory.station.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.fairystory.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends BaseActivity {
    private CameraSurfaceView mCameraSurfaceView;
    private String mUri;
    View preViewLayout;
    ImageView btnOk;
    ImageView btnCancel;
    ImageView ivPreView;
    ImageView ivTakePhoto;
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    private Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    private Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mCameraSurfaceView.startPreview();
            saveFile(data);
            ivTakePhoto.setVisibility(View.INVISIBLE);
            preViewLayout.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(mUri);
                ivPreView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(CameraActivity.this, getString(R.string.take_success), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ivTakePhoto = (ImageView) findViewById(R.id.img_take_photo);
        ivPreView = findViewById(R.id.iv_pre_view);
        preViewLayout = findViewById(R.id.pre_view_layout);
        btnOk = (ImageView) findViewById(R.id.btnOk);
        btnCancel = (ImageView) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preViewLayout.setVisibility(View.GONE);
                ivTakePhoto.setVisibility(View.VISIBLE);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(MediaStore.EXTRA_OUTPUT,mUri);
                setResult(RESULT_OK,data);
                finish();
            }
        });
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.sv_camera);
        ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        mUri = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void takePhoto() {
        mCameraSurfaceView.takePicture(mShutterCallback, rawPictureCallback, jpegPictureCallback);
    }

    public void saveFile(byte[] data) {
        FileOutputStream outputStream = null;
        try {
            File file = new File(mUri);
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            outputStream = new FileOutputStream(mUri);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
