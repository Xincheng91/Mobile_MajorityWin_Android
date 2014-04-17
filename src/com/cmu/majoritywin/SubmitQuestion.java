package com.cmu.majoritywin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SubmitQuestion extends ActionBarActivity implements
		OnClickListener {

	private static String TAG = "SubmitQuestion";
	private EditText editText_option1;
	private EditText editText_option2;
	private EditText editText_option3;
	private EditText editText_topic;
	private Button button_submit_question;
	private Button button_giveup;
	private String roomID;
	private TextView textView_timer;
	private Handler handler;
	private int TimerCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_question);
		editText_option1 = (EditText) this.findViewById(R.id.EditText_Option1);
		editText_option2 = (EditText) this.findViewById(R.id.EditText_Option2);
		editText_option3 = (EditText) this.findViewById(R.id.EditText_Option3);
		editText_topic = (EditText) this.findViewById(R.id.Edit_Topic);
		button_submit_question = (Button) this
				.findViewById(R.id.Button_Submit_Question);
		button_giveup = (Button) this.findViewById(R.id.Button_Give_Up);
		textView_timer = (TextView) this.findViewById(R.id.TextView_TimeOut);
		button_submit_question.setOnClickListener(this);
		button_giveup.setOnClickListener(this);
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
		handler = new Handler() {
			public void handleMessage(Message msg) {
				TimerCounter++;
				if (TimerCounter >= 120) {
					GiveUp();
				} else {
					textView_timer.setText(TimerCounter + "s / 120s");
				}
				super.handleMessage(msg);
			}

		};
		new TimerThread().start();
	}

	private void GiveUp() {
		
	}

	public class TimerThread extends Thread {
		public void run() {
			while (true) {
				try {
					sleep(1000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Submit_Question:
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.accumulate("topic", editText_topic.getText()
						.toString());
				jsonObject.accumulate("option1", editText_option1.getText()
						.toString());
				jsonObject.accumulate("option2", editText_option2.getText()
						.toString());
				jsonObject.accumulate("option3", editText_option3.getText()
						.toString());
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				Toast.makeText(this, "Unexpected Json Error",
						Toast.LENGTH_SHORT).show();
			}
			String json = jsonObject.toString();
			try {
				HttpRequestUtils.submitQuestion(json, roomID);
				Intent intent = new Intent();
				intent.setClassName("com.cmu.majoritywin",
						"com.cmu.majoritywin.StartVote");
				intent.putExtra("com.cmu.passdata.roomID", roomID);
				intent.putExtra("com.cmu.passdata.questions", json);
				startActivity(intent);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				Toast.makeText(this, "Unexpected Network Error",
						Toast.LENGTH_SHORT).show();
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
									GiveUp();
									finish();
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
