package com.cmu.majoritywin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class EnterRoomActivity extends ActionBarActivity implements OnClickListener{

	private static String Tag = "EnterRoomActivity";
	private Button button_cancle;
	private Button button_start_voting;
	private TextView textview_participants_number;
	private EditText edittext_participants;
	private Handler handler;
	private Handler handlerForCheckingLeader;
	private int numberOfParticipants = 0;
	private String roomID;
	private String username;
	private Handler toastHandler;
	private boolean flag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_room);
		button_cancle = (Button) this.findViewById(R.id.Button_Cancel);
		button_start_voting = (Button) this.findViewById(R.id.Button_Begin_Vote);
		textview_participants_number = (TextView) this.findViewById(R.id.TextView_ParticipantNumber);
		edittext_participants = (EditText) this.findViewById(R.id.EditText_Participants);
		button_cancle.setOnClickListener(this);
		button_start_voting.setOnClickListener(this);
		Intent intent = getIntent();
		roomID = intent.getExtras().getString("com.cmu.passdata.roomID");
		username = intent.getExtras().getString("com.cmu.passdata.username");
		boolean isCreater = intent.getExtras().getBoolean("com.cmu.passdata.isCreater");
		if(!isCreater){
			button_start_voting.setVisibility(View.GONE);
		}
		handler = new Handler(){
			public void handleMessage(Message msg) {
				String[] participants = (String[]) msg.obj;
				int number = participants.length;
				textview_participants_number.setText("Participants:" + number);
				edittext_participants.setText("");
				for(String p: participants){
					edittext_participants.append(p+"\n");
				}
				super.handleMessage(msg);
			}
		};
		
		handlerForCheckingLeader = new Handler(){
			public void handleMessage(Message msg){
				String leader = (String) msg.obj;
				//pDialog.dismiss();
				if(leader.equals(username)){
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.SubmitQuestion");
					intent.putExtra("com.cmu.passdata.leader", leader);
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.username", username);
					startActivity(intent);
					flag = false;
					finish();
				}else{
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.WaitSubmit");
					intent.putExtra("com.cmu.passdata.leader", leader);
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.username", username);
					startActivity(intent);
					flag = false;
					finish();
				}
				super.handleMessage(msg);
			}
		};
		new getInfoThread().start();
		toastHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(getApplicationContext(),
							"Problems with network", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(),
							"Json error", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(getApplicationContext(),
							"Unexpected Error", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(getApplicationContext(),
							"Wrong status returned by server", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		};
	}
	
	public class getInfoThread extends Thread{
		
		public void run() {
			while(flag){
				try {
					sleep(500);
					String info= HttpRequestUtils.getInfo(roomID);
					JSONObject jsObject = new JSONObject(info);
					String participants = (String) jsObject.get("participants");
					int status = (int) jsObject.get("status");
					String leader = jsObject.getString("leader");
					if(status == 1){
						Message msg = new Message();
						msg.obj = leader;
						handlerForCheckingLeader.sendMessage(msg);
						Log.i("Leader", leader);
						flag = false;
					}else if(status == 0){
						String[] arrayOfParticipants = participants.split(",");
						if(arrayOfParticipants.length > numberOfParticipants){
							Message msg = new Message();
							msg.obj = arrayOfParticipants;
							handler.sendMessage(msg);
						}
					}else{
						Log.e(Tag, "Wrong status returned by server");
						toastHandler.sendEmptyMessage(3);
					}
				}catch (IOException e) {
					flag = false;
					Log.e(Tag, e.toString());
					toastHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					flag = false;
					Log.e(Tag, e.toString());
					toastHandler.sendEmptyMessage(0);
				} catch (JSONException e) {
					flag = false;
					Log.e(Tag, e.toString());
					toastHandler.sendEmptyMessage(1);
				}
			}
		}
	}
	
	public class pickLeaderThread extends Thread{
		public void run(){
			try {
				HttpRequestUtils.pickLeader(roomID);
			} catch (IOException e) {
				Log.e(Tag, e.toString());
				toastHandler.sendEmptyMessage(2);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancel:
			finish();
			break;
		case R.id.Button_Begin_Vote:
			new pickLeaderThread().start();
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			break;
		}
	}
}