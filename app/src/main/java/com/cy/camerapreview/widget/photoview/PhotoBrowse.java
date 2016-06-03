package com.cy.camerapreview.widget.photoview;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cy.camerapreview.R;

public class PhotoBrowse extends FragmentActivity {

	private ViewPager mViewPager;
	private PhotoAdapter mAdapter;
	private RelativeLayout mRelativeLayout_main;
	private TextView mTextView;
	private ArrayList<String> mUrls;
	private ArrayList<PhotoFragment> mArrayList_Fragments;
	private AlphaAnimation in = new AlphaAnimation(0, 1);
	private AlphaAnimation out = new AlphaAnimation(1, 0);
	private int mCurrentPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo);
		mRelativeLayout_main = (RelativeLayout) findViewById(R.id.photo_main);
		mViewPager = (ViewPager) findViewById(R.id.pager_PhotoBrowse);
		mTextView = (TextView) findViewById(R.id.photo_pageNumber);
		mArrayList_Fragments = new ArrayList<>();
        mUrls = new ArrayList<>();
		mUrls.clear();

        mUrls.addAll(getIntent().getStringArrayListExtra("urls"));
        Log.e("urls", mUrls.toString());
		mCurrentPage = getIntent().getIntExtra("currentPage", 1);
        Log.e("urls", mCurrentPage+"");
		if(mUrls != null){
			for(String s : mUrls){
				mArrayList_Fragments.add(new PhotoFragment(s));
			}
			mAdapter = new PhotoAdapter(getSupportFragmentManager());
			mViewPager.setAdapter(mAdapter);
			in.setDuration(300);
			out.setDuration(300);
			mRelativeLayout_main.startAnimation(in);
			mTextView.setText(1 + " / " + mArrayList_Fragments.size());
			setListener();
			mViewPager.setCurrentItem(mCurrentPage);
		}
	}

	private void setListener() {
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				mTextView.setText((position+1) + " / " + mArrayList_Fragments.size());
//				mArrayList_Fragments.remove(position);
				mAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	public class PhotoAdapter extends FragmentPagerAdapter {

		public PhotoAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mArrayList_Fragments.get(position);
		}

		@Override
		public int getCount() {
			return mArrayList_Fragments.size();
		}


	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.alpha_out);
	}

}
