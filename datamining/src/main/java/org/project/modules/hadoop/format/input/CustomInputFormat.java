package org.project.modules.hadoop.format.input;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.LineRecordReader;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;

public class CustomInputFormat extends TextInputFormat {
	public RecordReader<LongWritable, Text> getRecordReader(
			InputSplit genericSplit, JobConf job, Reporter reporter)
			throws IOException {
		reporter.setStatus(genericSplit.toString());
		MyDemoRecordReader reader = new MyDemoRecordReader(
				new LineRecordReader(job, (FileSplit) genericSplit));
		return reader;
	}

	public static class MyDemoRecordReader implements
			RecordReader<LongWritable, Text> {
		LineRecordReader reader;
		Text text;

		public MyDemoRecordReader(LineRecordReader reader) {
			this.reader = reader;
			this.text = reader.createValue();
		}

		public void close() throws IOException {
			this.reader.close();
		}

		public LongWritable createKey() {
			return this.reader.createKey();
		}

		public Text createValue() {
			return new Text();
		}

		public long getPos() throws IOException {
			return this.reader.getPos();
		}

		public float getProgress() throws IOException {
			return this.reader.getProgress();
		}

		public boolean next(LongWritable key, Text value) throws IOException {
			if (this.reader.next(key, this.text)) {
				String strReplace = this.text.toString().toLowerCase()
						.replaceAll("::", ":");
				Text txtReplace = new Text();
				txtReplace.set(strReplace);
				value.set(txtReplace.getBytes(), 0, txtReplace.getLength());
				return true;
			}

			return false;
		}
	}
}
