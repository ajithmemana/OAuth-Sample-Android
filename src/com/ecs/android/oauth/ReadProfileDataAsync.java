package com.ecs.android.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ecs.android.oauth.Imageloader.ImageLoader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ReadProfileDataAsync extends AsyncTask<OAuthConsumer, Void, String> {
	String TAG = "OauthApp";
	BufferedReader bufferedReader;
	TextView profileId, profileName, profileGender, profileBirthday;
	ImageView profilePic;
	ProgressBar profilePicLoader;
	Context context;
	public ReadProfileDataAsync(TextView profileId, TextView profileName, TextView profileGender,
			TextView profileBirthday, ImageView profilePic, ProgressBar profilePicLoader, Context context) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.profileGender = profileGender;
		this.profileBirthday = profileBirthday;
		this.profilePic = profilePic;
		this.profilePicLoader =profilePicLoader;
		this.context =context;
	}
	@Override
	protected String doInBackground(OAuthConsumer... params) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo");
		Log.i(TAG, "Requesting URL : " + Constants.API_REQUEST);

		try {
			params[0].sign(request);

			HttpResponse response = httpclient.execute(request);
			Log.i(TAG, "Statusline : " + response.getStatusLine());
			InputStream data = response.getEntity().getContent();
			bufferedReader = new BufferedReader(new InputStreamReader(data));

		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String responeLine;
		StringBuilder responseBuilder = new StringBuilder();
		try {
			while ((responeLine = bufferedReader.readLine()) != null) {
				responseBuilder.append(responeLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO HANDLE RESPONSE DATA HERE .

		return responseBuilder.toString();
	}

	protected void onPostExecute(String responseJson) {
		Log.v(TAG, "Finished async " + responseJson);
		Log.v(TAG, "Response : " + responseJson);
		Log.d(TAG, "Name = " + Utility.getJsonField(responseJson, "name"));
		Log.d(TAG, "Birthday = " + Utility.getJsonField(responseJson, "birthday"));
		
		profileId.setText("Profile Id: " + Utility.getJsonField(responseJson, "id"));
		profileName.setText("User name: " + Utility.getJsonField(responseJson, "name"));
		profileGender.setText("Gender: " +Utility.getJsonField(responseJson, "gender"));
		profileBirthday.setText("Birthday: "+Utility.getJsonField(responseJson, "birthday"));
		ImageLoader imageLoader = new ImageLoader(context);
		imageLoader.DisplayImageWithProgress(Utility.getJsonField(responseJson, "picture"), profilePic, profilePicLoader, false);
		

	}

}
