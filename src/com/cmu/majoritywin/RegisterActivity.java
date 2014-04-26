package com.cmu.majoritywin;

import java.io.IOException;

import com.cmu.http.HttpRequestUtils;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity implements OnClickListener{

	private Button register_cancel;
	private Button register;
	private Handler handler;
	private TextView textView_username;
	private TextView textView_password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		textView_password = (TextView) findViewById(R.id.password);
		textView_username = (TextView) findViewById(R.id.username);
		register = (Button) findViewById(R.id.register_button);
		register_cancel = (Button) findViewById(R.id.register_cancel);
		register.setOnClickListener(this);
		register_cancel.setOnClickListener(this);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				boolean bool = (boolean) msg.obj;
				if(bool){
					Toast.makeText(getApplicationContext(), "Register Success",
							Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "Register Fail",
							Toast.LENGTH_SHORT).show();
				}
				super.handleMessage(msg);
			}
			
		};
	}
	
	public class registerThread extends Thread{
		public void run(){
			String username = textView_username.getText().toString();
			String password = textView_password.getText().toString();
			boolean result;
			Message msg = new Message();
			try {
				result = HttpRequestUtils.register(username, password);
				msg.obj = result;
				handler.sendMessage(msg);
			} catch (IOException e) {
				msg.obj = false;
				handler.sendMessage(msg);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_button:
			new registerThread().start();
			break;
		case R.id.register_cancel:
			finish();
			break;
		default:
			break;
		}
	}

}
