package com.bearya.robot.fairystory.station.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void takePicture(Camera.ShutterCallback mShutterCallback, Camera.PictureCallback rawPictureCallback, Camera.PictureCallback jpegPictureCallback) {
        mCamera.takePicture(mShutterCallback, rawPictureCallback, jpegPictureCallback);
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        mCamera.setDisplayOrientation(180);
        try {
            mCamera.setPreviewDisplay(holder);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();// 获取mCamera的参数对象
        Camera.Size largestSize = getBestSupportedSize(parameters
                .getSupportedPreviewSizes());
        parameters.setPreviewSize(largestSize.width, largestSize.height);// 设置预览图片尺寸
        largestSize = getBestSupportedSize(parameters
                .getSupportedPictureSizes());// 设置捕捉图片尺寸
        parameters.setPictureSize(largestSize.width, largestSize.height);
        parameters.set("rotation", 180);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes) {
        // 取能适用的最大的SIZE
        Camera.Size largestSize = sizes.get(0);
        int largestArea = sizes.get(0).height * sizes.get(0).width;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                largestArea = area;
                largestSize = s;
            }
        }
        return largestSize;
    }

}
