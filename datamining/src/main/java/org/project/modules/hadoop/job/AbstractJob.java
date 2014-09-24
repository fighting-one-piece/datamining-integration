package org.project.modules.hadoop.job;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.project.common.enums.SimpleDateFormatEnum;
import org.project.modules.hadoop.job.annotation.RunFrequency;
import org.project.modules.hadoop.job.strategy.JobStrategy;
import org.project.modules.hadoop.job.strategy.DefaultStrategy;
import org.project.modules.hadoop.job.strategy.StrategyFactory;
import org.project.utils.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

@RunFrequency
public abstract class AbstractJob extends Configured implements Tool {

	public static final Logger LOG = Logger.getLogger(AbstractJob.class);

	public static FileSystem HDFS = null;

	public Configuration configuration = new Configuration();

	public JobParams params = null;

	public JobStrategy strategy = StrategyFactory.get(DefaultStrategy.class);

	public List<String> inputList = new ArrayList<String>();

	public List<String> outputList = new ArrayList<String>();

	public int run() {
		Class<?> strategyClass = null;
		String strategyClassStr = this.params.getStrategyClass();
		if (StringUtils.isNotBlank(strategyClassStr)) {
			try {
				strategyClass = Class.forName(strategyClassStr);
			} catch (ClassNotFoundException e) {
				LOG.error(new StringBuilder()
						.append("strategy class error, + ")
						.append(strategyClassStr).toString());
				return 1;
			}
		} else {
			strategyClass = DefaultStrategy.class;
		}

		this.strategy = StrategyFactory.get(strategyClass);

		if ((null == this.params) || (null == this.strategy)) {
			LOG.error(new StringBuilder()
					.append(null == this.params ? "params " : "")
					.append(null == this.strategy ? "strategy " : "")
					.append(" null").toString());
			return 1;
		}
		return this.strategy.rubJob(this);
	}

	public FileSystem getHDFS() {
		if (null == HDFS) {
			try {
				HDFS = FileSystem.get(this.configuration);
			} catch (IOException e) {
				LOG.error("get hdfs error:", e);
			}
		}
		return HDFS;
	}

	public String getUserDefineParam(String key, String defValue) {
		return this.params.getUserDefineParam(key, defValue);
	}

	public void printClassName() {
		LOG.info(getClass().getSimpleName());
	}

	public abstract boolean init(String paramString);

	public void debugModel() {
		for (int i = 0; i < this.outputList.size(); i++) {
			this.outputList.set(i,
							new StringBuilder().append("/debug")
									.append((String) this.outputList.get(i))
									.toString());
		}
	}

	public void ParamsOverride() {
		List<String> inList = this.params.getInputList();
		if (!inList.isEmpty()) {
			this.inputList = inList;
		}

		List<String> outList = this.params.getOutputList();
		if (!outList.isEmpty()) {
			this.outputList = outList;
		}
	}

	public boolean inputCheck() {
		boolean res = true;
		List<String> nonexistentList = new ArrayList<String>();
		try {
			for (String p : this.inputList) {
				if (!getHDFS().exists(new Path(p))) {
					res = false;
					nonexistentList.add(p);
				}
			}
		} catch (Exception e) {
			LOG.error(new StringBuilder().append("input check error:")
					.append(e).toString());
			res = false;
		}

		if (!res) {
			LOG.info(new StringBuilder().append("not exist input file: ")
					.append(nonexistentList).toString());
		}

		return res;
	}

	public boolean outputCheckBeforeRun() {
		boolean res = true;
		List<String> existentList = new ArrayList<String>();
		try {
			for (String p : this.outputList) {
				if (getHDFS().exists(new Path(p))) {
					existentList.add(p);
				}
			}
		} catch (Exception e) {
			LOG.error("check output error:", e);
		}
		if (this.params.isForce()) {
			Iterator<String> it = existentList.iterator();
			while (it.hasNext()) {
				String toDelFile = (String) it.next();
				try {
					getHDFS().delete(new Path(toDelFile), true);
					it.remove();
				} catch (IOException e) {
					LOG.warn("del toDelFile error. ", e);
				}
			}
		}
		if (existentList.size() > 0) {
			LOG.info(new StringBuilder().append("exist output file: ")
					.append(existentList).toString());
			res = false;
		}
		return res;
	}

	public boolean executeThisDate(String date, AbstractJob job) {
		RunFrequency annotation = (RunFrequency) job.getClass().getAnnotation(
				RunFrequency.class);
		if (null == annotation) {
			return true;
		}
		RunFrequency.FrequencyType value = annotation.value();
		if (RunFrequency.FrequencyType.daily == value)
			return true;
		if (RunFrequency.FrequencyType.monthly == value)
			return DateUtils.runMonthlyJob(date);
		if (RunFrequency.FrequencyType.quarterly == value) {
			return DateUtils.runQuarterlyJob(date);
		}
		return false;
	}

	public void outputClean(String reason) {
		LOG.info(new StringBuilder().append("outputClean").append(StringUtils.isBlank(reason) ? "" : 
			new StringBuilder().append(" because: ").append(reason).toString()).toString());
		try {
			for (String p : this.outputList) {
				getHDFS().delete(new Path(p), true);
			}
		} catch (Exception e) {
			LOG.error("clean error", e);
		}
	}

	public void extraJob(String[] args) {
	}

	public void printTimeStamp(long begin, long end) {
		LOG.info(new StringBuilder().append("Started at: ")
				.append(DateUtils.getTime(begin,SimpleDateFormatEnum.timeFormat.get()))
				.append("\tFinished at: ")
				.append(DateUtils.getTime(end,SimpleDateFormatEnum.timeFormat.get()))
				.append("\tFinished in: ").append((end - begin) / 1000L)
				.append("sec").toString());
	}

	public JobParams getParams() {
		return this.params;
	}

	public void setParams(JobParams params) {
		this.params = params;
	}

	public JobStrategy getStrategy() {
		return this.strategy;
	}

	public void setStrategy(JobStrategy strategy) {
		this.strategy = strategy;
	}

}
