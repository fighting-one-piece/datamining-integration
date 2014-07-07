package org.project.modules.hadoop.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class TextPair implements WritableComparable<TextPair> {
	private Text first;
	private Text second;

	public TextPair() {
		this.first = new Text();
		this.second = new Text();
	}

	public TextPair(String s1, String s2) {
		this.first = new Text(s1);
		this.second = new Text(s2);
	}

	public TextPair(Text first, Text second) {
		this.first = first;
		this.second = second;
	}

	public Text getFirst() {
		return first;
	}

	public Text getSecond() {
		return second;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first.readFields(in);
		second.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		first.write(out);
		second.write(out);
	}

	@Override
	public int compareTo(TextPair o) {
		// TODO Auto-generated method stub
		int cmp = first.compareTo(o.first);
		System.out.println("cmp: " + cmp);
		if (cmp != 0)
			return cmp;
		else
			return second.compareTo(o.second);
	}

	public boolean equals(Object o) {
		if (o instanceof TextPair) {
			TextPair tp = (TextPair) o;
			return first.equals(tp.first) && second.equals(tp.second);
		}
		return false;
	}

	public static void main(String[] args) throws IOException, IOException {
		TextPair t1 = new TextPair("zpc", "cy");
		TextPair t2 = new TextPair("zpc", "cy");
		System.out.println("t1.compareTo(t2):" + t1.compareTo(t2));
		System.out.println("t1.equals(t2):" + t1.equals(t2));
//		t1.write(new DataOutputStream(
//				new FileOutputStream("/home/dataText.txt")));
	}
}
