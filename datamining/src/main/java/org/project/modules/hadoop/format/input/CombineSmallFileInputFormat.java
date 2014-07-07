package org.project.modules.hadoop.format.input;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class CombineSmallFileInputFormat extends FileInputFormat<NullWritable, BytesWritable> {

	@Override
	public RecordReader<NullWritable, BytesWritable> createRecordReader(
			InputSplit inputSplit, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new CombineSmallFileRecordReader();
	}
	
}

class CombineSmallFileRecordReader extends RecordReader<NullWritable, BytesWritable> {

	private FileSplit fileSplit = null;
	private JobContext jobContext = null;
	private NullWritable currentKey = null;
	private BytesWritable currentValue = null;
	private boolean isFinishConvert = false;
	
	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		this.fileSplit = (FileSplit) inputSplit;
		this.jobContext = context;
		this.currentKey = NullWritable.get();
		context.getConfiguration().set("map.input.file.name", 
				fileSplit.getPath().getName());
	}
	
	@Override
	public NullWritable getCurrentKey() throws IOException,
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
			int len = (int) fileSplit.getLength();
			byte[] content = new byte[len];
			Path path = fileSplit.getPath();
			FileSystem fs = path.getFileSystem(jobContext.getConfiguration());
			FSDataInputStream fsDataInputStream = null;
			try {
				fsDataInputStream = fs.open(path);
				IOUtils.readFully(fsDataInputStream, content, 0, len);
				currentValue.set(content, 0, len);
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