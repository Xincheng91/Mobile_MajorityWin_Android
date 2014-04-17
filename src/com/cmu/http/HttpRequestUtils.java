package com.cmu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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

	public static boolean pickLeader(String roomID) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		HttpGet httpGet = new HttpGet(ServerIP + "PickLeader?roomID=" + param);
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
	
	public static String checkLeaderStatus(String roomID) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		HttpGet httpGet = new HttpGet(ServerIP + "CheckLeader?roomID=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			return result;
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}
	
	public static void submitQuestion(String json, String roomID) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(ServerIP+"SubmitQuestions");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("roomID", roomID));
		parameters.add(new BasicNameValuePair("questions", json));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        HttpResponse httpResponse = httpClient.execute(httpPost);
        int code = httpResponse.getStatusLine().getStatusCode();
		if (code == 200) {
			//InputStream is = httpResponse.getEntity().getContent();
			//String result = convertStreamToString(is);
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}

	public static void submitVote(int option) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(option+"");
		HttpGet httpGet = new HttpGet(ServerIP + "SubmitVote?vote=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			//InputStream is = response.getEntity().getContent();
			//String result = convertStreamToString(is);
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}

	public static String checkSubmitQuestionsStatus(String roomID) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		//checkSubmitQuestionsStatus reture "" or "OK"
		HttpGet httpGet = new HttpGet(ServerIP + "CheckQuestionStatus?roomID=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			return result;
		} else {
			Log.e(Tag, "Code:" + code);
			throw new IllegalStateException("Network Failure");
		}
	}

	public static String checkSubmitVoteStatus(String roomID) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		String param = URLEncoder.encode(roomID);
		//checkSubmitVoteStatus reture jsonString of jsonObject: numOfFinished int, numOfMajority int, done boolean, result String;
		HttpGet httpGet = new HttpGet(ServerIP + "CheckSubmitStatus?roomID=" + param);
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			return result;
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
