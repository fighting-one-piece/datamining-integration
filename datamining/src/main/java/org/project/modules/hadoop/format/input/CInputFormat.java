package org.project.modules.hadoop.format.input;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class CInputFormat extends FileInputFormat<Text, Text> {
	
	@Override
	protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
		return super.computeSplitSize(blockSize, minSize, maxSize);
	}
	
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		return super.isSplitable(context, filename);
	}

	@Override
	public List<InputSplit> getSplits(JobContext arg0) throws IOException {
		return super.getSplits(arg0);
	}
	
	@Override
	public RecordReader<Text, Text> createRecordReader(
			InputSplit inputSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		return null;
	}

}

class CRecordReader extends RecordReader<Text, Text> {

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		
	}
	
	@Override
	public Text getCurrentKey() throws IOException,
			InterruptedException {
		return null;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return null;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		return false;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return 0;
	}
	
	@Override
	public void close() throws IOException {
		
	}

	
}