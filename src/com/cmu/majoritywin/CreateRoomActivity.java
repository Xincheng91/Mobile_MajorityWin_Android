package com.cmu.majoritywin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_room);
		textview_roomNumber = (TextView) this.findViewById(R.id.TextView_RoomNumber);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancle_Create);
		handler = new Handler(){

			public void handleMessage(Message msg) {
				int roomNumber = (int) msg.obj;
				textview_roomNumber.setText(roomNumber);
				super.handleMessage(msg);
			}
			
		};
		Thread t_getRoomNumber = new Thread(new Runnable() {
			public void run() {
				try {
					int id = HttpRequestUtils.getUniqueRoomNumber();
					Message msg = new Message();
					msg.obj = id;
					handler.sendMessage(msg);
				} catch (ClientProtocolException e) {
					Log.i(Tag, e.toString());
				} catch (IOException e) {
					Log.i(Tag, e.toString());
				} catch (Exception e) {
					Log.i(Tag, e.toString());
				}
			}
		});
		t_getRoomNumber.run();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancle_Create:
			finish();
			break;
		case R.id.Button_Enter_Room:
			
			break;
		default:
			Log.i(Tag, "Unexpected Error");
			break;
		}
	}

}
