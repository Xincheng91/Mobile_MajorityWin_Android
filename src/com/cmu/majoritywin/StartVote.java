package com.cmu.majoritywin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class StartVote extends ActionBarActivity implements OnClickListener{

	private static String TAG = "StartVote";
	private RadioButton option1;
	private RadioButton option2;
	private RadioButton option3;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_vote);
		button_cancel = (Button) this.findViewById(R.id.Button_Cancel);
		button_submit = (Button) this.findViewById(R.id.Button_Submit);
		button_cancel.setOnClickListener(this);
		button_submit.setOnClickListener(this);
		option1 = (RadioButton) this.findViewById(R.id.radioButton1);
		option2 = (RadioButton) this.findViewById(R.id.radioButton2);
		option3 = (RadioButton) this.findViewById(R.id.radioButton3);
		textView_finished = (TextView) this.findViewById(R.id.TextView_Finished);
		textView_majority_agreed = (TextView) this.findViewById(R.id.TextView_Majority_Agreed);
		textView_topic = (TextView) this.findViewById(R.id.Text_Topic);
		jsonString = getIntent().getExtras().getString("com.cmu.passdata.questions");
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		username = getIntent().getExtras().getString("com.cmu.passdata.username");
		JSONObject jsObject = null;
		try {
			jsObject = new JSONObject(jsonString);
			String topic  = (String) jsObject.get("topic");
			String question1  = (String) jsObject.get("option1");
			String question2  = (String) jsObject.get("option2");
			String question3  = (String) jsObject.get("option3");
			textView_topic.setText(topic);
			option1.setText(question1);
			option2.setText(question2);
			option3.setText(question3);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			Toast.makeText(this, "Unexpected Json Error", Toast.LENGTH_SHORT).show();
			finish();
		}
		handler = new Handler(){
			public void handleMessage(Message msg) {
				int num1 = msg.arg1;
				int num2 = msg.arg2;
				textView_finished.setText(num1 + "/5");
				textView_majority_agreed.setText(num2 + "/5");
				super.handleMessage(msg);
			}
		};
		new checkRoomStatusThread().start();
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
				default:
					break;
				}
			}
		};
	}

	public class checkRoomStatusThread extends Thread{
		public void run(){
			while(true){
				String jsonStatus;
				try {
					sleep(500);
					jsonStatus = HttpRequestUtils.checkSubmitVoteStatus(roomID);
					JSONObject jsObject = new JSONObject(jsonStatus);
					int numOfFinished = jsObject.getInt("numOfFinished");
					int numOfMajority = jsObject.getInt("numOfMajority");
					int status = jsObject.getInt("status");
					String voteResult = jsObject.getString("result");
					if(status == 3){
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
						Intent intent = new Intent();
						intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.VoteResult");
						intent.putExtra("com.cmu.passdata.roomID", roomID);
						intent.putExtra("com.cmu.passdata.result", voteResult);
						intent.putExtra("com.cmu.passdata.numOfMajority", numOfMajority);
						startActivity(intent);
					}else{
						Message msg = new Message();
						msg.arg1 = numOfFinished;
						msg.arg2 = numOfMajority;
						handler.sendMessage(msg);
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
					toastHandler.sendEmptyMessage(0);
				} catch (JSONException e) {
					Log.e(TAG, "Unexpected JSON Error");
					toastHandler.sendEmptyMessage(1);
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
					toastHandler.sendEmptyMessage(2);
				}
			}
		}
	}
	
	public class submitVoteThread extends Thread{
		public void run(){
			int option = 0;
			if(option1.isChecked()){
				option = 1;	
			}else if(option2.isChecked()){
				option = 2;
			}else if(option3.isChecked()){
				option = 3;
			}
			try {
				HttpRequestUtils.submitVote(roomID, username, option);
			} catch (IOException e) {
				Log.e(TAG, "Unexpected Network Error");
				toastHandler.sendEmptyMessage(0);
				finish();
			}
			pDialog = ProgressDialog.show(getApplicationContext(), "Note", "Thank you for your waiting, please wait others...", true,false);
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
