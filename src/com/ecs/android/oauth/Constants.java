package com.ecs.android.oauth;

public class Constants {

	public static final String CONSUMER_KEY = "anonymous";
	public static final String CONSUMER_SECRET = "anonymous";
	public static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	//public static final String SCOPE = "	https://www.googleapis.com/auth/userinfo.email";

	public static final String REQUEST_URL = "https://www.google.com/accounts/OAuthGetRequestToken";
	public static final String ACCESS_URL = "https://www.google.com/accounts/OAuthGetAccessToken";
	public static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken";

	public static final String API_REQUEST = "http://www.blogger.com/feeds/default/blogs";

	public static final String ENCODING = "UTF-8";

	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}
