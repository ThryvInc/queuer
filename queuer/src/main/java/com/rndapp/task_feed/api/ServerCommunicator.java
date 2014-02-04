package com.rndapp.task_feed.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ServerCommunicator {
	private Context context;
	public static final String BASE_URL = "http://queuer-rndapp.rhcloud.com/api/v1/"; //192.168.11.152:3000
	public static final String API_KEY_PREFERENCE = "com.rndapp.queuer.api_key_pref";
    public static final String API_KEY_HEADER = "X-Qer-Authorization";
	
	public ServerCommunicator(Context ctxt){
		this.context = ctxt;
	}

    public String postToEndpointAuthed(String endpoint, JSONObject postData){
        Log.d("postData", postData.toString());
        SharedPreferences sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        String output = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(BASE_URL + endpoint);

        //ArrayList<BufferedHeader> headers = getAuthHeaders();

        try {
            httpPost.setHeader("Content-type","application/json; charset=utf-8");
            httpPost.setHeader(API_KEY_HEADER, sp.getString("api_key", ""));

            if (postData != null){
                StringEntity se = new StringEntity(postData.toString());
                se.setContentEncoding("UTF-8");
                se.setContentType("application/json");

                httpPost.setEntity(se);
            }

            HttpResponse response = client.execute(httpPost);
            output = readResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(endpoint, output+"--------------------------\n\n\n");
        return output;
    }

    public String putToEndpointAuthed(String endpoint, JSONObject postData){
        Log.d("postData", postData.toString());
        SharedPreferences sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        String output = "";
        HttpClient client = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(BASE_URL + endpoint);

        //ArrayList<BufferedHeader> headers = getAuthHeaders();

        try {
            httpPut.setHeader("Content-type", "application/json; charset=utf-8");
            httpPut.setHeader(API_KEY_HEADER, sp.getString("api_key", ""));

            if (postData != null){
                StringEntity se = new StringEntity(postData.toString());
                se.setContentEncoding("UTF-8");
                se.setContentType("application/json");

                httpPut.setEntity(se);
            }

            HttpResponse response = client.execute(httpPut);
            output = readResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(endpoint, output+"\n\n\n");
        return output;
    }
	
	public String postToEndpointUnauthed(String endpoint, JSONObject postData){
        Log.d("postData", postData.toString());
		String output = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(BASE_URL + endpoint);
		
		try {
			httpPost.setHeader("Content-type","application/json; charset=utf-8");
			
			StringEntity se = new StringEntity(postData.toString());
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");
			
			httpPost.setEntity(se);
			HttpResponse response = client.execute(httpPost);
			output = readResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Log.d(endpoint, output);
		return output;
	}
	
	public String getEndpointUnauthed(String endpoint){
		String output = "";
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(BASE_URL + endpoint);
		try {
			httpGet.setHeader("Content-type","application/json; charset=utf-8");
			HttpResponse response = client.execute(httpGet);
			output = readResponse(response);			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(endpoint, output);
		return output;
	}
	
	public String getEndpointAuthed(String endpoint){
		SharedPreferences sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
		String output = "";
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(BASE_URL + endpoint);
		try {
			httpGet.setHeader("Content-type","application/json; charset=utf-8");
			httpGet.setHeader(API_KEY_HEADER, sp.getString("api_key", ""));
			
			HttpResponse response = client.execute(httpGet);
			output = readResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(endpoint, output);
		return output;
	}
	
	private static String readResponse(HttpResponse response) throws IOException{
		StringBuilder builder = new StringBuilder();
		if (response != null){
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
            Log.d("response status code", String.valueOf(statusCode));
            Log.d("response status line", statusLine.getReasonPhrase());
            if (response.getEntity() != null){
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String l;
				while ((l = reader.readLine()) != null) {
					builder.append(l);
				}
            }
		}
		return builder.toString();
	}

}
