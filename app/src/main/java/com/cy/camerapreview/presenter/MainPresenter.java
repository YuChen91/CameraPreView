package com.cy.camerapreview.presenter;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * MainPresenter
 *
 * @author YuChen
 * @date 2016/6/2
 */
public interface MainPresenter {
    void onCreate();
    void onClickBiu(Context context);
    void setResult(int requestCode, int resultCode, Intent data);
    void setPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults);
    ArrayList<String> getGalleryData();
    void onDestroy();
}
