package org.project.modules.hadoop.mr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MatrixMultiplyMR {
	
	public static final Pattern DELIMITER = Pattern.compile("[\t,]");
	
	private static void configureJob(Job job) {
		job.setJarByClass(MatrixMultiplyMR.class);
		
		job.setMapperClass(MatrixMultiplyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(MatrixMultiplyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BytesWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		try {
			String[] inputArgs = new GenericOptionsParser(
						configuration, args).getRemainingArgs();
			if (inputArgs.length != 2) {
				System.out.println("error, please input two path. input and output");
				System.exit(2);
			}
			Job job = new Job(configuration, "Martrix Multiply");
			
			FileInputFormat.addInputPath(job, new Path(inputArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(inputArgs[1]));
			
			configuration.set("rowNum", inputArgs[2]);
			configuration.set("colNum", inputArgs[3]);
			
			configureJob(job);
			
			System.out.println(job.waitForCompletion(true) ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class MatrixMultiplyMapper extends Mapper<LongWritable, Text, Text, Text> {

	/** 行*/
	private int rowNum = 0;
	/** 列*/
	private int colNum = 0;
	/** 判断读的数据集名称*/
	private String fileName = null;
	/** 矩阵A，当前在第几行*/
	private int rowIndexA = 1; 
	/** 矩阵B，当前在第几行*/
    private int rowIndexB = 1; 
    
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		rowNum = Integer.parseInt(conf.get("rowNum"));
		colNum = Integer.parseInt(conf.get("colNum"));
		FileSplit split = (FileSplit) context.getInputSplit();
		fileName = split.getPath().getName(); 
	}
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = MatrixMultiplyMR.DELIMITER.split(value.toString());
        if (fileName.equals("m1")) {
            for (int i = 1; i <= rowNum; i++) {
                Text k = new Text(rowIndexA + "," + i);
                for (int j = 1; j <= tokens.length; j++) {
                    Text v = new Text("A:" + j + "," + tokens[j - 1]);
                    context.write(k, v);
                    System.out.println(k.toString() + "  " + v.toString());
                }

            }
            rowIndexA++;
        } else if (fileName.equals("m2")) {
            for (int i = 1; i <= tokens.length; i++) {
                for (int j = 1; j <= colNum; j++) {
                    Text k = new Text(i + "," + j);
                    Text v = new Text("B:" + rowIndexB + "," + tokens[j - 1]);
                    context.write(k, v);
                    System.out.println(k.toString() + "  " + v.toString());
                }
            }
            rowIndexB++;
        }
	}
}

class MatrixMultiplyReducer extends Reducer<Text, Text, Text, IntWritable> {
	
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Map<String, String> mapA = new HashMap<String, String>();
        Map<String, String> mapB = new HashMap<String, String>();
        System.out.print(key.toString() + ":");
        for (Text value : values) {
            String val = value.toString();
            System.out.print("("+val+")");
            if (val.startsWith("A:")) {
                String[] kv = MatrixMultiplyMR.DELIMITER.split(val.substring(2));
                mapA.put(kv[0], kv[1]);
            } else if (val.startsWith("B:")) {
                String[] kv = MatrixMultiplyMR.DELIMITER.split(val.substring(2));
                mapB.put(kv[0], kv[1]);
            }
        }
        int result = 0;
        Iterator<String> iter = mapA.keySet().iterator();
        while (iter.hasNext()) {
            String mapk = iter.next();
            result += Integer.parseInt(mapA.get(mapk)) * Integer.parseInt(mapB.get(mapk));
        }
        context.write(key, new IntWritable(result));
	}
}