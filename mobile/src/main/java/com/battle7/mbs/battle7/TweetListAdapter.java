package com.battle7.mbs.battle7;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TweetListAdapter extends BaseAdapter {
	private Activity mActivity;
	private ArrayList<TwitterInfo> mTweetList;
	private ArrayList<ImageView> mUserImageList;
	private ArrayList<Bitmap> mImageList;
	private Handler mHandler;
	private int mPosition = 0;

	public TweetListAdapter(Activity act){
		mActivity = act;
		mTweetList = new ArrayList<TwitterInfo>();
		mUserImageList = new ArrayList<ImageView>();
		mImageList = new ArrayList<Bitmap>();
		mHandler = new Handler();
		AsynkImageLoadThread.getInstance(AsynkImageLoadThread.class).setOnAudioRecordCallback(new AsynkImageLoadThread.LoadCallback() {
			@Override
			public void onLoad(int id, Bitmap bitmap) {
				mUserImageList.get(id).setImageBitmap(bitmap);
			}
		});
	}

	public void loadImage(int position){
		mPosition = position;
		AsynkImageLoadThread.getInstance(AsynkImageLoadThread.class).setImageQueue(position, mTweetList.get(mPosition).profile_image_url);
	}

	public void addTwitterInfo(TwitterInfo info){
		mTweetList.add(info);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTweetList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTweetList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mActivity.getLayoutInflater().inflate(R.layout.tweet_list_cell_view, null);
		TextView userName = (TextView) convertView.findViewById(R.id.txt_user_name);
		userName.setText(mTweetList.get(position).user_name);
		TextView tweet = (TextView) convertView.findViewById(R.id.txt_tweet);
		tweet.setText(mTweetList.get(position).tweet);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.img_user);
		mUserImageList.add(imageView);
		loadImage(position);
		return convertView;
	}

	public void release(){
		for(Bitmap bmp : mImageList){
			bmp.recycle();
			bmp = null;
		}
		for(ImageView im : mUserImageList){
			ApplicationHelper.releaseImageView(im);
		}
		mTweetList.clear();
	}
}
