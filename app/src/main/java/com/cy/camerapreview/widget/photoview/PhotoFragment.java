package com.cy.camerapreview.widget.photoview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cy.camerapreview.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoFragment extends Fragment {
	
	private String mUrl;
	View mParent;
	View mBg;
	PhotoView mPhotoView;
	Info mInfo;
	public PhotoFragment() {
	}
	@SuppressLint("ValidFragment")
	public PhotoFragment(String url) {
		mUrl = url;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_photo_browse, null);
		mParent = view.findViewById(R.id.parent);
		mPhotoView = (PhotoView) view.findViewById(R.id.img);
		Picasso.with(getActivity())
				.load(new File(mUrl))
				.placeholder(R.mipmap.default_img)
				.error(R.mipmap.default_img)
				.into(mPhotoView);
		mInfo = mPhotoView.getInfo();
		mPhotoView.enable();
		mParent.setVisibility(View.VISIBLE);
		// mPhotoView.animaFrom(mInfo);

		mPhotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				getActivity().overridePendingTransition(0, R.anim.alpha_out);
			}
		});
		return view;
	}
}
