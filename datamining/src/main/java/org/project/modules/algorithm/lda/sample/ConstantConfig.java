package org.project.modules.algorithm.lda.sample;

import java.net.URISyntaxException;

public class ConstantConfig {
	
	public static String LDAPARAMETERFILE = null;
	
	static {
		try {
			LDAPARAMETERFILE = PathConfig.class.getClassLoader().getResource("trainset/lda/LdaParameter/LdaParameters.txt").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
