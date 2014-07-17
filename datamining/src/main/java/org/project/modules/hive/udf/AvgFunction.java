package org.project.modules.hive.udf;

import org.apache.hadoop.hive.ql.exec.NumericUDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;

public class AvgFunction extends NumericUDAF {
	
	public static class AvgEvaluator implements UDAFEvaluator {
		
		private double sum = 0;
		
		private int count = 0;
		
		private boolean isEmpty = true;

		@Override
		public void init() {
			sum = 0;
			count = 0;
			isEmpty = true;
		}
		
		public boolean iterate(IntWritable o) {
			if (null != o) {
				sum += o.get();
				count += 1;
				isEmpty = false;
			}
			return true;
		}
		
        public DoubleWritable terminatePartial() {
            return isEmpty ? null : new DoubleWritable(sum / count);
        }

        public boolean merge(DoubleWritable o) {
            if (o != null) {
                sum += o.get();
                count += 1;
                isEmpty = false;
            }
            return true;
        }

        public DoubleWritable terminate() {
            return isEmpty ? null : new DoubleWritable(sum / count);
        }
		
	}

}
