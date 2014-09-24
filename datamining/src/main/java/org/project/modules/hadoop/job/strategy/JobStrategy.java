package org.project.modules.hadoop.job.strategy;

import org.project.modules.hadoop.job.AbstractJob;

public abstract interface JobStrategy {
	
	public abstract int rubJob(AbstractJob job);
}
