package org.project.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum SimpleDateFormatEnum {
	timeFormat("yyyyMMdd HH:mm:ss"), 
	dateFormat("yyyyMMdd"), 
	monthFormat("yyyyMM");

	private String formatStr;
	private static Map<String, ThreadLocal<java.text.SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<java.text.SimpleDateFormat>>();

	private SimpleDateFormatEnum(String s) {
		this.formatStr = s;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public java.text.SimpleDateFormat get() {
		ThreadLocal<java.text.SimpleDateFormat> res = (ThreadLocal) 
				sdfMap.get(this.formatStr);

		if (null == res) {
			synchronized (sdfMap) {
				if (null == res) {
					res = new ThreadLocal() {
						protected java.text.SimpleDateFormat initialValue() {
							return new java.text.SimpleDateFormat(
									SimpleDateFormatEnum.this.formatStr);
						}
					};
					sdfMap.put(this.formatStr, res);
				}
			}
		}
		return (java.text.SimpleDateFormat) res.get();
	}
}
