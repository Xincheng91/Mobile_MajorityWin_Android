package com.cmu.majoritywin;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;

public class LauncherActivity extends ActionBarActivity {

	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_launcher);
		new launcherThread().start();
		handler =  new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Intent intent = new Intent();
				intent.setClassName("com.cmu.majoritywin",
						"com.cmu.majoritywin.LoginActivity");
				startActivity(intent);
				
				overridePendingTransition(R.anim.in,R.anim.out);
				//finish();
			}
			
		};
	}
	
	public class launcherThread extends Thread{
		public void run(){
			try {
				sleep(2000);
				handler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
