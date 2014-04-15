package com.cmu.majoritywin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinRoomActivity extends ActionBarActivity implements
		OnClickListener {

	private static String Tag = "JoinRoomActivity";
	private EditText edittext_roomNumber;
	private Button button_cancel;
	private Button button_enter_room;
	private Button button_scan_qrcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);
		edittext_roomNumber = (EditText) this
				.findViewById(R.id.EditView_RoomNumber);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancle);
		button_enter_room = (Button) this.findViewById(R.id.Button_Enter_Room);
		button_scan_qrcode = (Button) this
				.findViewById(R.id.Button_Scan_QRCode);
		button_cancel.setOnClickListener(this);
		button_enter_room.setOnClickListener(this);
		button_scan_qrcode.setOnClickListener(this);
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
				Toast.makeText(this, "The room number is not valid",
						Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				boolean result = HttpRequestUtils.joinRoom(id);
				if (result) {
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin",
							"com.cmu.majoritywin.EnterRoomActivity");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					startActivity(intent);
				} else {
					Toast.makeText(this, "The roomID doesn't exist",
							Toast.LENGTH_SHORT).show();
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
		case R.id.Button_Scan_QRCode:
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				edittext_roomNumber.setText(contents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

}
