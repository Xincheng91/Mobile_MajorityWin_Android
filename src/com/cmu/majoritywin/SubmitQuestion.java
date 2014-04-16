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
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;

public class SubmitQuestion extends ActionBarActivity implements
		OnClickListener {

	private static String TAG = "SubmitQuestion";
	private EditText editText_option1;
	private EditText editText_option2;
	private EditText editText_option3;
	private EditText editText_topic;
	private Button button_submit_question;
	private Button button_cancel;
	private String roomID;
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
		button_cancel = (Button) this.findViewById(R.id.Button_Cancle);
		button_submit_question.setOnClickListener(this);
		button_cancel.setOnClickListener(this);
		roomID = getIntent().getExtras().getString("com.cmu.passdata.roomID");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_Submit_Question:
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.accumulate("topic", editText_topic.getText().toString());
				jsonObject.accumulate("option1", editText_option1.getText().toString());
				jsonObject.accumulate("option2", editText_option2.getText().toString());
				jsonObject.accumulate("option3", editText_option3.getText().toString());
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				Toast.makeText(this, "Unexpected Json Error", Toast.LENGTH_SHORT).show();
			}
			String json = jsonObject.toString();
			try {
				HttpRequestUtils.submitQuestion(json, roomID);
				Intent intent = new Intent();
				intent.setClassName("com.cmu.majoritywin", "com.cmu.majoritywin.StartVote");
				intent.putExtra("com.cmu.passdata.roomID", roomID);
				intent.putExtra("com.cmu.passdata.questions", json);
				startActivity(intent);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				Toast.makeText(this, "Unexpected Network Error", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.Button_Cancle:
			finish();
			//Actually we need to close the room through network and notify other user
			break;
		default:
			Log.e(TAG, "Unexpected Error");
			Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
