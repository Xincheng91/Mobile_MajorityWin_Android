package com.cmu.majoritywin;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

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
	private int numberOfParticipants = 0;
	private int roomID;
	private ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_room);
		button_cancle = (Button) this.findViewById(R.id.Button_Cancle);
		button_start_voting = (Button) this.findViewById(R.id.Button_Begin_Vote);
		textview_participants_number = (TextView) this.findViewById(R.id.TextView_ParticipantNumber);
		edittext_participants = (EditText) this.findViewById(R.id.EditText_Participants);
		button_cancle.setOnClickListener(this);
		button_start_voting.setOnClickListener(this);
		Intent intent = getIntent();
		roomID = Integer.parseInt(intent.getExtras().getString("com.cmu.passdata.roomID").trim());
		
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
		public void run(){
			try {
				if(HttpRequestUtils.startVoting(roomID)){
					
				};
			} catch (IOException e) {
				Log.e(Tag, e.toString());
				Toast.makeText(getApplicationContext(), "Problems with network",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancle:
			finish();
			break;
		case R.id.Button_Begin_Vote:
			new beginVoteThread().start();
			pDialog = ProgressDialog.show(this, "deciding the leader", "Please wait", true,false);
			break;
		default:
			Log.e(Tag, "Unexpected Error");
			break;
		}
	}
}