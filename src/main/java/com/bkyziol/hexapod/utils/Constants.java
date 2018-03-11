package com.bkyziol.hexapod.utils;

import java.io.IOException;
import java.util.Properties;

import com.bugsnag.Bugsnag;

public final class Constants {
	public static final String CLIENT_ENDPOINT;
	public static final String CERTIFICATE_FILE;
	public static final String PRIVATE_KEY_FILE;
	public static final Bugsnag BUGSNAG;
	public static final int FRAME_WIDTH;
	public static final int FRAME_HEIGTH;
	public static final String OPENCV_LIB_FILE;
	public static final String HAARCASCADES_PATH;

	static {
		final Properties appProperties = new Properties();
		try {
			appProperties.load(Constants.class.getResourceAsStream("/META-INF/hexapod.properties"));
			CLIENT_ENDPOINT = appProperties.getProperty("client.endpoint");
			CERTIFICATE_FILE = appProperties.getProperty("certificate.file");
			PRIVATE_KEY_FILE = appProperties.getProperty("privateKey.file");
			BUGSNAG = new Bugsnag(appProperties.getProperty("bugsnag.id"));
			FRAME_WIDTH = Integer.valueOf(appProperties.getProperty("frame.width"));
			FRAME_HEIGTH = Integer.valueOf(appProperties.getProperty("frame.height"));
			OPENCV_LIB_FILE = appProperties.getProperty("libopencv.file");
			HAARCASCADES_PATH = appProperties.getProperty("haarcascades.path");
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Constants() {
		super();
	}

}
