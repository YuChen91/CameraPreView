package com.cy.camerapreview.adapter;

import java.util.ArrayList;

import com.cy.camerapreview.activity.CameraPreViewActivity;
import com.cy.camerapreview.presenter.ImagePresenterImpl;
import com.cy.camerapreview.utils.Util;
import com.cy.camerapreview.widget.photoview.PhotoBrowse;
import com.cy.camerapreview.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class GalleryAdapter extends
		RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<String> mArrayList_urls;
	private int width;
	private ImagePresenterImpl mImagePresenter;
	LayoutParams params;

	public GalleryAdapter(Context context, ArrayList<String> al) {
		mContext = context;
		mActivity = (Activity) mContext;
		mInflater = LayoutInflater.from(context);
		mArrayList_urls = al;
		int screenWidth = mActivity.getWindowManager().getDefaultDisplay()
				.getWidth();
		width = (screenWidth - Util.dp2px(mContext, 10 * 2)) / 4;
		params = new LayoutParams(width, width);
		mImagePresenter = new ImagePresenterImpl();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View arg0) {
			super(arg0);
		}

		ImageView mImg;
		ImageView mImg_delete;

	}

	@Override
	public int getItemCount() {
		return mArrayList_urls.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = mInflater.inflate(R.layout.image_view_delete, viewGroup,
				false);
		ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.mImg = (ImageView) view
				.findViewById(R.id.iv_imageViewDelete_image);
		viewHolder.mImg_delete = (ImageView) view
				.findViewById(R.id.iv_imageViewDelete_delete);
		viewHolder.mImg.setLayoutParams(params);

		return viewHolder;
	}

	/**
	 * 设置值
	 */
	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
		// 打开图片
		viewHolder.mImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PhotoBrowse.class);
				intent.putStringArrayListExtra("urls", mArrayList_urls);
				intent.putExtra("currentPage", i);
				mActivity.startActivity(intent);
				mActivity.overridePendingTransition(0, 0);
			}
		});

		// 删除图片
		viewHolder.mImg_delete
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mArrayList_urls.remove(i);
						if(mActivity instanceof CameraPreViewActivity){
							CameraPreViewActivity spva = (CameraPreViewActivity) mActivity;
							int currentCount = spva.getImageCurrentCount();
							spva.setImageCurrentCount(currentCount-1);
						}
						notifyDataSetChanged();
					}
				});

		// 设置图片
		mImagePresenter.setImageView(viewHolder.mImg, mArrayList_urls.get(i), width-20);

	}

}
