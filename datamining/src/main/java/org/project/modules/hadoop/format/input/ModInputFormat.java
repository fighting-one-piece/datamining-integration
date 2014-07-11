package org.project.modules.hadoop.format.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.mapred.LineRecordReader.LineReader;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

@SuppressWarnings("deprecation")
public class ModInputFormat extends TextInputFormat {

	@Override
	public RecordReader<LongWritable, Text> createRecordReader(
			InputSplit split, TaskAttemptContext context) {
		return new ModLineRecordReader();
	}

}

@SuppressWarnings("deprecation")
class ModLineRecordReader extends RecordReader<LongWritable, Text> {
	private static final Log LOG = LogFactory.getLog(LineRecordReader.class);

	private CompressionCodecFactory compressionCodecs = null;
	private long start;
	private long pos;
	private long end;
	private LineReader in;
	private int maxLineLength;
	private LongWritable key = null;
	private Text value = null;
	private Seekable filePosition;
	private CompressionCodec codec;
	private Decompressor decompressor;

	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException {
		FileSplit split = (FileSplit) genericSplit;
		Configuration job = context.getConfiguration();
		this.maxLineLength = job.getInt("mapred.linerecordreader.maxlength",
				2147483647);

		this.start = split.getStart();
		this.end = (this.start + split.getLength());
		Path file = split.getPath();
		this.compressionCodecs = new CompressionCodecFactory(job);
		this.codec = this.compressionCodecs.getCodec(file);

		FileSystem fs = file.getFileSystem(job);
		FSDataInputStream fileIn = fs.open(split.getPath());

		if (isCompressedInput()) {
//			this.decompressor = CodecPool.getDecompressor(this.codec);
//			if ((this.codec instanceof SplittableCompressionCodec)) {
//				SplitCompressionInputStream cIn = ((SplittableCompressionCodec) this.codec)
//						.createInputStream(fileIn, this.decompressor,
//								this.start, this.end,
//								SplittableCompressionCodec.READ_MODE.BYBLOCK);
//
//				this.in = new LineReader(cIn, job);
//				this.start = cIn.getAdjustedStart();
//				this.end = cIn.getAdjustedEnd();
//				this.filePosition = cIn;
//			} else {
//				this.in = new LineReader(this.codec.createInputStream(fileIn,
//						this.decompressor), job);
//
//				this.filePosition = fileIn;
//			}
		} else {
			fileIn.seek(this.start);
			this.in = new LineReader(fileIn, job);
			this.filePosition = fileIn;
		}

		if (this.start != 0L) {
			this.start += this.in.readLine(new Text(), 0,
					maxBytesToConsume(this.start));
		}
		this.pos = this.start;
	}

	private boolean isCompressedInput() {
		return this.codec != null;
	}

	private int maxBytesToConsume(long pos) {
		return isCompressedInput() ? 2147483647 : (int) Math.min(2147483647L,
				this.end - pos);
	}

	private long getFilePosition() throws IOException {
		long retVal;
		if ((isCompressedInput()) && (null != this.filePosition)) {
			retVal = this.filePosition.getPos();
		} else {
			retVal = this.pos;
		}
		return retVal;
	}

	public boolean nextKeyValue() throws IOException {
		if (this.key == null) {
			this.key = new LongWritable();
		}
		this.key.set(this.pos);
		if (this.value == null) {
			this.value = new Text();
		}
		int newSize = 0;

		while (getFilePosition() <= this.end) {
			newSize = this.in.readLine(this.value, this.maxLineLength,
					Math.max(maxBytesToConsume(this.pos), this.maxLineLength));

			if (newSize == 0) {
				break;
			}
			this.pos += newSize;
			if (newSize < this.maxLineLength) {
				break;
			}

			LOG.info("Skipped line of size " + newSize + " at pos "
					+ (this.pos - newSize));
		}

		if (newSize == 0) {
			this.key = null;
			this.value = null;
			return false;
		}
		return true;
	}

	public LongWritable getCurrentKey() {
		return this.key;
	}

	public Text getCurrentValue() {
		return this.value;
	}

	public float getProgress() throws IOException {
		if (this.start == this.end) {
			return 0.0F;
		}
		return Math.min(1.0F, (float) (getFilePosition() - this.start)
				/ (float) (this.end - this.start));
	}

	public synchronized void close() throws IOException {
		try {
			if (this.in != null) {
				this.in.close();
			}
		} finally {
			if (this.decompressor != null) {
				CodecPool.returnDecompressor(this.decompressor);
			}
		}
	}
}
