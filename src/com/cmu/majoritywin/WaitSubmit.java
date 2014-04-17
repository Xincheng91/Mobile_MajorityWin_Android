package com.cmu.majoritywin;

import java.io.IOException;

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
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class WaitSubmit extends ActionBarActivity {

	private static String TAG = "WaitSubmit";
	private Button button_back;
	private TextView text_leader_submit;
	private String leader;
	private String roomID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_submit);
		button_back = (Button) this.findViewById(R.id.Button_Cancel);
		text_leader_submit = (TextView) this.findViewById(R.id.TextView_Leader);
		leader = getIntent().getExtras().getString("com.cmu.passdata.leader");
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		text_leader_submit.setText(leader + " is the leader, he/she is submitting the vote");
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		new checkSubmitStatusThread().start();
		button_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	public class checkSubmitStatusThread extends Thread{
		public void run(){
			while(true){
				String json;
				try {
					sleep(500);
					json = HttpRequestUtils.checkSubmitQuestionsStatus(roomID);
					if(!json.equals("")){
						Intent intent = new Intent();
						intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.StartVote");
						intent.putExtra("com.cmu.passdata.roomID", roomID);
						intent.putExtra("com.cmu.passdata.questions", json);
						startActivity(intent);
						finish();
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
					Toast.makeText(getApplicationContext(), "Unexpected Network Error", Toast.LENGTH_SHORT).show();
					finish();
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
					Toast.makeText(getApplicationContext(), "Unexpected Network Error", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}
	}
}
