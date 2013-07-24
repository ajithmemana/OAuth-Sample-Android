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
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Entry point in the application. Launches the OAuth flow by starting the
 * PrepareRequestTokenActivity
 * 
 */
public class OAuthFlowApp extends Activity {

	final String TAG = "OauthApp";
	private SharedPreferences prefs;
	TextView profileId, profileName, profileGender, profileBirthday, profileLocale;
	ImageView profilePic;
	ProgressBar profilePicLoader;
	Button readProfile;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate - OauthFlowApp.java");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth_flow_app);
		initViews();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Button launchOauth = (Button) findViewById(R.id.btn_launch_oauth);
		Button clearCredentials = (Button) findViewById(R.id.clear_oauth);
		 readProfile = (Button) findViewById(R.id.read_profile);

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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getString(OAuth.OAUTH_TOKEN, "").length() != 0
				&& prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "").length() != 0) {
			readProfile.setBackgroundColor(Color.RED);
		}
		// Read profile data
		readProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performApiCall();

			}
		});
	}

	private void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
		readProfile.setBackgroundColor(Color.GRAY);

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
		Log.v(TAG, "Reading profile data using Aync task");
		String jsonOutput = "";
		try {
			new ReadProfileDataAsync(profileId, profileName, profileGender, profileBirthday, profilePic,
					profilePicLoader, this).execute(getConsumer(this.prefs));
			System.out.println("jsonOutput : " + jsonOutput);
			JSONObject jsonResponse = new JSONObject(jsonOutput);
			JSONObject m = (JSONObject) jsonResponse.get("feed");
			JSONArray entries = (JSONArray) m.getJSONArray("entry");
			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);
				JSONObject title = entry.getJSONObject("title");
				if (title.getString("$t") != null && title.getString("$t").length() > 0) {
				}
			}
			Log.i("JSON Output", jsonOutput);
		} catch (Exception e) {
			Log.e(TAG, "Error executing request", e);
			profileName.setText("Error retrieving Profile data : " + jsonOutput);
		}
	}

	public void initViews() {
		profileId = (TextView) findViewById(R.id.profile_id);
		profileName = (TextView) findViewById(R.id.profile_name);
		profileGender = (TextView) findViewById(R.id.profile_gender);
		profileBirthday = (TextView) findViewById(R.id.profile_birthday);

		profilePicLoader = (ProgressBar) findViewById(R.id.progressLoader);
		profilePic = (ImageView) findViewById(R.id.imageView1);

	}
}