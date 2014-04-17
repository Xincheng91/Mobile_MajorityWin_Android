package com.cmu.majoritywin;

import java.io.IOException;
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
	private ProgressDialog pDialog;
	private String username;
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
		
		handler = new Handler(){
			public void handleMessage(Message msg) {
				String[] participants = (String[]) msg.obj;
				int number = participants.length;
				textview_participants_number.setText(number + "/5");
				edittext_participants.setText("");
				for(String p: participants){
					edittext_participants.append(p+"\n");
				}
				super.handleMessage(msg);
			}
		};
		
		handlerForCheckingLeader = new Handler(){
			public void handleMessage(Message msg){
				String status = (String) msg.obj;
				pDialog.dismiss();
				if(status.equals(username)){
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.SubmitVote");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					startActivity(intent);
				}else{
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.WaitSubmit");
					intent.putExtra("com.cmu.passdata.leader", status);
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					startActivity(intent);
				}
				super.handleMessage(msg);
			}
		};
		
		new participantsThread().start();
	}
	
	public class participantsThread extends Thread{
		public void run() {
			while(true){
				try {
					sleep(500);
					String participants= HttpRequestUtils.getParticipants(roomID);
					String[] arrayOfParticipants = participants.split(",");
					if(arrayOfParticipants.length > numberOfParticipants){
						Message msg = new Message();
						msg.obj = arrayOfParticipants;
						handler.sendMessage(msg);
					}
				}catch (IOException e) {
					Log.e(Tag, e.toString());
					Toast.makeText(getApplicationContext(), "Problems with network",
							Toast.LENGTH_SHORT).show();
				} catch (InterruptedException e) {
					Log.e(Tag, e.toString());
					Toast.makeText(getApplicationContext(), "Problems with network",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	public class beginVoteThread extends Thread{
		private boolean flag = false;
		public void run(){
			while(true){
				try {
					if(!flag){
						HttpRequestUtils.pickLeader(roomID);
						flag = true;
					}else{
						String status = HttpRequestUtils.checkLeaderStatus(roomID);
						//username represents username of leader, @NotReady represents notready
						if(status.equals("@NotReady")){
							continue;
						}else{
							Message msg = new Message();
							msg.obj = status;
							handlerForCheckingLeader.sendMessage(msg);
						}
					};
				} catch (IOException e) {
					Log.e(Tag, e.toString());
					Toast.makeText(getApplicationContext(), "Problems with network",
							Toast.LENGTH_SHORT).show();
				}
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
			new beginVoteThread().start();
			pDialog = ProgressDialog.show(this, "Please Wait", "Server is deciding the leader", true,false);
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			break;
		}
	}
}