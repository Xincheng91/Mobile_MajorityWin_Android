package com.cmu.majoritywin;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import com.cmu.http.HttpRequestUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SubmitQuestion extends ActionBarActivity implements
		OnClickListener {

	private static String TAG = "SubmitQuestion";
	private EditText editText_topic;
	private Button button_submit_question;
	private Button button_giveup;
	private String roomID;
	private TextView textView_timer;
	private Handler handler;
	private int TimerCounter;
	private String username;
	private boolean startVoteFlag;
	private Handler toastHandler;
	private LinearLayout mContainerView;
	private Button mAddButton;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		editText_topic = (EditText) this.findViewById(R.id.Edit_Topic);
		button_submit_question = (Button) this
				.findViewById(R.id.Button_Submit_Question);
		button_giveup = (Button) this.findViewById(R.id.Button_Give_Up);
		textView_timer = (TextView) this.findViewById(R.id.TextView_TimeOut);
		button_submit_question.setOnClickListener(this);
		button_giveup.setOnClickListener(this);
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		username = getIntent().getExtras().getString("com.cmu.passdata.username");
		mContainerView = (LinearLayout) findViewById(R.id.parentView);
	    mAddButton = (Button) findViewById(R.id.btnAddNewItem);
	    mAddButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onAddNewClicked(v);
			}
		});
		handler = new Handler() {
			public void handleMessage(Message msg) {
				TimerCounter++;
				if (TimerCounter == 120) {
					new GiveUpThread().start();
				} else if (TimerCounter < 120){
					textView_timer.setText(TimerCounter + "s / 120s");
				}
				super.handleMessage(msg);
			}
		};
		new TimerThread().start();
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

	public void onAddNewClicked(View v) {
	    inflateEditRow(null);
	}

	public void onDeleteClicked(View v) {
	    mContainerView.removeView((View) v.getParent());
	}
	
	@SuppressLint("NewApi")
	private void inflateEditRow(String name) {
	    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View rowView = inflater.inflate(R.layout.option_row, null);
	    final Button deleteButton = (Button)rowView
	            .findViewById(R.id.buttonDelete);
	    EditText editText = (EditText) rowView
	            .findViewById(R.id.editText);
	    
	    if (name != null && !name.isEmpty()) {
	        editText.setText(name);
	    }
	    deleteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onDeleteClicked(v);
			}
		});
	    mContainerView.addView(rowView, mContainerView.getChildCount() - 1);
	}
	
	private class GiveUpThread extends Thread{
		public void run(){
			try {
				String newLeader = HttpRequestUtils.giveUp(roomID, username);
				if(username.equals(newLeader)){
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.SubmitQuestion");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.username", username);
					startActivity(intent);
					finish();
					flag = false;
				}else{
					Intent intent = new Intent();
					intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.WaitSubmit");
					intent.putExtra("com.cmu.passdata.roomID", roomID);
					intent.putExtra("com.cmu.passdata.leader", newLeader);
					intent.putExtra("com.cmu.passdata.username", username);
					startActivity(intent);
					finish();
					flag = false;
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				toastHandler.sendEmptyMessage(0);
			}
		}
	}

	public class TimerThread extends Thread {
		int count = 0;
		public void run() {
			while (flag) {
				try {
					if(count<=125){
						sleep(1000);
						handler.sendEmptyMessage(0);
						count++;
					}else{
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class startVoteThread extends Thread{
		public void run(){
			JSONObject jsonObject = new JSONObject();
			try {
				int count = mContainerView.getChildCount();
				jsonObject.accumulate("topic", editText_topic.getText()
						.toString());
				for(int i = 0; i < count - 1; i++){
					final LinearLayout child = (LinearLayout) mContainerView.getChildAt(i);
				    EditText edittext = (EditText) child.getChildAt(1);
			        jsonObject.accumulate("option" + i, edittext.getText().toString());
				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				toastHandler.sendEmptyMessage(1);
			}
			String json = jsonObject.toString();
			try {
				HttpRequestUtils.submitQuestion(json, roomID);
				Intent intent = new Intent();
				intent.setClassName("com.cmu.majoritywin",
						"com.cmu.majoritywin.StartVote");
				intent.putExtra("com.cmu.passdata.roomID", roomID);
				intent.putExtra("com.cmu.passdata.questions", json);
				intent.putExtra("com.cmu.passdata.username", username);
				intent.putExtra("com.cmu.passdata.nextRoundLeader", true);
				startActivity(intent);
				finish();
				flag = false;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				toastHandler.sendEmptyMessage(0);
			}
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Submit_Question:
			if(!startVoteFlag){
				new startVoteThread().start();
				startVoteFlag = true;
			}
			break;
		case R.id.Button_Give_Up:
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Note")
					.setMessage(
							"Are you sure you want to give up the leader chance?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									new GiveUpThread().start();
								}

							}).setNegativeButton("No", null).show();
			break;
		default:
			Log.e(TAG, "Unexpected Error");
			Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
