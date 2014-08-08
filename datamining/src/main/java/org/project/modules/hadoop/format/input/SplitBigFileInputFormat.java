package org.project.modules.hadoop.format.input;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class SplitBigFileInputFormat extends FileInputFormat<LongWritable, BytesWritable> {
	
	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		return super.getSplits(job);
	}

	@Override
	public RecordReader<LongWritable, BytesWritable> createRecordReader(
			InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new SplitBigFileRecordReader();
	}

}

class SplitBigFileRecordReader extends RecordReader<LongWritable, BytesWritable> {

	private FileSplit fileSplit = null;
	private JobContext jobContext = null;
	private LongWritable currentKey = null;
	private BytesWritable currentValue = null;
	private boolean isFinishConvert = false;
	private int start = 0;  
    private int end = 0;  
    private int fileLen = 0;
    private int len = 100;
	
	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		this.fileSplit = (FileSplit) inputSplit;
		this.jobContext = context;
		this.fileLen = (int) fileSplit.getLength();
		this.end = fileLen;
	}
	
	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {
		return currentKey;
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException,
			InterruptedException {
		return currentValue;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (isFinishConvert) {
			return false;
		} else {
			currentValue = new BytesWritable();
			Path path = fileSplit.getPath();
			FileSystem fs = path.getFileSystem(jobContext.getConfiguration());
			FSDataInputStream fsDataInputStream = null;
			try {
				fsDataInputStream = fs.open(path);
				if ((start + len) > end) {
					len = end;
				}
				byte[] content = new byte[len];
				IOUtils.readFully(fsDataInputStream, content, start, len);				
				currentValue.set(content, 0, len);
				start += len;
				if (start >= end) {
					start = end;
				}
			} finally {
				if (null != fsDataInputStream) {
					IOUtils.closeStream(fsDataInputStream);
				}
			}
			isFinishConvert = true;
			return true;
		}
	}
	
	@Override
	public float getProgress() throws IOException, InterruptedException {
		float progress = 0;
		if (isFinishConvert) {
			progress = 1;
		}
		return progress;
	}
	
	@Override
	public void close() throws IOException {
		
	}
	
}