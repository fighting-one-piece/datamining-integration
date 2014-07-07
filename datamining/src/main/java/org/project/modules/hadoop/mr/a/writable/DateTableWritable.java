package org.project.modules.hadoop.mr.a.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class DateTableWritable implements WritableComparable<DateTableWritable>{
	
	private Text date = null;
	
	private Text table = null;
	
	public DateTableWritable() {
		date = new Text();
		table = new Text();
	}
	
	public DateTableWritable(Text date, Text table) {
		super();
		this.date = date;
		this.table = table;
	}

	public Text getDate() {
		return date;
	}

	public void setDate(Text date) {
		this.date = date;
	}

	public Text getTable() {
		return table;
	}

	public void setTable(Text table) {
		this.table = table;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		date.readFields(in);
		table.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		date.write(out);
		table.write(out);
	}

	@Override
	public int compareTo(DateTableWritable o) {
		int flag = date.compareTo(o.getDate());
		if (flag == 0) {
			flag = table.compareTo(o.getTable());
		}
		return flag;
	}

}
