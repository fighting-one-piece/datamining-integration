package org.project.modules.hadoop.format.output;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class COutputFormat extends FileOutputFormat<Text, Text> {

	@Override
	public RecordWriter<Text, Text> getRecordWriter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		 Configuration conf = context.getConfiguration();  
		 Path file = getDefaultWorkFile(context, "");  
		 FileSystem fs = file.getFileSystem(conf);  
		 FSDataOutputStream fsDataOutputStream = fs.create(file, false);  
		return new CRecordWriter(fsDataOutputStream);
	}

}

class CRecordWriter extends RecordWriter<Text, Text> {
	
	private DataOutputStream dataOutputStream = null;
	
	public CRecordWriter(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

	@Override
	public void write(Text key, Text value) throws IOException,
			InterruptedException {
		dataOutputStream.write(key.getBytes());
		dataOutputStream.write(value.getBytes());
	}
	
	@Override
	public void close(TaskAttemptContext context) throws IOException,
			InterruptedException {
		
	}

}