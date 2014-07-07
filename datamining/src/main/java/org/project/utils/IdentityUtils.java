package org.project.utils;

import java.util.UUID;

public class IdentityUtils {
	
	private IdentityUtils() {
		
	}

	public static String generateUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
