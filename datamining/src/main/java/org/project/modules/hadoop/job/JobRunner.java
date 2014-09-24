package org.project.modules.hadoop.job;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JobRunner {

	public static final Logger LOG = Logger.getLogger(JobRunner.class);

	public static void main(String[] args) {
		int status = submit(args);
		System.exit(status);
	}

	private static int submit(String[] args) {
		JobParams params = new JobParams(args);
		LOG.info(params);
		try {
			if (StringUtils.isBlank(params.getClazz())) {
				LOG.error("class is null");
				return 1;
			}
			Class<?> clazz = Class.forName(params.getClazz());
			Object obj = clazz.newInstance();
			if ((obj instanceof AbstractJob)) {
				AbstractJob job = (AbstractJob) obj;
				job.setParams(params);
				return job.run();
			}
			LOG.error(params.getClazz() + " is not a AbstractJob!");
			return 1;
		} catch (Exception e) {
			LOG.error("Cound not run AbstractJob", e);
		}
		return 1;
	}
	
}
