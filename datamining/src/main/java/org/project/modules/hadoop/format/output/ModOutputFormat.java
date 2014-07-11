package org.project.modules.hadoop.format.output;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ModOutputFormat extends TextOutputFormat<LongWritable, Text> {

	@Override
	public RecordWriter<LongWritable, Text> getRecordWriter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {
		return super.getRecordWriter(arg0);
	}
	
}
class NewRecordWriter extends RecordWriter<LongWritable, Text> {
	
	@Override
	public void close(TaskAttemptContext arg0) throws IOException,
	InterruptedException {
		
	}
	
	@Override
	public void write(LongWritable arg0, Text arg1) throws IOException,
	InterruptedException {
		
	}
	
}

