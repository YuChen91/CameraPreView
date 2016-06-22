package com.cy.camerapreview.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.cy.camerapreview.R;
import com.cy.camerapreview.activity.CameraPreViewActivity;
import com.cy.camerapreview.utils.ImageUtils;
import com.cy.camerapreview.view.CameraPreView;
import com.cy.camerapreview.widget.CameraPreviewSurfaceView;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Class
 *
 * @author YuChen
 * @date 2016/6/22
 */
public class CameraPreViewPresenterImpl implements CameraPreViewPresenter {

    private CameraPreView mCameraPreView;
    private ArrayList<String> mArrayList_galleryData;

    private Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText((Context) mCameraPreView, R.string.failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(String s) {
            Log.e("url", s);
            mArrayList_galleryData.add(s);
            mCameraPreView.refreshGallery();
        }
    };

    public CameraPreViewPresenterImpl(CameraPreView cameraPreView) {
        super();
        mCameraPreView = cameraPreView;
        mArrayList_galleryData = new ArrayList<>();
    }

    @Override
    public void initCamera(Camera camera, CameraPreviewSurfaceView cameraPreviewSurfaceView) {
        if (camera != null) {
            // 获取Holder
            SurfaceHolder surfaceHolder = cameraPreviewSurfaceView.getHolder();
            try {
                Camera.Parameters parameters = camera.getParameters();
                // 设置闪光灯为自动状态
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(parameters);
                // 设置预览照片的大小
                // parameters.setPreviewSize(screenWidth, screenHeight);
                // 设置每秒显示4帧
                parameters.setPreviewFrameRate(4);
                // 设置图片格式
                parameters.setPictureFormat(PixelFormat.JPEG);
                // 设置JPG照片的质量
                parameters.set("jpeg-quality", 85);
                // 设置照片大小
                // parameters.setPictureSize(screenWidth, screenHeight);
                // 通过SurfaceView显示取景画面
                camera.setPreviewDisplay(surfaceHolder);
                // 如果是竖屏
//				if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//					mCamera.setDisplayOrientation(90);
//				} else {
//					mCamera.setDisplayOrientation(0);
//				}
                // 开始预览
                camera.startPreview();
                // 自动对焦
                camera.autoFocus(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void camera(byte[] data) {
        Observable.just(data)
                .subscribeOn(Schedulers.io())
                .map(new Func1<byte[], String>() {
                    @Override
                    public String call(byte[] data) {
                        Bitmap bitmap_old = BitmapFactory
                                .decodeByteArray(data, 0, data.length);
                        bitmap_old = ImageUtils.getRotateBitmap(bitmap_old, 90);
                        return ImageUtils.saveImage(bitmap_old);
//        				Bitmap bitmap_new = getSampleBitmap(data, 150, 150);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    @Override
    public void submitImages() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("urls", mArrayList_galleryData);
        if(mCameraPreView instanceof CameraPreViewActivity){
            Activity cameraPreView = (Activity) mCameraPreView;
            cameraPreView.setResult(CameraPreViewActivity.CPA_CAMERA_RESULT, intent);
            cameraPreView.finish();
        }

    }

    public ArrayList<String> getArrayList_galleryData() {
        return mArrayList_galleryData;
    }

    public void setArrayList_galleryData(ArrayList<String> arrayList_galleryData) {
        mArrayList_galleryData = arrayList_galleryData;
    }
}
