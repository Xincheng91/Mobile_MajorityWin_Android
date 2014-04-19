package com.cmu.majoritywin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.cmu.http.HttpRequestUtils;
import com.cmu.view.Contents;
import com.cmu.view.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class CreateRoomActivity extends ActionBarActivity implements OnClickListener{

	private static String Tag = "CreateRoomActivity";
	private TextView textview_roomNumber;
	private Button button_cancel;
	private Button button_enter_room;
	private Handler handler;
	private String roomID;
	private String username;
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_room);
		textview_roomNumber = (TextView) this.findViewById(R.id.TextView_RoomNumber);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancle_Create);
		button_cancel.setOnClickListener(this);
		button_enter_room = (Button) this.findViewById(R.id.Button_Enter_Room);
		button_enter_room.setOnClickListener(this);
		username = getIntent().getExtras().getString("com.cmu.passdata.username").trim();
		imageView = (ImageView) findViewById(R.id.qrCode);
		
		handler = new Handler(){

			public void handleMessage(Message msg) {
				String roomNumber = (String) msg.obj;
				roomID = roomNumber;
				textview_roomNumber.append(roomNumber);
				
				
				String qrData = roomID;
				int qrCodeDimention = 500;
				QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null,
				        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

				try {
				    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
				    imageView.setImageBitmap(bitmap);
				} catch (WriterException e) {
				    e.printStackTrace();
				    Toast.makeText(getApplicationContext(), "Problems with QR Code",
							Toast.LENGTH_SHORT).show();
				}
				super.handleMessage(msg);
			}
			
		};
		new t_getRoomNumber().start();
	}
	
	public class t_getRoomNumber extends Thread{
		public void run() {
			try {
				String id = HttpRequestUtils.createRoom(username).trim();
				Message msg = new Message();
				msg.obj = id;
				handler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				Log.e(Tag, e.toString());
				Toast.makeText(getApplicationContext(), "Problems with network",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Log.e(Tag, e.toString());
				Toast.makeText(getApplicationContext(), "Problems with network",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.e(Tag, e.toString());
				Toast.makeText(getApplicationContext(), "Problems with network",
						Toast.LENGTH_SHORT).show();
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
			intent.putExtra("com.cmu.passdata.username", username);
			intent.putExtra("com.cmu.passdata.isCreater", true);
			startActivity(intent);
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			Toast.makeText(getApplicationContext(), "Unexpected Error",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
