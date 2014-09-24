package org.project.modules.hadoop.job;

import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapred.JobPriority;

public class JobHelper {

	public static JobConf getJobConf(Configuration conf, Class<?> exampleClass,
			String jobName) {
		JobConf jobConf = new JobConf(conf, exampleClass);
		jobConf.setJobName(jobName);
		jobConf.setJobPriority(JobPriority.LOW);
		jobConf.setQueueName("");
		return jobConf;
	}

	public static Job getJob(Class<?> exampleClass, String jobName)
			throws IOException {
		Job job = Job.getInstance();
		job.setJobName(jobName);
		job.setJarByClass(exampleClass);
		job.getConfiguration().set("mapred.job.queue.name", "");
		return job;
	}
	
	public static Job getJob(Configuration conf, Class<?> exampleClass, String jobName)
			throws IOException {
		Job job = Job.getInstance(conf, jobName);
		job.setJarByClass(exampleClass);
		return job;
	}

	public static void writeString(DataOutput out, String value)
			throws IOException {
		if (null == value) {
			out.writeUTF("");
		} else {
			out.writeUTF(value);
		}
	}
}
