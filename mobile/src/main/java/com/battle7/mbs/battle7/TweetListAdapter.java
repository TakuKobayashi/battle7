package com.battle7.mbs.battle7;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TweetListAdapter extends BaseAdapter {
	private Activity mActivity;
	private ArrayList<TwitterInfo> mTweetList;
	private HashMap<String, Bitmap> mImageList;
	private RequestQueue mQueue;

	public TweetListAdapter(Activity act){
		mActivity = act;
		mQueue = Volley.newRequestQueue(mActivity);
		mTweetList = new ArrayList<TwitterInfo>();
		mImageList = new HashMap<String, Bitmap>();
	}

	public void addTwitterInfo(TwitterInfo info){
		ArrayList list = new ArrayList<TwitterInfo>();
		list.add(info);
		list.addAll(mTweetList);
		mTweetList.clear();
		mTweetList = list;
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
		NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.img_user);
		imageView.setImageUrl(mTweetList.get(position).profile_image_url, new ImageLoader(mQueue, new ImageLoader.ImageCache() {
			@Override
			public Bitmap getBitmap(String s) {
				return mImageList.get(s);
			}

			@Override
			public void putBitmap(String s, Bitmap bitmap) {
				mImageList.put(s, bitmap);
			}
		}));
		return convertView;
	}

	public void release(){
		for(Map.Entry<String, Bitmap> e : mImageList.entrySet()){
			e.getValue().recycle();
		}
		mImageList.clear();
		mTweetList.clear();
	}
}
