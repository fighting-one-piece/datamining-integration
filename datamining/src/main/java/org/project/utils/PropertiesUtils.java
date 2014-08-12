package org.project.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

public class PropertiesUtils {
	
	public static Properties newInstance(String file) {
		InputStream inStream = null;
		try {
			inStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(file);
			if (null == inStream) {
				inStream = new FileInputStream(new File(file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return newInstance(inStream);
	}
	
	public static Properties newInstance(InputStream inStream) {
		Properties properties = new Properties();
		try {
			properties.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inStream) inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
	
	public static Collection<Object> obtainValues(String file) {
		return newInstance(file).values();
	}

	public static String obtainValue(String file, String key) {
		return newInstance(file).getProperty(key);
	}

}
