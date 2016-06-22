package com.cy.camerapreview.presenter;

import android.hardware.Camera;

import com.cy.camerapreview.widget.CameraPreviewSurfaceView;

import java.util.ArrayList;

/**
 * CameraPreViewPresenter
 *
 * @author YuChen
 * @date 2016/6/3
 */
public interface CameraPreViewPresenter {
    void initCamera(Camera camera, CameraPreviewSurfaceView cameraPreviewSurfaceView);
    void camera(byte[] data);
    void submitImages();
}
