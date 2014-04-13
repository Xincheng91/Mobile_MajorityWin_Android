package com.cmu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpRequestUtils {

	private static HttpClient httpClient;
	private static String ServerIP = "http://localhost:8080/";

	public static HttpClient getHttpClientInstance() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public static int getUniqueRoomNumber() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(ServerIP + "CreateRoom");
		HttpResponse response = getHttpClientInstance().execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			InputStream is = response.getEntity().getContent();
			String result = convertStreamToString(is);
			return Integer.parseInt(result);
		}else{
			throw new IllegalStateException("Network Failure");
		}
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
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
