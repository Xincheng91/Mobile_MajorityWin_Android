package com.cmu.majoritywin;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener{
	
	private static String Tag = "MainActivity";
	private Button button_exit;
	private Button button_join_room;
	private Button button_create_room;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button_exit = (Button) this.findViewById(R.id.Button_Cancel);
		button_create_room = (Button) this.findViewById(R.id.Button_Create_Room);
		button_join_room = (Button) this.findViewById(R.id.Button_Join_Room);
		button_exit.setOnClickListener(this);
		button_create_room.setOnClickListener(this);
		button_join_room.setOnClickListener(this);
		username = getIntent().getExtras().getString("com.cmu.passdata.username").trim();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancel:
			finish();
			break;
		case R.id.Button_Create_Room:
			Intent intent = new Intent();
			intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.CreateRoomActivity");
			intent.putExtra("com.cmu.passdata.username", username);
			startActivity(intent);
			break;
		case R.id.Button_Join_Room:
			Intent intent1 = new Intent();
			intent1.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.JoinRoomActivity");
			intent1.putExtra("com.cmu.passdata.username", username);
			startActivity(intent1);
			break;
		default:
			Log.e(Tag, "Unexpected Click Error");
			finish();
			break;
		}
	}
}
