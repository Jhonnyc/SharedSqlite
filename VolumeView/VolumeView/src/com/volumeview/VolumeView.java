package com.volumeview;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("NewApi") 
public class VolumeView extends LinearLayout {
	
	public enum LayoutStyle {Triangle, Flat};   

	// Class Objects
	private List<View> mViews;
	private final int DEFAULT_VISIBLE_BARS = 0;
	private final int DEFAULT_BARS = 10;
	private float DEFAULT_BAR_SIZE = 10f;
	private float DEFAULT_SEPERATOR_SIZE = 1f;
	private int DEFAULT_LAYOUT_STYLE = 0;
	private int DEFAULT_EMPTY_COLOR;
	private int DEFAULT_FILLED_COLOR;
	private static String VOLUME = "Progress";
	private static String PARENT_CLASS = "ParentClass";
	
	private int mVolume;
	private int mTotalViews;
	private int mNumberOfBars;
	private int mFilledBars;
	private int mEmptyColor;
	private int mFilledColor;
	private float mBarSize;
	private float mSeperatorSize;
	private int intLayoutStyle;
	private LayoutStyle mLayoutStyle;
	
	// Class Views
	private Context mContext;
	private View mRoot;
	private LinearLayout mContainer;

	public VolumeView(Context context) {
		super(context);
		initializeClass(context);
	}

	public VolumeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(context, attrs);
		initializeClass(context);
	}

	public VolumeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setAttributes(context, attrs);
		initializeClass(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initViews();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(PARENT_CLASS, super.onSaveInstanceState());
		bundle.putInt(VOLUME, mVolume);
		return bundle;
	}
	 
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
	    if (state instanceof Bundle) {
	        Bundle bundle = (Bundle)state;
	        super.onRestoreInstanceState(bundle.getParcelable(PARENT_CLASS));
	        setVolume(bundle.getInt(VOLUME));
	    } 
	    else {
	       super.onRestoreInstanceState(state);
	    }
	}
	 
	@Override
	protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
	    super.dispatchFreezeSelfOnly(container);
	}
	 
	@Override
	protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
	    super.dispatchThawSelfOnly(container);
	}
	
	/**********************************
	 ********************************** 
	 ********* Public Methods *********
	 **********************************
	 **********************************/
	
	public void setVolume(int volume) {
		if(volume < mViews.size() ) {
			mVolume = volume;
			for(int i = 0; i < mViews.size(); i++){
				View view = mViews.get(i);
				if(i <= volume) {
					view.setBackgroundColor(mFilledColor);
				} else {
					view.setBackgroundColor(mEmptyColor);
				}
				view.invalidate();
			}
		}
	}
	
	public void volumeUp() {
		if(mVolume < mViews.size()) {
			View view = mViews.get(mVolume);
			view.setBackgroundColor(mFilledColor);
			view.invalidate();
			mVolume++;
			if(mVolume > mViews.size()) {
				mVolume = mViews.size() - 1;
			}
		}
	}
	
	public void volumeDown() {
		mVolume--;
		if(mVolume >= 0) {
			View view = mViews.get(mVolume);
			view.setBackgroundColor(mEmptyColor);
			view.invalidate();
		} else {
			mVolume = 0;
		}
	}
	
	/***********************************
	 *********************************** 
	 ********* Private Methods *********
	 ***********************************
	 ***********************************/
	
	private void setAttributes(Context context, AttributeSet attrs) {
		TypedArray typedArray;
		DEFAULT_EMPTY_COLOR = context.getResources().getColor(R.color.progress_empty_color); 
		DEFAULT_FILLED_COLOR = context.getResources().getColor(R.color.progress_filled_color);
		typedArray = context.obtainStyledAttributes(attrs, R.styleable.VolumeView);
		mNumberOfBars = typedArray.getInt(R.styleable.VolumeView_number_of_bars, DEFAULT_BARS);
		mFilledBars = typedArray.getInt(R.styleable.VolumeView_filled_bars, DEFAULT_VISIBLE_BARS);
		mEmptyColor = typedArray.getColor(R.styleable.VolumeView_empty_color, DEFAULT_EMPTY_COLOR);
		mFilledColor = typedArray.getColor(R.styleable.VolumeView_filled_color, DEFAULT_FILLED_COLOR);
		mBarSize = typedArray.getFloat(R.styleable.VolumeView_bar_size, DEFAULT_BAR_SIZE);
		mSeperatorSize = typedArray.getFloat(R.styleable.VolumeView_seperator_size, DEFAULT_SEPERATOR_SIZE);
		intLayoutStyle = typedArray.getInt(R.styleable.VolumeView_layoutStyle, DEFAULT_LAYOUT_STYLE);
		mLayoutStyle = LayoutStyle.values()[intLayoutStyle];
		typedArray.recycle();
	}
	
	private void initializeClass(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflater.inflate(R.layout.volume_view, this);
		mContainer = (LinearLayout)mRoot.findViewById(R.id.container);
		mViews = new ArrayList<View>();
	}
	
	private void initViews() {
		// The number of views between every two bars is one 
		// making the total views 2n - 1
		float j = 1;
		mTotalViews = (mNumberOfBars * 2) - 1;
		mVolume = mFilledBars;
		for(int i = 0; i < mTotalViews; i++) {
			LinearLayout layout;
			boolean isBar = (i % 2 == 0);
			if(isBar) {
				int color;
				LinearLayout layoutTop, layoutBottom;
				layout = new LinearLayout(mContext);
				LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, mBarSize);
				layout.setLayoutParams(lp);
				color = (j <= mFilledBars) ? mFilledColor : mEmptyColor;
				if(mLayoutStyle.equals(LayoutStyle.Triangle)) {
					layout.setOrientation(LinearLayout.VERTICAL);
					
					layoutTop = new LinearLayout(mContext);
					LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, mNumberOfBars - j);
					layoutTop.setLayoutParams(lp1);
					
					layoutBottom = new LinearLayout(mContext);
					LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, j);
					layoutBottom.setBackgroundColor(color);
					layoutBottom.setLayoutParams(lp2);
					mViews.add(layoutBottom);
					
					layout.addView(layoutTop);
					layout.addView(layoutBottom);
				} else if(mLayoutStyle.equals(LayoutStyle.Flat)) {
					layout.setBackgroundColor(color);
					mViews.add(layout);
				}
				layout.invalidate();
				j++;
			} else {
				layout = new LinearLayout(mContext);
				LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, mSeperatorSize);
				layout.setLayoutParams(lp);
			}
			mContainer.addView(layout);
			mContainer.invalidate();
		}
	}
}
