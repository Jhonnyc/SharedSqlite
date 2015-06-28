package com.example.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.volumeview.VolumeView;

public class MainActivity extends Activity {

	// For triangle style
	private Button mBtnVolumeUp;
	private Button mBtnVoluumeDown;
	private Button mBtnSetVolume;
	private EditText mVolumeLvlText;
	private VolumeView mVolumeView;
	
	// For flat style
	private Button mBtnVolumeUp1;
	private Button mBtnVoluumeDown1;
	private Button mBtnSetVolume1;
	private EditText mVolumeLvlText1;
	private VolumeView mVolumeView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mVolumeView = (VolumeView) findViewById(R.id.volume);
		mVolumeLvlText = (EditText) findViewById(R.id.volumeLvl);
		mBtnVolumeUp = (Button) findViewById(R.id.volumeUp);
		mBtnVolumeUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVolumeView.volumeUp();
			}
		});
		mBtnVoluumeDown = (Button) findViewById(R.id.volumeDown);
		mBtnVoluumeDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVolumeView.volumeDown();
			}
		});
		mBtnSetVolume = (Button) findViewById(R.id.setVolume);
		mBtnSetVolume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int lvl = 0;
				try {
					lvl = Integer.parseInt(mVolumeLvlText.getText().toString());
				} catch(Exception e) {
					
				}
				mVolumeView.setVolume(lvl);
			}
		});
		
		mVolumeView1 = (VolumeView) findViewById(R.id.volume1);
		mVolumeLvlText1 = (EditText) findViewById(R.id.volumeLvl1);
		mBtnVolumeUp1 = (Button) findViewById(R.id.volumeUp1);
		mBtnVolumeUp1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVolumeView1.volumeUp();
			}
		});
		mBtnVoluumeDown1 = (Button) findViewById(R.id.volumeDown1);
		mBtnVoluumeDown1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVolumeView1.volumeDown();
			}
		});
		mBtnSetVolume1 = (Button) findViewById(R.id.setVolume1);
		mBtnSetVolume1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int lvl = 0;
				try {
					lvl = Integer.parseInt(mVolumeLvlText1.getText().toString());
				} catch(Exception e) {
					
				}
				mVolumeView1.setVolume(lvl);
			}
		});
	}
}
