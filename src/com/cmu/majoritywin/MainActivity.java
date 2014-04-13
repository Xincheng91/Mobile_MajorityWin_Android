package com.cmu.majoritywin;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements OnClickListener{
	
	private static String Tag = "MainActivity";
	private Button button_exit;
	private Button button_join_room;
	private Button button_create_room;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button_exit = (Button) this.findViewById(R.id.Button_Exit);
		button_create_room = (Button) this.findViewById(R.id.Button_Create_Room);
		button_join_room = (Button) this.findViewById(R.id.Button_Join_Room);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Exit:
			finish();
			break;
		case R.id.Button_Create_Room:
			Intent intent = new Intent();
			intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.CreateRoomActivity");
			startActivity(intent);
			break;
		case R.id.Button_Join_Room:
			
			break;
		default:
			Log.i(Tag, "Unexpected Error");
			finish();
			break;
		}
	}
}
