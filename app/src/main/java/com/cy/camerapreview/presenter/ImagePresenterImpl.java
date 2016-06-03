package com.cy.camerapreview.presenter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.cy.camerapreview.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * ImagePresenterImpl
 *
 * @author YuChen
 * @date 2016/6/2
 */
public class ImagePresenterImpl implements ImagePresenter {
    @Override
    public void setImageView(ImageView imageView, String path, int size) {
        if(path.startsWith("http")){
            Picasso.with(imageView.getContext())
                    .load(path)
                    .resize(size, size)
                    .placeholder(R.mipmap.default_img)
                    .error(R.mipmap.default_img).config(Bitmap.Config.RGB_565)
                    .into(imageView);
        }else{
            Picasso.with(imageView.getContext())
                    .load(new File(path))
                    .resize(size, size)
                    .placeholder(R.mipmap.default_img)
                    .error(R.mipmap.default_img).config(Bitmap.Config.RGB_565)
                    .into(imageView);
        }
    }
}
