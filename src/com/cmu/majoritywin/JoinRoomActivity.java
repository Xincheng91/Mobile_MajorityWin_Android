package com.cmu.majoritywin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.cmu.http.HttpRequestUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private String username;
	private Handler handler;
	private Handler toastHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);
		edittext_roomNumber = (EditText) this
				.findViewById(R.id.EditView_RoomNumber);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancel);
		button_enter_room = (Button) this.findViewById(R.id.Button_Enter_Room);
		button_scan_qrcode = (Button) this
				.findViewById(R.id.Button_Scan_QRCode);
		button_cancel.setOnClickListener(this);
		button_enter_room.setOnClickListener(this);
		button_scan_qrcode.setOnClickListener(this);
		username = getIntent().getExtras().getString("com.cmu.passdata.username").trim();
		handler = new Handler(){
			public void handleMessage(Message msg) {
				boolean result = (boolean) msg.obj;
				if (result) {
					String roomID = edittext_roomNumber.getText().toString();
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin",
							"com.cmu.majoritywin.EnterRoomActivity");
					intent.putExtra("com.cmu.passdata.username", username);
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.isCreater", false);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "The roomID doesn't exist",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		toastHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(getApplicationContext(),
							"Problems with network", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		};
	}

	public class networkThread extends Thread{
		public void run(){
			String roomID = edittext_roomNumber.getText().toString();
			try {
				boolean result = HttpRequestUtils.joinRoom(roomID, username);
				Message msg = new Message();
				msg.obj = result;
				handler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				Log.e(Tag, e.toString());
				toastHandler.sendEmptyMessage(0);
				return;
			} catch (IOException e) {
				Log.e(Tag, e.toString());
				toastHandler.sendEmptyMessage(0);
				return;
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancel:
			finish();
			break;
		case R.id.Button_Enter_Room:
			new networkThread().start();
			break;
		case R.id.Button_Scan_QRCode:
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			String contents = scanResult.getContents();
			//String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
			edittext_roomNumber.setText(contents);
		}
	}

}
