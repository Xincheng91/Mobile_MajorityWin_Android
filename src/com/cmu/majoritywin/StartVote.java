package com.cmu.majoritywin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StartVote extends ActionBarActivity implements OnClickListener{

	private static String TAG = "StartVote";
	private TextView textView_finished;
	private TextView textView_majority_agreed;
	private TextView textView_topic;
	private String jsonString;
	private String roomID;
	private Button button_cancel;
	private Button button_submit;
	private Handler handler;
	private ProgressDialog pDialog;
	private String username;
	private Handler toastHandler;
	private boolean nextRoundLeader;
	private RadioGroup radioGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_vote);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancel);
		button_submit = (Button) this.findViewById(R.id.Button_Submit);
		button_cancel.setOnClickListener(this);
		button_submit.setOnClickListener(this);
		textView_finished = (TextView) this.findViewById(R.id.TextView_Finished);
		textView_majority_agreed = (TextView) this.findViewById(R.id.TextView_Majority_Agreed);
		textView_topic = (TextView) this.findViewById(R.id.Text_Topic);
		jsonString = getIntent().getExtras().getString("com.cmu.passdata.questions");
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		username = getIntent().getExtras().getString("com.cmu.passdata.username");
		nextRoundLeader = getIntent().getExtras().getBoolean("com.cmu.passdata.nextRoundLeader");
		radioGroup = (RadioGroup) findViewById(R.id.parentLayout);
		
		JSONObject jsObject = null;
		try {
			jsObject = new JSONObject(jsonString);
			String topic  = (String) jsObject.get("topic");
			textView_topic.setText(topic);
			for(int i = 0; i < jsObject.length() - 1; i++){
				String option = "option" + i;
				String question = jsObject.getString(option);
				RadioButton rb = new RadioButton(this);
				rb.setText(question);
				rb.setTextSize(20);
				rb.setTextColor(Color.BLACK);
				radioGroup.addView(rb);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			Toast.makeText(this, "Unexpected Json Error", Toast.LENGTH_SHORT).show();
		}
		handler = new Handler(){
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				int finished = bundle.getInt("numOfFinished");
				int majority = bundle.getInt("numOfMajority");
				int roomSize = bundle.getInt("roomSize");
				textView_finished.setText(finished + "/" + roomSize);
				textView_majority_agreed.setText(majority + "/" + roomSize);
				super.handleMessage(msg);
			}
		};
		new checkRoomStatusThread().start();
		final Context ctx = this;
		toastHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(getApplicationContext(),
							"Problems with network", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(),
							"Problems with json", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(getApplicationContext(),
							"Unexpected Error", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					pDialog = ProgressDialog.show(ctx, "Note", "Thank you for your waiting, please wait others...", true,false);
					break;
				default:
					break;
				}
			}
		};
	}

	public class checkRoomStatusThread extends Thread{
		boolean flag = true;
		public void run(){
			while(flag){
				String jsonStatus;
				try {
					sleep(500);
					jsonStatus = HttpRequestUtils.checkSubmitVoteStatus(roomID);
					JSONObject jsObject = new JSONObject(jsonStatus);
					int roomSize = jsObject.getInt("roomSize");
					int numOfFinished = jsObject.getInt("numOfFinished");
					int numOfMajority = jsObject.getInt("numOfMajority");
					Log.i(TAG, "numOfMajority: " + numOfMajority);
					int status = jsObject.getInt("status");
					String voteResult = jsObject.getString("result");
					if(status == 3){
						if(pDialog!=null && pDialog.isShowing()){
							pDialog.dismiss();
						}
						Intent intent = new Intent();
						intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.VoteResult");
						intent.putExtra("com.cmu.passdata.roomID", roomID);
						intent.putExtra("com.cmu.passdata.result", voteResult);
						intent.putExtra("com.cmu.passdata.numOfMajority", numOfMajority+"");
						intent.putExtra("com.cmu.passdata.username", username);
						intent.putExtra("com.cmu.passdata.nextRoundLeader", nextRoundLeader);
						intent.putExtra("com.cmu.passdata.roomSize", roomSize);
						startActivity(intent);
						finish();
						flag = false;
					}else{
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt("numOfFinished", numOfFinished);
						bundle.putInt("roomSize", roomSize);
						bundle.putInt("numOfMajority", numOfMajority);
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
				} catch (IOException e) {
					flag = false;
					Log.e(TAG, e.toString());
					toastHandler.sendEmptyMessage(0);
				} catch (JSONException e) {
					flag = false;
					Log.e(TAG, "Unexpected JSON Error");
					toastHandler.sendEmptyMessage(1);
				} catch (InterruptedException e) {
					flag = false;
					Log.e(TAG, e.toString());
					toastHandler.sendEmptyMessage(2);
				}
			}
		}
	}
	
	public class submitVoteThread extends Thread{
		public void run(){
			int option = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
			//Log.i(TAG, radioGroup.);
			Log.i(TAG, option + "");
			try {
				HttpRequestUtils.submitVote(roomID, username, option);
				toastHandler.sendEmptyMessage(3);
			} catch (IOException e) {
				Log.e(TAG, "Unexpected Network Error");
				toastHandler.sendEmptyMessage(0);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Cancel:
			finish();
			break;
		case R.id.Button_Submit:
			new submitVoteThread().start();
			break;
		default:
			Log.e(TAG, "Unexpected Error");
			Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
