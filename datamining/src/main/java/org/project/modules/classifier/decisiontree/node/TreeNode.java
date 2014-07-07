package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

/**
 ** 决策树（非叶结点），决策树中的每个非叶结点都引导了一棵决策树 
 *  每个非叶结点包含一个分支属性和多个分支，分支属性的每个值对应一个分支，该分支引导了一棵子决策树
 */
public class TreeNode extends Node {
	
	private static final long serialVersionUID = 1L;
	/** 节点名称*/
	private String name = null;
	
	private int record = 0;
	/** 树分支*/
	private Map<Object, Object> children = null;

	public TreeNode() {
		
	}
	
	public TreeNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}
	
	public void addOneRecord() {
		this.record += 1;
	}

	public Object getChild(Object attributeValue) {
		return getChildren().get(attributeValue);
	}

	public void setChild(Object attributeValue, Object child) {
		getChildren().put(attributeValue, child);
	}

	public Map<Object, Object> getChildren() {
		if (null == children) {
			children = new HashMap<Object, Object>();
		}
		return children;
	}
	
	public void setChildren(Map<Object, Object> children) {
		this.children = children;
	}
	
	public void clearChildren() {
		getChildren().clear();
	}
	
	public boolean hasBranchNode() {
		boolean hasBranchNode = false;
		for (Map.Entry<Object, Object> entry : getChildren().entrySet()) {
			if (entry.getValue() instanceof TreeNode) {
				hasBranchNode = true;
			}
		}
		return hasBranchNode;
	}
	
	public int getLeafNumber() {
		int leafNum = 0;
		for (Map.Entry<Object, Object> entry : getChildren().entrySet()) {
			if (!(entry.getValue() instanceof TreeNode)) {
				leafNum += 1;
			}
		}
		return leafNum;
	}
	
	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Object classify(Data data) {
		List<Instance> instances = data.getInstances();
		return classify(instances.toArray(new Instance[0]));
	}
	
	@Override
	public Object classify(Instance... instances) {
		int length = instances.length;
		if (length == 0) return null;
		Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = classify(instances[i]);
		}
		return result;
	}
	
	public Object classify(Instance instance) {
		Object attributeValue = instance.getAttribute(name);
		if (null == attributeValue) return null;
		addOneRecord();
		for (Map.Entry<Object, Object> entry : getChildren().entrySet()) {
			if (attributeValue.equals(entry.getKey())) {
				Object value = entry.getValue();
				if (value instanceof TreeNode) {
					return ((TreeNode) value).classify(instance);
				} else {
					return value;
				}
			} 
		}
		return null;
	}
	
	public Object classifySprint(Data data) {
		List<Instance> instances = data.getInstances();
		return classifySprint(instances.toArray(new Instance[0]));
	}
	
	public Object classifySprint(Instance... instances) {
		int length = instances.length;
		if (length == 0) return null;
		Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = classifySprint(instances[i]);
		}
		return result;
	}
	
	public Object classifySprint(Instance instance) {
		Object attributeValue = instance.getAttribute(name);
		if (null == attributeValue) return null;
		addOneRecord();
		for (Map.Entry<Object, Object> entry : getChildren().entrySet()) {
			String key = String.valueOf(entry.getKey());
			if (key.indexOf(attributeValue.toString()) != -1) {
				Object value = entry.getValue();
				if (value instanceof TreeNode) {
					return ((TreeNode) value).classifySprint(instance);
				} else {
					return value;
				}
			} 
		}
		return null;
	}
	
	@Override
	public void readFields(DataInput dataInput) throws IOException {
		int length = dataInput.readInt();
		byte[] buff = new byte[length];
		dataInput.readFully(buff, 0, length);
		String jsonData = new String(buff);
		TreeNode temp = (TreeNode) TreeNodeHelper.json2TreeNode(jsonData);
		this.name = temp.getName();
		this.children = temp.getChildren();
	}
	
	@Override
	protected void readNode(DataInput dataInput) throws IOException {
		
	}
	
	@Override
	public void write(DataOutput dataOutput) throws IOException {
		StringBuilder sb = new StringBuilder();
		TreeNodeHelper.treeNode2json(this, sb);
		System.out.println(sb.toString());
		dataOutput.writeInt(sb.length());
		dataOutput.write(sb.toString().getBytes());
	}
	
	@Override
	protected void writeNode(DataOutput dataOutput) throws IOException {
		
	}
	
}
