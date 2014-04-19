package com.cmu.majoritywin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WaitSubmit extends ActionBarActivity {

	private static String TAG = "WaitSubmit";
	private Button button_back;
	private TextView text_leader_submit;
	private String leader;
	private String roomID;
	private String username;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_submit);
		button_back = (Button) this.findViewById(R.id.Button_Cancel);
		text_leader_submit = (TextView) this.findViewById(R.id.TextView_Leader);
		leader = getIntent().getExtras().getString("com.cmu.passdata.leader");
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		text_leader_submit.setText(leader + " is the leader, he/she is submitting the vote");
		username = getIntent().getExtras().getString("com.cmu.passdata.username");
		
		new checkSubmitStatusThread().start();
		button_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		handler = new Handler(){
			public void handleMessage(Message msg) {
				text_leader_submit.setText((String)msg.obj);
			}
		};
	}
	
	public class checkSubmitStatusThread extends Thread{
		public void run(){
			while(true){
				String json;
				try {
					sleep(500);
					json = HttpRequestUtils.checkSubmitQuestionsStatus(roomID);
					JSONObject jsObject = new JSONObject(json);
					String questions = jsObject.getString("questions");
					String newLeader = jsObject.getString("leader");
					if(jsObject.getBoolean("OK")){
						Intent intent = new Intent();
						intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.StartVote");
						intent.putExtra("com.cmu.passdata.roomID", roomID);
						intent.putExtra("com.cmu.passdata.questions", questions);
						intent.putExtra("com.cmu.passdata.username", username);
						startActivity(intent);
						finish();
					}else{
						if(!newLeader.equals(leader)){
							Toast.makeText(getApplicationContext(), "Leader is changed", Toast.LENGTH_SHORT).show();
							Message msg = new Message();
							msg.obj = newLeader;
							handler.sendMessage(msg);
						}else{
							Toast.makeText(getApplicationContext(), "You are the leader now", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.SubmitVote");
							intent.putExtra("com.cmu.passdata.roomID", roomID);
							intent.putExtra("com.cmu.passdata.questions", questions);
							startActivity(intent);
							finish();
						}
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
					Toast.makeText(getApplicationContext(), "Unexpected Network Error", Toast.LENGTH_SHORT).show();
					finish();
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
					Toast.makeText(getApplicationContext(), "Unexpected Network Error", Toast.LENGTH_SHORT).show();
					finish();
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
					Toast.makeText(getApplicationContext(), "Unexpected JSON Error", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}
	}
}
