package com.cy.camerapreview.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.cy.camerapreview.activity.CameraPreViewActivity;
import com.cy.camerapreview.activity.MainActivity;
import com.cy.camerapreview.view.MainView;

import java.util.ArrayList;

/**
 * MainPresenterImpl
 *
 * @author YuChen
 * @date 2016/6/2
 */
public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;
    private ArrayList<String> mArrayList_galleryData;

    public MainPresenterImpl(MainView mainView) {
        super();
        mMainView = mainView;
        mArrayList_galleryData = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onClickBiu(Context context) {
        Activity activity = (Activity) context;
        int checkStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (checkStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CAMERA);
        } else {
            Intent intent = new Intent(activity, CameraPreViewActivity.class);
            activity.startActivityForResult(intent, 1000);
        }
    }

    @Override
    public void setResult(int requestCode, int resultCode, Intent data) {
        mArrayList_galleryData.clear();
        if (data != null) {
            // 设置画廊数据
            mArrayList_galleryData.addAll(data.getStringArrayListExtra("urls"));
        }
        // 刷新画廊
        mMainView.refreshGallery();
    }

    @Override
    public void setPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        Activity activity = (Activity) context;
        switch (requestCode) {
            case MainActivity.PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(activity, CameraPreViewActivity.class);
                    activity.startActivityForResult(intent, 1000);
                } else {
                    Toast.makeText(activity, "打开照相机失败, 请查看系统权限是否开启", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public ArrayList<String> getGalleryData() {
        return mArrayList_galleryData;
    }

    @Override
    public void onDestroy() {
        if(mMainView != null){
            mMainView = null;
        }
    }
}
