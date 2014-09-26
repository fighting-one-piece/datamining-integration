package org.project.modules.hive.serde;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class TestSerDe extends AbstractSerDe {

	private List<String> structFieldNames = new ArrayList<String>();
	private List<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>();

	@Override
	public void initialize(Configuration conf, Properties tbl)
			throws SerDeException {
		structFieldNames.add("id");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("name");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
		structFieldNames.add("age");
		structFieldObjectInspectors.add(ObjectInspectorFactory
				.getReflectionObjectInspector(String.class,
						ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
	}

	@Override
	public ObjectInspector getObjectInspector() throws SerDeException {
		return ObjectInspectorFactory.getStandardStructObjectInspector(
				structFieldNames, structFieldObjectInspectors);
	}

	@Override
	public SerDeStats getSerDeStats() {
		return null;
	}

	@Override
	public Class<? extends Writable> getSerializedClass() {
		return null;
	}

	@Override
	public Writable serialize(Object arg0, ObjectInspector arg1)
			throws SerDeException {
		return null;
	}

	@Override
	public Object deserialize(Writable writable) throws SerDeException {
		if (writable == null)
			return null;
		Text text = (Text) writable;
		String[] values = text.toString().split("\t");
		ArrayList<Object> row = new ArrayList<Object>();
		row.add(values[0]);
		row.add(values[1]);
		row.add(values[2]);
		return row;
	}

}
