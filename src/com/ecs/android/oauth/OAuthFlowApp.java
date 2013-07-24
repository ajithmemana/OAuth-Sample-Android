package com.ecs.android.oauth;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

	final String TAG = "OauthApp";
	private SharedPreferences prefs;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate - OauthFlowApp.java");
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
				// Not necessary i guess
				performApiCall();
			}
		});
		// Read profile data
		performApiCall();
	}

	private void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}
	// Read a consumer auth token info from Prefs - already saved
	private OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

	private void performApiCall() {
		TextView textView = (TextView) findViewById(R.id.response_code);
		Log.v(TAG, "Reading profile data using Aync task");
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
			textView.setText("Error retrieving Profile data : " + jsonOutput);
		}
	}
}