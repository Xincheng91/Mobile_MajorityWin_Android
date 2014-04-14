package com.cmu.majoritywin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.cmu.http.HttpRequestUtils;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class JoinRoomActivity extends ActionBarActivity implements OnClickListener{

	private static String Tag = "JoinRoomActivity";
	private EditText edittext_roomNumber;
	private Button button_cancel;
	private Button button_enter_room;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);
		edittext_roomNumber = (EditText) this.findViewById(R.id.EditView_RoomNumber);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancle);
		button_enter_room = (Button) this.findViewById(R.id.Button_Enter_Room);
		button_cancel.setOnClickListener(this);
		button_enter_room.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancle:
			finish();
			break;
		case R.id.Button_Enter_Room:
			String roomID = edittext_roomNumber.getText().toString();
			int id = 0;
			try {
				id = Integer.parseInt(roomID);
			} catch (Exception e) {
				Log.e(Tag, e.toString());
				Toast.makeText(this, "The room number is not valid", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				Log.i(Tag, id+"");
				boolean result = HttpRequestUtils.joinRoom(id);
				Log.i(Tag, result+"");
				if(result){
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.EnterRoomActivity");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					startActivity(intent);
				}else{
					Toast.makeText(this, "The roomID doesn't exist", Toast.LENGTH_SHORT).show();
				}
			} catch (ClientProtocolException e) {
				Log.e(Tag, e.toString());
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
				return;
			} catch (IOException e) {
				Log.e(Tag, e.toString());
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
				return;
			}
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			break;
		}
	}

}
