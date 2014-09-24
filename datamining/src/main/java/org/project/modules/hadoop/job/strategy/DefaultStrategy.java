package org.project.modules.hadoop.job.strategy;

import org.apache.hadoop.util.ToolRunner;
import org.project.modules.hadoop.job.AbstractJob;

public class DefaultStrategy implements JobStrategy {

	static {
		StrategyFactory.define(DefaultStrategy.class, new DefaultStrategy());
	}

	public int rubJob(AbstractJob job) {
		long begin = System.currentTimeMillis();
		try {
			job.printClassName();
			if (!job.executeThisDate(job.params.getDate(), job)) {
				return 0;
			}
			if (!job.init(job.params.getDate())) {
				return 1;
			}
			job.ParamsOverride();
			if (job.params.isDebug()) {
				AbstractJob.LOG.info("debug model");
				job.debugModel();
			}
			if (!job.outputCheckBeforeRun()) {
				return 1;
			}
			String[] params = job.params.getArgs() == null ? null : job.params.getArgsArray();
			if (!job.inputCheck()) {
				return 1;
			}
			try {
				if (1 != ToolRunner.run(job.configuration, job, params)) {
					job.outputClean(null);
					return 1;
				}
			} catch (Exception e) {
				AbstractJob.LOG.error("run job error: ", e);
				job.outputClean("exception");
				return 1;
			}
			job.extraJob(job.params.getArgsArray());
			return 0;
		} finally {
			job.printTimeStamp(begin, System.currentTimeMillis());
		}
	}
	
}
