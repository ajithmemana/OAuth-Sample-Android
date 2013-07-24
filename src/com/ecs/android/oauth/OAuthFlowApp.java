package com.ecs.android.oauth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Entry point in the application. Launches the OAuth flow by starting the
 * PrepareRequestTokenActivity
 * 
 */
public class OAuthFlowApp extends Activity {

	private static final int PICK_CONTACT = 0;
	final String TAG = "OauthApp";
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Oncreate - OauthFlowApp.java");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth_flow_app);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Button launchOauth = (Button) findViewById(R.id.btn_launch_oauth);
		Button clearCredentials = (Button) findViewById(R.id.btn_clear_credentials);

		launchOauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "Launching oauth [Button clicked]");
				startActivity(new Intent().setClass(v.getContext(), PrepareRequestTokenActivity.class));
			}
		});

		clearCredentials.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "Clearing credentials");

				clearCredentials();
				performApiCall();
			}
		});

		// TODO ENABLE
		Toast.makeText(getBaseContext(), "Authenticated ", 0).show();
		boolean secondTime = false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getString(OAuth.OAUTH_TOKEN, "").length() != 0)
			performApiCall();
		secondTime = true;
	}

	private void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}

	private OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

	private void performApiCall() {
		TextView textView = (TextView) findViewById(R.id.response_code);

		String jsonOutput = "";
		try {
			new ReadProfileDataAsync().execute(getConsumer(this.prefs));
			System.out.println("jsonOutput : " + jsonOutput);
			JSONObject jsonResponse = new JSONObject(jsonOutput);
			JSONObject m = (JSONObject) jsonResponse.get("feed");
			JSONArray entries = (JSONArray) m.getJSONArray("entry");
			String contacts = "";
			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);
				JSONObject title = entry.getJSONObject("title");
				if (title.getString("$t") != null && title.getString("$t").length() > 0) {
					contacts += title.getString("$t") + "\n";
				}
			}
			Log.i("JSON Output", jsonOutput);
			textView.setText(contacts);
		} catch (Exception e) {
			Log.e(TAG, "Error executing request", e);
			textView.setText("Error retrieving contacts : " + jsonOutput);
		}
	}
	// private String doGet(String url,OAuthConsumer consumer) throws Exception
	// {}
}