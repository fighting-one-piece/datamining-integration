package org.project.modules.hive.serde;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

@SuppressWarnings("deprecation")
public class TestSerDe extends AbstractSerDe {

	private List<String> columnNames = null;
	private List<TypeInfo> columnTypes = null;
	private ObjectInspector objectInspector = null;

	@Override
	public void initialize(Configuration conf, Properties tbl)
			throws SerDeException {
		String columnNameProp = tbl.getProperty(Constants.LIST_COLUMNS);
		if (columnNameProp != null && columnNameProp.length() > 0) {
			columnNames = Arrays.asList(columnNameProp.split(","));
		} else {
			columnNames = new ArrayList<String>();
		}
		String columnTypeProp = tbl.getProperty(Constants.LIST_COLUMN_TYPES);
		if (columnTypeProp == null) {
			String[] types = new String[columnNames.size()];
			Arrays.fill(types, 0, types.length, Constants.STRING_TYPE_NAME);
			columnTypeProp = StringUtils.join(types, ":");
		}
		columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(columnTypeProp);

		// Check column and types equals
		if (columnTypes.size() != columnNames.size()) {
			throw new SerDeException("len(columnNames) != len(columntTypes)");
		}

		// Create ObjectInspectors from the type information for each column
		List<ObjectInspector> columnOIs = new ArrayList<ObjectInspector>();
		ObjectInspector oInspector;
		for (int c = 0; c < columnNames.size(); c++) {
			oInspector = TypeInfoUtils
					.getStandardJavaObjectInspectorFromTypeInfo(columnTypes.get(c));
			columnOIs.add(oInspector);
		}
		objectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(columnNames, columnOIs);

	}
	
	@Override
	public ObjectInspector getObjectInspector() throws SerDeException {
		return objectInspector;
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
        Map<String, String> kvMap = new HashMap<String, String>();
        for (int i = 0, len = values.length; i < len; i++) {
        	kvMap.put(columnNames.get(i), values[i]);
        }
		ArrayList<Object> row = new ArrayList<Object>();
		String colName = null;
		TypeInfo typeInfo = null;
		Object obj = null;
		for (int i = 0; i < columnNames.size(); i++) {
			colName = columnNames.get(i);
			typeInfo = columnTypes.get(i);
			obj = null;
			if (typeInfo.getCategory() == ObjectInspector.Category.PRIMITIVE) {
				PrimitiveTypeInfo pTypeInfo = (PrimitiveTypeInfo) typeInfo;
				switch (pTypeInfo.getPrimitiveCategory()) {
				case STRING:
					obj = StringUtils.defaultString(kvMap.get(colName), "");
					break;
				case INT:
					try {
						obj = Long.parseLong(kvMap.get(colName));
					} catch (Exception e) {
						
					}
				case LONG:
				default:
					break;
				}
			}
			row.add(obj);
		}
        return row;
	}

}
