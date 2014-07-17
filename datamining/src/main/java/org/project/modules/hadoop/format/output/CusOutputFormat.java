package org.project.modules.hadoop.format.output;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.ReflectionUtils;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CusOutputFormat extends
		HiveIgnoreKeyTextOutputFormat<WritableComparable, Writable> {
	public RecordWriter<WritableComparable, Writable> getRecordWriter(
			FileSystem ignored, JobConf job, String name, Progressable progress)
			throws IOException {
		System.out
				.println("mydemo---------------------------------------------------------------");
		return getRecordWriter(ignored, job, name, progress, "");
	}

	public RecordWriter getRecordWriter(FileSystem ignored, JobConf job,
			String name, Progressable progress, String s) throws IOException {
		boolean isCompressed = getCompressOutput(job);
		String keyValueSeparator = job.get("mapred.textoutputformat.separator",
				"\t");
		if (!isCompressed) {
			Path file = FileOutputFormat.getTaskOutputPath(job, name);
			FileSystem fs = file.getFileSystem(job);
			FSDataOutputStream fileOut = fs.create(file, progress);
			return new MyDemoRecordWriter(fileOut, keyValueSeparator);
		}
		Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(
				job, GzipCodec.class);

		CompressionCodec codec = (CompressionCodec) ReflectionUtils
				.newInstance(codecClass, job);

		Path file = FileOutputFormat.getTaskOutputPath(job,
				name + codec.getDefaultExtension());
		FileSystem fs = file.getFileSystem(job);
		FSDataOutputStream fileOut = fs.create(file, progress);
		return new MyDemoRecordWriter(new DataOutputStream(
				codec.createOutputStream(fileOut)), keyValueSeparator);
	}

	public static class MyDemoRecordWriter<K, V> implements RecordWriter<K, V> {
		@SuppressWarnings("unused")
		private static final String utf8 = "UTF-8";
		private static final byte[] newline;
		protected DataOutputStream out;
		private final byte[] keyValueSeparator;

		static {
			try {
				newline = "\n".getBytes("UTF-8");
			} catch (UnsupportedEncodingException uee) {
				throw new IllegalArgumentException("can't find UTF-8 encoding");
			}
		}

		public MyDemoRecordWriter(DataOutputStream out, String keyValueSeparator) {
			this.out = out;
			try {
				this.keyValueSeparator = keyValueSeparator.getBytes("UTF-8");
			} catch (UnsupportedEncodingException uee) {
				throw new IllegalArgumentException("can't find UTF-8 encoding");
			}
		}

		public MyDemoRecordWriter(DataOutputStream out) {
			this(out, "\t");
		}

		private void writeObject(Object o) throws IOException {
			if ((o instanceof Text)) {
				Text to = (Text) o;
				String strReplace = to.toString().replaceAll(":", "::");
				to.set(strReplace);
				this.out.write(to.getBytes(), 0, to.getLength());
			} else {
				this.out.write(o.toString().getBytes("UTF-8"));
			}
		}

		public synchronized void write(K key, V value) throws IOException {
			System.out
					.println("test---------------------------------------------------------------");
			boolean nullKey = (key == null) || ((key instanceof NullWritable));
			boolean nullValue = (value == null)
					|| ((value instanceof NullWritable));
			if ((nullKey) && (nullValue)) {
				return;
			}
			if (!nullKey) {
				System.out
						.println("test1---------------------------------------------------------------");
				writeObject(key);
			}
			if ((!nullKey) && (!nullValue)) {
				System.out
						.println("test2---------------------------------------------------------------");
				this.out.write(this.keyValueSeparator);
			}
			if (!nullValue) {
				System.out
						.println("test3---------------------------------------------------------------");
				writeObject(value);
			}

			System.out
					.println("test4---------------------------------------------------------------");
			this.out.write(newline);
		}

		public synchronized void close(Reporter reporter) throws IOException {
			this.out.close();
		}
	}
}