package com.cmu.majoritywin;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

public class VoteResult extends ActionBarActivity implements OnClickListener{

	private static String TAG = "VoteResult";
	private Button button_exit;
	private Button button_next_round;
	private TextView textView_result;
	private String roomID;
	private String result;
	private String majority;
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
		majority = getIntent().getExtras().getString("com.cmu.passdata.numOfMajority") + "/5";
		textView_result.setText(result + " - " + majority);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Next_Round:
			finish();
			break;
		case R.id.Button_Exit:
			finish();
			break;
		default:
			Log.e(TAG, "Unexpected Error");
			Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
