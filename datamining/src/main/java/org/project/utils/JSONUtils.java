package org.project.utils;

import java.lang.reflect.Field;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public class JSONUtils {
	
	private static final ObjectMapper defaultObjectMapper;
	private static final ObjectMapper numAsStringObjectMapper = new ObjectMapper();
	
	static {
//		numAsStringObjectMapper.configure(
//				JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
		defaultObjectMapper = new ObjectMapper().configure(
				JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		defaultObjectMapper.getDeserializationConfig().setDateFormat(
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		defaultObjectMapper
				.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
				.configure(
						DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
	}

	public static String toFilterNumberString(Object object) {
		return toString(object, numAsStringObjectMapper);
	}

	public static String toString(Object object, ObjectMapper mapper) {
		String json = "";
		try {
			json = mapper.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String toJson(Object object) {
		return toString(object, defaultObjectMapper);
	}

	public static long getLong(Object o, long defaltValue) {
		if ((o instanceof String))
			return NumberUtils.toLong((String) o, defaltValue);
		if ((o instanceof Number)) {
			return ((Number) o).longValue();
		}
		return defaltValue;
	}

	public static String getStr(String remarks) {
		if ((remarks == null) || (remarks.trim().equalsIgnoreCase("null"))) {
			return null;
		}
		return remarks;
	}

	public static <T> T json2Bean(String json, Class<T> clazz) {
		try {
			return defaultObjectMapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> json2Map(String json) {
		try {
			return (Map) defaultObjectMapper.readValue(json,
					TypeFactory.mapType(Map.class, String.class, String.class));
		} catch (Exception e) {
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> json2ObjMap(String json) {
		try {
			return (Map) defaultObjectMapper.readValue(json,
					TypeFactory.mapType(Map.class, String.class, Object.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, String> json2StrMap(String json) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			Map<String, Object> tempMap = json2ObjMap(json);
			if (null != tempMap) {
				for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
					result.put(entry.getKey(), entry.getValue().toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> json2List(String json, Class<T> clazz) {
		try {
			return (List) defaultObjectMapper.readValue(json,
					TypeFactory.collectionType(List.class, clazz));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map<String, String>> json2List(String json) {
		try {
			return (List) defaultObjectMapper.readValue(json, TypeFactory
					.collectionType(List.class, TypeFactory.mapType(Map.class,
							String.class, String.class)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] json2Array(String json, Class<T> clazz) {
		try {
			return (T[]) defaultObjectMapper.readValue(json,
					TypeFactory.arrayType(clazz));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, String>[] json2Array(String json) {
		try {
			return (Map[]) defaultObjectMapper.readValue(json, TypeFactory
					.arrayType(TypeFactory.mapType(Map.class, String.class,
							String.class)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

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
