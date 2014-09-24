package org.project.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TextUtils {

	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static ScriptEngine engine = manager.getEngineByName("JavaScript");

	public static String unescape(String value) {
		String ret = "";
		if (value == null) {
			return null;
		}
		try {
			ret = engine.eval("unescape(\"" + value + "\")").toString();
		} catch (ScriptException e) {
			ret = value;
		}
		return ret;
	}
}
