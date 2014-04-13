package com.cmu.majoritywin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class CreateRoomActivity extends ActionBarActivity implements OnClickListener{

	private static String Tag = "CreateRoomActivity";
	private TextView textview_roomNumber;
	private Button button_cancel;
	private Button button_enter_room;
	private Handler handler;
	private String roomID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_room);
		textview_roomNumber = (TextView) this.findViewById(R.id.TextView_RoomNumber);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancle_Create);
		button_cancel.setOnClickListener(this);
		button_enter_room = (Button) this.findViewById(R.id.Button_Enter_Room);
		button_enter_room.setOnClickListener(this);
		handler = new Handler(){

			public void handleMessage(Message msg) {
				String roomNumber = (String) msg.obj;
				roomID = roomNumber;
				textview_roomNumber.append(roomNumber);
				super.handleMessage(msg);
			}
			
		};
		new t_getRoomNumber().start();
	}
	
	public class t_getRoomNumber extends Thread{
		public void run() {
			try {
				Log.i(Tag, "New thread running");
				String id = HttpRequestUtils.getUniqueRoomNumber().trim();
				Log.i(Tag, "The room id:" + id);
				Message msg = new Message();
				msg.obj = id;
				handler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				Log.e(Tag, e.toString());
			} catch (IOException e) {
				Log.e(Tag, e.toString());
			} catch (Exception e) {
				Log.e(Tag, e.toString());
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancle_Create:
			finish();
			break;
		case R.id.Button_Enter_Room:
			if(roomID == null || roomID.equals("")){
				break;
			}
			Intent intent = new Intent();
			intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.EnterRoomActivity");
			intent.putExtra("com.cmu.passdata.roomID", roomID);
			startActivity(intent);
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			break;
		}
	}

}
