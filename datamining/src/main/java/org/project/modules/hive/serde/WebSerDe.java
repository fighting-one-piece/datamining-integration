package org.project.modules.hive.serde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.project.utils.JSONUtils;
import org.project.utils.TextUtils;

@SuppressWarnings("deprecation")
public class WebSerDe implements Deserializer {
	
	private static List<String> structFieldNames = new ArrayList<String>();
	private static List<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>();

	static {
		structFieldNames.add("ip");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("event");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("uuid");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("url");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("urlDomain");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("ref");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("refDomain");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("pver");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("cdata");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("pagescroll");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("sid");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("ptype");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("entry");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("pgr");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("r");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("utime");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("uctime");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("ultime");

		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("email");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("browserInfo");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
	}

	public Object deserialize(Writable writable) throws SerDeException {
		List<Object> result = new ArrayList<Object>();
		try {
			Text rowText = (Text) writable;
			String[] items = rowText.toString().split(" ");
			if ((items != null) && (items.length > 0)) {
				result.add(items[0]);
			}

			String rowStr = "";
			StringBuilder browserInfoSB = new StringBuilder();
			boolean browserInfoBegin = false;
			for (String item : items) {
				if (item.startsWith("/stat/?")) {
					rowStr = item.replaceAll("/stat/\\?", "");
				}

				if (item.startsWith("HTTP")) {
					browserInfoBegin = true;
				}
				if ((browserInfoBegin) && (!item.startsWith("\"http:"))
						&& (!item.startsWith("http:"))) {
					browserInfoSB.append(item).append(" ");
				}
			}

			String[] splits = rowStr.split("&");
			Map<String, String> map = new HashMap<String, String>();
			for (String s : splits) {
				try {
					int first = s.indexOf("=");
					if (first != -1) {
						map.put(s.substring(0, first),
								s.substring(first + 1, s.length()));
					}
				} catch (Exception localException) {
				}
			}
			String event = stringProcess((String) map.get("event"));
			result.add(event);

			String uuid = stringProcess((String) map.get("uuid"));
			result.add(uuid);

			String url = stringProcess((String) map.get("url"));
			result.add(url);

			String urlDomain = urlGetDomain(url);
			result.add(urlDomain);

			String ref = stringProcess((String) map.get("ref"));
			result.add(ref);

			String refDomain = urlGetDomain(ref);
			result.add(refDomain);

			String pver = stringProcess((String) map.get("pver"));
			result.add(pver);

			String cdata = stringProcess((String) map.get("cdata"));
			result.add(cdata);

			String pagescroll = stringProcess((String) map.get("pagescroll"));
			result.add(pagescroll);

			String sid = stringProcess((String) map.get("sid"));
			result.add(sid);

			String ptype = stringProcess((String) map.get("ptype"));
			result.add(ptype);

			String entry = stringProcess((String) map.get("entry"));
			result.add(entry);

			String pgr = stringProcess((String) map.get("pgr"));
			result.add(pgr);

			String r = stringProcess((String) map.get("r"));
			result.add(r);

			String utime = stringProcess((String) map.get("utime"));
			result.add(utime);

			String uctime = stringProcess((String) map.get("uctime"));
			result.add(uctime);

			String ultime = stringProcess((String) map.get("ultime"));
			result.add(ultime);

			Map<String, String> cvarMap = new HashMap<String, String>();
			try {
				String cvar = stringProcess((String) map.get("cvar"));
				if (cvar.length() > 2) {
					Map<String, String> json2Map = JSONUtils.json2Map(cvar);
					if (null != json2Map) {
						cvarMap.putAll(json2Map);
					}
				}
			} catch (Exception e) {
			}
			String email = stringProcess((String) cvarMap.get("email"));
			result.add(email);

			result.add(browserInfoSB.toString().trim());
		} catch (Exception localException1) {
		}
		return result;
	}

	private String stringProcess(String s) {
		String unescapeStr = TextUtils.unescape(s);
		return StringUtils.isBlank(unescapeStr) ? "" : unescapeStr.trim();
	}

	private String urlGetDomain(String url) {
		String temp = url.replace("http://", "");
		int first = temp.indexOf("/");
		if (first != -1) {
			return temp.substring(0, first);
		}
		return temp;
	}

	public ObjectInspector getObjectInspector() throws SerDeException {
		return ObjectInspectorFactory.getStandardStructObjectInspector(
				structFieldNames, structFieldObjectInspectors);
	}

	public SerDeStats getSerDeStats() {
		return null;
	}

	public void initialize(Configuration job, Properties arg1)
			throws SerDeException {
	}
}
