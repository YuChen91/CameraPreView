package com.cy.camerapreview.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.camerapreview.R;
import com.cy.camerapreview.adapter.GalleryAdapter;
import com.cy.camerapreview.presenter.CameraPreViewPresenterImpl;
import com.cy.camerapreview.view.CameraPreView;
import com.cy.camerapreview.widget.CameraPreviewSurfaceView;
import com.cy.camerapreview.widget.RectSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自定义摄像机
 *
 * @author YuChen
 * @date 2016-3-14 上午11:54:01
 */
public class CameraPreViewActivity extends Activity implements CameraPreView {

    public final static int CPA_CAMERA_RESULT = 500;

    /**
     * 画面
     */
    @BindView(R.id.sv_camera_priview_surfaceView)
    CameraPreviewSurfaceView mSvCameraPriviewSurfaceView;

    /**
     * 蒙板
     */
    @BindView(R.id.rs_rect_surfaceView)
    RectSurfaceView mRsRectSurfaceView;

    /**
     * 预览画面布局
     */
    @BindView(R.id.fl_camera_priview)
    FrameLayout mFlCameraPriview;

    /**
     * 图片画廊
     */
    @BindView(R.id.rv_camera_images)
    RecyclerView mRvCameraImages;

    /**
     * 预览图
     */
    @BindView(R.id.iv_image)
    ImageView mIvImage;

    /**
     * 拍照按钮
     */
    @BindView(R.id.btn_camera_biu)
    Button mBtnCameraBiu;

    /**
     * 完成按钮
     */
    @BindView(R.id.btn_camera_submit)
    TextView mBtnCameraSubmit;

    /**
     * 适配器
     */
    private GalleryAdapter mAdapter;

    /**
     * 最大照片数量
     */
    private int mImageMaxCount = 10;
    /**
     * 当前照片数量
     */
    private int mImageCurrentCount = 1;

    /**
     * 照相机对象
     */
    Camera mCamera;

    private CameraPreViewPresenterImpl mCameraPreViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_camera);
        ButterKnife.bind(this);
        mCameraPreViewPresenter = new CameraPreViewPresenterImpl(this);
        initView();
        initData();
    }

    private void initView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvCameraImages.setLayoutManager(linearLayoutManager);

        // 初始化相机
        mCameraPreViewPresenter.initCamera(mCamera, mSvCameraPriviewSurfaceView);
    }

    private void initData() {
        mAdapter = new GalleryAdapter(this, mCameraPreViewPresenter.getArrayList_galleryData());
        if (getIntent() != null) {
            mImageMaxCount = getIntent().getIntExtra("MaxCount", 10);
        }

        // 添加适配器
        mRvCameraImages.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCamera == null) {
            mCamera = Camera.open();
            mSvCameraPriviewSurfaceView.setCamera(mCamera);
        }
        if (mSvCameraPriviewSurfaceView.getParent() == null) {
            mFlCameraPriview.addView(mSvCameraPriviewSurfaceView);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshMask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mSvCameraPriviewSurfaceView.setCamera(null);
            mCamera.release();
            mCamera = null;
            mFlCameraPriview.removeView(mSvCameraPriviewSurfaceView);
        }
    }

    public void setImageMaxCount(int count) {
        mImageMaxCount = count;
    }

    public int getImageMaxCount() {
        return mImageMaxCount;
    }

    public void setImageCurrentCount(int count) {
        mImageCurrentCount = count;
    }

    public int getImageCurrentCount() {
        return mImageCurrentCount;
    }


    @Override
    public void refreshMask() {
        if (mRsRectSurfaceView != null) {
            mRsRectSurfaceView.invalidate();
        }
    }

    @Override
    public void refreshGallery() {
        if (mAdapter != null) {
            // 启用拍照按钮
            mBtnCameraBiu.setEnabled(true);
            // 自动对焦
            mSvCameraPriviewSurfaceView.setAutoFocus(true);
            // 刷新图片，移动到最后一张
            mAdapter.notifyDataSetChanged();
            mRvCameraImages.smoothScrollToPosition(mImageCurrentCount);
        }
    }

    @OnClick(R.id.btn_camera_biu)
    void onClick_biu(){
        if (mCamera != null) {
            if (mImageCurrentCount <= mImageMaxCount) {
                // 拍照
                mBtnCameraBiu.setEnabled(false);
                mSvCameraPriviewSurfaceView.setAutoFocus(false);
                mCamera.takePicture(null, null, new PictureCallback() {

                    @Override
                    public void onPictureTaken(final byte[] data,
                                               final Camera camera) {
                        // 重新预览
//								camera.stopPreview();
                        // 根据拍照所得的数据创建位图
                        camera.startPreview();
                        // 保存图片
                        mCameraPreViewPresenter.camera(data);
                    }
                });
                // 当前图片数量+1
                mImageCurrentCount++;

            } else {
                Toast.makeText(getApplicationContext(), "最多上传" + mImageMaxCount + "张图片！",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.btn_camera_submit)
    void onClick_submit(){
        mCameraPreViewPresenter.submitImages();
    }

}
