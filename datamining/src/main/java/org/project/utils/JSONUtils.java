package org.project.utils;

import java.lang.reflect.Field;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

public class JSONUtils {

	public static String object2json(Object object) {
		StringBuffer sb = new StringBuffer();
		if (null == object)
			sb.append("\"\"");
		else
			sb.append(object2json(object, null));
		return sb.toString();
	}
	
	public static String object2json(Object object, final String[] properties){
		if (null == properties || properties.length == 0) {
			return Collection.class.isAssignableFrom(object.getClass()) ?
						JSONArray.fromObject(object).toString() :
							JSONObject.fromObject(object).toString();
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);  
		jsonConfig.setIgnoreDefaultExcludes(false);  
		jsonConfig.setAllowNonStringKeys(true); 
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object arg0, String arg1, Object arg2) {
				for(String property : properties){
					if(property.equals(arg1)){
						return true;
					}
				}
				return false;
			}
		});
		return Collection.class.isAssignableFrom(object.getClass()) ?
					JSONArray.fromObject(object, jsonConfig).toString() :
						JSONObject.fromObject(object, jsonConfig).toString();
	}

	public static JSONObject json2Object(String jsonData) {
		return JSONObject.fromObject(jsonData);
	}
	
	public static Object json2Object(String jsonData, Class<?> clazz) {
		JSONObject jsonObject = JSONObject.fromObject(jsonData);
		Object object = null;
		try {
			object = clazz.newInstance();
			for (Field field : clazz.getDeclaredFields()) {
				String name = field.getName();
				if (!jsonObject.containsKey(name)) {
					continue;
				}
				field.setAccessible(true);
				Object value = jsonObject.get(name);
				field.set(object, value);
				field.setAccessible(false);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}
	
}
