package com.cmu.majoritywin;

import java.io.IOException;

import com.cmu.http.HttpRequestUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class VoteResult extends ActionBarActivity implements OnClickListener{

	private static String TAG = "VoteResult";
	private Button button_exit;
	private Button button_next_round;
	private TextView textView_result;
	private String roomID;
	private String result;
	private String majority;
	private String username;
	private boolean nextRoundLeader;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote_result);
		button_exit = (Button) this.findViewById(R.id.Button_Exit);
		button_next_round = (Button) this.findViewById(R.id.Button_Next_Round);
		textView_result = (TextView) this.findViewById(R.id.TextView_Result);
		button_exit.setOnClickListener(this);
		button_next_round.setOnClickListener(this);
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		result = getIntent().getExtras().getString("com.cmu.passdata.result");
		username = getIntent().getExtras().getString("com.cmu.passdata.username");
		nextRoundLeader = getIntent().getExtras().getBoolean("com.cmu.passdata.nextRoundLeader");
		int roomSize = getIntent().getExtras().getInt("com.cmu.passdata.roomSize");
		majority = getIntent().getExtras().getString("com.cmu.passdata.numOfMajority") + "/" + roomSize;
		textView_result.setText(result + " - " + majority);
		handler = new Handler(){
			public void handleMessage(Message msg) {
				if(nextRoundLeader){
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin",
							"com.cmu.majoritywin.EnterRoomActivity");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.username", username);
					intent.putExtra("com.cmu.passdata.isCreater", true);
					startActivity(intent);
					finish();
				}else{
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin",
							"com.cmu.majoritywin.EnterRoomActivity");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.username", username);
					intent.putExtra("com.cmu.passdata.isCreater", false);
					startActivity(intent);
					finish();
				}
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Next_Round:
			new nextRoundThread().start();
			break;
		case R.id.Button_Exit:
			finish();
			break;
		default:
			Log.e(TAG, "Unexpected Error");
			break;
		}
	}

	public class nextRoundThread extends Thread{
		public void run(){
			try {
				boolean result = HttpRequestUtils.startNextRound(roomID,username);
				Message msg = new Message();
				msg.obj = result;
				handler.sendMessage(msg);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				return;
			}
		}
	}
}
