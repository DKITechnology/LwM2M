package com.dkitec.lwm2m.common.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesUtil {
	private static HashMap<String, Properties> propses;

	public static String get(String prop, String key) {
		if (load(prop) == true) {
			return ((Properties) propses.get(prop)).getProperty(key);
		}
		return "";
	}

	private static boolean load(String prop) {
		if (propses == null) {
			init();
		}

		if (propses.containsKey(prop))
			return true;
		try {
			StringBuilder path = new StringBuilder();
			path.append("config/").append(prop).append(".properties");

			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString());
			Properties props = new Properties();
			props.load(is);

			propses.put(prop, props);

			is.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void init() {
		propses = new HashMap();
	}
}