package com.cy.camerapreview.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cy.camerapreview.R;
import com.cy.camerapreview.adapter.GalleryAdapter;
import com.cy.camerapreview.presenter.MainPresenter;
import com.cy.camerapreview.presenter.MainPresenterImpl;
import com.cy.camerapreview.view.MainView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainView {

    public static final int PERMISSION_CAMERA = 0;

    /** 打开拍照界面 */
    @BindView(R.id.btn_main_biu)
    Button mBtnMainBiu;

    /** 画廊 */
    @BindView(R.id.rv_main_gallery)
    RecyclerView mRvMainGallery;

    private MainPresenterImpl mMainPresenter;
    private GalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
        setListener();

    }

    private void initData() {
        mMainPresenter = new MainPresenterImpl(this);
        mMainPresenter.onCreate();
    }

    private void initView() {
        //设置布局管理器
        mRvMainGallery.setLayoutManager(new GridLayoutManager(this, 3));
        // 设置item动画
        mRvMainGallery.setItemAnimator(new DefaultItemAnimator());

        mGalleryAdapter = new GalleryAdapter(this, mMainPresenter.getGalleryData());
        mRvMainGallery.setAdapter(mGalleryAdapter);
    }

    private void setListener() {
        mBtnMainBiu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_biu:
                mMainPresenter.onClickBiu(this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mMainPresenter.setPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMainPresenter.setResult(requestCode, resultCode, data);
    }


    @Override
    public void refreshGallery() {
        mGalleryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.onDestroy();
        if(mMainPresenter != null){
            mMainPresenter = null;
        }
    }
}
