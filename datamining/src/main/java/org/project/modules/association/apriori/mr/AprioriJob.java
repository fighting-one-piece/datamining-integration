package org.project.modules.association.apriori.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;
import org.project.utils.HDFSUtils;
import org.project.utils.IdentityUtils;

public class AprioriJob {
	
	private Configuration conf = null;
	
	public String frequency_1_itemset_gen(String input) {
		String output = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
		String[] inputArgs = new String[]{input, output};
		Frequency1ItemSetMR.main(inputArgs);
		return output;
	}
	
	public String frequency_k_itemset_gen(String input) {
		
		String output = HDFSUtils.HDFS_TEMP_INPUT_URL + IdentityUtils.generateUUID();
		return output;
	}
	
	public void calculateSupport() {
		
	}
	
	public void run(String[] args) {
		if (null == conf) conf = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
					conf, args).getRemainingArgs();
			if (inputArgs.length != 3) {
				System.out.println("error");
				System.out.println("1. input path.");
				System.out.println("2. output path.");
				System.out.println("3. min support.");
				System.exit(2);
			}
			String fre1_output = frequency_1_itemset_gen(inputArgs[0]);
			frequency_k_itemset_gen(fre1_output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		AprioriJob job = new AprioriJob();
		long startTime = System.currentTimeMillis();
		job.run(args);
		long endTime = System.currentTimeMillis();
		System.out.println("spend time: " + (endTime - startTime));
	}
}
