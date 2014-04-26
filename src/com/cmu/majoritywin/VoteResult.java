package com.cmu.majoritywin;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;

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
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Next_Round:
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
			break;
		case R.id.Button_Exit:
			finish();
			break;
		default:
			Log.e(TAG, "Unexpected Error");
			break;
		}
	}

}
