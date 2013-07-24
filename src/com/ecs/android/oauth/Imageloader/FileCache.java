package com.ecs.android.oauth.Imageloader;

import java.io.File;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.content.Context;

public class FileCache {
	private File cacheDir;

	@SuppressLint("NewApi")
	public FileCache(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), ".Revelation");

		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {

		@SuppressWarnings("deprecation")
		String suburl;
		if (url.length() > 180)
			suburl = url.substring(url.length() - 180, url.length());
		else
			suburl = url;
		String filename = URLEncoder.encode(suburl);
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
}
