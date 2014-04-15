package com.cmu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpRequestUtils {

	private static String Tag = "HttpRequestUtils";
	private static String ServerIP = "http://128.237.200.74:8080/MajorityWin/";

	public static String createRoom(String username) throws ClientProtocolException,
			IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(ServerIP + "CreateRoom?username=" + username);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			Log.i(Tag, "result: " + result);
			return result;
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}

	public static String getParticipants(String roomID) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		HttpGet httpGet = new HttpGet(ServerIP + "GetRoomInfo?roomID=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			Log.i(Tag, "result: " + result);
			return result;
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}
	
	public static boolean joinRoom(String roomID, String username) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param1 = URLEncoder.encode(roomID);
		String param2 = URLEncoder.encode(username);
		HttpGet httpGet = new HttpGet(ServerIP + "JoinRoom?roomID=" + param1 + "&username=" + param2);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			if(result.equals("Success")){
				return true;
			}else{
				return false;
			}
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}

	public static boolean startVoting(String roomID) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		HttpGet httpGet = new HttpGet(ServerIP + "StartRoom?roomID=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			if(result.equals("Success")){
				return true;
			}else{
				return false;
			}
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}
	
	public static int checkLeader(String roomID) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		HttpGet httpGet = new HttpGet(ServerIP + "CheckLeader?roomID=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			return Integer.parseInt(result);
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}
	
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
