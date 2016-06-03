package com.cy.camerapreview.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.cy.camerapreview.adapter.GalleryAdapter;
import com.cy.camerapreview.utils.ImageUtils;
import com.cy.camerapreview.widget.CameraPreviewSurfaceView;
import com.cy.camerapreview.widget.RectSurfaceView;
import com.cy.camerapreview.R;

/**
 * 自定义摄像机
 * @author YuChen
 * @date 2016-3-14 上午11:54:01
 */
public class CameraPreViewActivity extends Activity {

	public final static int CPA_CAMERA_RESULT = 500;

	/** 画面 */
	private CameraPreviewSurfaceView mPreview;
	/** 蒙板 */
	private RectSurfaceView mRectSurfaceView;

	/** 预览画面布局 */
	private FrameLayout mContainer;

	/** 拍照按钮 */
	private Button mButton;

	/** SurfaceHolder */
	private SurfaceHolder sHolder;

	private ImageView mImageView;

	/** 横向ListView */
	private RecyclerView mRecyclerView;

	/** 适配器 */
	private GalleryAdapter mAdapter;

	/** 照片路径集合 */
	private ArrayList<String> mArrayList_urls;

	/** 界面宽度 */
	private int screenWidth;
	/** 最大照片数量 */
	private int mImageMaxCount = 10;
	/** 当前照片数量 */
	private int mImageCurrentCount = 1;

	/** 照相机对象 */
	Camera mCamera;

	private ExecutorService executorService;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message paramMessage) {
			super.handleMessage(paramMessage);
			switch (paramMessage.what) {
				case 0:
					break;
				case 1:
					// 设置预览图
					Bitmap bitmap = (Bitmap) paramMessage.obj;
					mImageView.setImageBitmap(bitmap);
					break;
				case 2:
					// 返回拍摄图片路径
					Intent intent = new Intent();
					intent.putStringArrayListExtra("urls", mArrayList_urls);
					setResult(CPA_CAMERA_RESULT, intent);
					// 启用拍照按钮
					mButton.setEnabled(true);
					// 自动对焦
					mPreview.setAutoFocus(true);
					// 刷新图片，移动到最后一张
					mAdapter.notifyDataSetChanged();
					mRecyclerView.smoothScrollToPosition(mImageCurrentCount);
					break;

				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_continuous_camera);
		// 获取屏幕宽度
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		initView();
		initData();

		// 获取Holder
		sHolder = mPreview.getHolder();

		//设置布局管理器
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRecyclerView.setLayoutManager(linearLayoutManager);

		// 添加适配器
		mRecyclerView.setAdapter(mAdapter);
		initCamera();
		setListener();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mPreview = (CameraPreviewSurfaceView) findViewById(R.id.sv_camera_priview_surfaceView);
		mRectSurfaceView = (RectSurfaceView) findViewById(R.id.rs_rect_surfaceView);
		mContainer = (FrameLayout) findViewById(R.id.fl_camera_priview);
		mRecyclerView = (RecyclerView) findViewById(R.id.rv_camera_images);
		mButton = (Button) findViewById(R.id.btn_camera_biu);
		mImageView = (ImageView) findViewById(R.id.iv_image);
	}

	private void initData(){
		mArrayList_urls = new ArrayList<String>();
		mAdapter = new GalleryAdapter(this, mArrayList_urls);
		if(getIntent() != null){
			mImageMaxCount = getIntent().getIntExtra("MaxCount", 10);
		}
		executorService = Executors.newCachedThreadPool();
	}

	private void setListener() {

	}

	/**
	 * 初始化摄像头
	 */
	private void initCamera() {
		if (mCamera != null) {
			try {
				Parameters parameters = mCamera.getParameters();
				// 设置闪光灯为自动状态
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mCamera.setParameters(parameters);
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
				mCamera.setPreviewDisplay(sHolder);
				// 如果是竖屏
//				if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//					mCamera.setDisplayOrientation(90);
//				} else {
//					mCamera.setDisplayOrientation(0);
//				}
				// 开始预览
				mCamera.startPreview();
				// 自动对焦
				mCamera.autoFocus(null);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		mButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				if (mCamera != null) {
					if (mImageCurrentCount <= mImageMaxCount) {
						// 拍照
						mButton.setEnabled(false);
						mPreview.setAutoFocus(false);
						mCamera.takePicture(null, null, new PictureCallback() {

							@Override
							public void onPictureTaken(final byte[] data,
													   final Camera camera) {
								// 重新预览
//								camera.stopPreview();
								// 根据拍照所得的数据创建位图
								camera.startPreview();
								// 保存图片
								executorService.execute(new Runnable() {
									@Override
									public void run() {
										savaImageGetBitmap(data);
									}
								});
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
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mCamera == null){
			mCamera = Camera.open();
			mPreview.setCamera(mCamera);
		}
		if (mPreview.getParent() == null) {
			mContainer.addView(mPreview);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
			mContainer.removeView(mPreview);
		}
	}

	/**
	 * 保存图片到本地
	 * @param bitmap Bitmap
	 * @return 图片文件路径
     */
	public String saveImage(Bitmap bitmap) {
		String status = Environment.getExternalStorageState();
		SimpleDateFormat timeFormatter = new SimpleDateFormat(
				"yyyyMMdd_HHmmss", Locale.CHINA);
		long time = System.currentTimeMillis();
		String imageName = timeFormatter.format(new Date(time));
		// 创建一个位于SD卡上的文件
		File file = new File(Environment.getExternalStorageDirectory(),
				imageName + ".jpg");
		FileOutputStream outStream = null;
		try {
			// 打开指定文件对应的输出流
			outStream = new FileOutputStream(file);
			// 把位图输出到指定文件中
			bitmap.compress(CompressFormat.JPEG, 100, outStream);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("path",file.getAbsolutePath()+"");
		return file.getAbsolutePath();
	}

	/**
	 * 旋转Bitmap
	 * @param b Bitmap
	 * @param rotateDegree 选择角度
     * @return 返回旋转后的Bitmap
     */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	/**
	 * 获取缩放后的Bitmap
	 * @param byteArr 字节数组
	 * @param reqWidth 宽度
	 * @param reqHeight 高度
     * @return 缩放Bitmap
     */
	public Bitmap getSampleBitmap(byte[] byteArr, int reqWidth, int reqHeight) {
		// 计算sampleSize
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		// 调用方法后，option已经有图片宽高信息
		BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length, options);

		// 计算最相近缩放比例
		options.inSampleSize = ImageUtils.calculateInSampleSize(options,
				reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		// 这个Bitmap out只有宽高
		return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length,
				options);
	}

	/**
	 * 保存图片
	 * @param data 图片字节数组
     */
	private void savaImageGetBitmap(byte[] data){
				Bitmap bitmap_old = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				bitmap_old = getRotateBitmap(bitmap_old, 90);
//				Message msg = new Message();
//				msg.what = 1;
//				msg.obj = bitmap_old;
//				mHandler.sendMessage(msg);
				String path = saveImage(bitmap_old);
//				Bitmap bitmap_new = getSampleBitmap(data, 150, 150);
				mArrayList_urls.add(path);
				Message msg1 = new Message();
				msg1.what = 2;
				mHandler.sendMessage(msg1);
	}

	public void setImageMaxCount(int count){
		mImageMaxCount = count;
	}

	public int getImageMaxCount(){
		return mImageMaxCount;
	}

	public void setImageCurrentCount(int count){
		mImageCurrentCount = count;
	}

	public int getImageCurrentCount(){
		return mImageCurrentCount;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		mRectSurfaceView.invalidate();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
