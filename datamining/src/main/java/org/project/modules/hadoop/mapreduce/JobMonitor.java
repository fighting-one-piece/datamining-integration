package org.project.modules.hadoop.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.YARNRunner;
import org.apache.hadoop.mapreduce.JobStatus;

public class JobMonitor {

	public static void main(String[] args) {
		Configuration conf = new Configuration();
		conf.set("yarn.resourcemanager.address", "centos.host1:8032");
		YARNRunner yarnRunner = new YARNRunner(conf);
		try {
			JobStatus[] jobStatuses = yarnRunner.getAllJobs();
			for (JobStatus jobStatus : jobStatuses) {
				System.out.println(jobStatus.getJobName());
				System.out.println(jobStatus.getFinishTime());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
