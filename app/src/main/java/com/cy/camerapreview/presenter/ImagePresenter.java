package com.cy.camerapreview.presenter;

import android.content.Context;
import android.widget.ImageView;

/**
 * ImagePresenter
 *
 * @author YuChen
 * @date 2016/6/2
 */
public interface ImagePresenter {
    void setImageView(ImageView imageView, String path, int size);
}
