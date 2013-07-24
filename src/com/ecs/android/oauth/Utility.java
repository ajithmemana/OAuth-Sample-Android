package com.ecs.android.oauth;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.myjson.Gson;
import com.google.myjson.GsonBuilder;

public class Utility {
	public static String getJsonField(String jsonData, String fieldName) {
		JSONObject jsonObject = new JSONObject();
		String fieldData = "InvalidUrl";
		try {

			jsonObject = new JSONObject(jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			fieldData = jsonObject.getString(fieldName);
		} catch (JSONException e) {
			fieldData = null;
			e.printStackTrace();
		}
		return fieldData;

	}

}
