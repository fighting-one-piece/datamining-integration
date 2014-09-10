package org.project.modules.algorithm.lda.sample;

import java.net.URISyntaxException;

public class PathConfig {

	public static String ldaDocsPath = null;
	
	public static String LdaResultsPath = null;
	
	static {
		try {
			ldaDocsPath = PathConfig.class.getClassLoader().getResource("trainset/lda/LdaOriginalDocs/").toURI().getPath();
			LdaResultsPath = PathConfig.class.getClassLoader().getResource("trainset/lda/LdaResults/").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(ldaDocsPath);
		System.out.println(LdaResultsPath);
	}

}
