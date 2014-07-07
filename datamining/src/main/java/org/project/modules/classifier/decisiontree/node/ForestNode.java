package org.project.modules.classifier.decisiontree.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataHandler;
import org.project.modules.classifier.decisiontree.data.Instance;

public class ForestNode extends Node {

	private static final long serialVersionUID = 1L;
	
	private List<TreeNode> treeNodes = null;
	
	public ForestNode(List<TreeNode> treeNodes) {
		this.treeNodes = treeNodes;
	}
	
	@Override
	public Type getType() {
		return null;
	}
	
	@Override
	public Object classify(Data data) {
		List<Object[]> results = new ArrayList<Object[]>();
		for (TreeNode treeNode : treeNodes) {
			Object result = treeNode.classify(data);
			if (null != result) {
				results.add((Object[]) treeNode.classify(data));
			}
		}
		return DataHandler.vote(results);
	}
	
	@Override
	public Object classify(Instance... instances) {
		List<Object[]> results = new ArrayList<Object[]>();
		for (TreeNode treeNode : treeNodes) {
			Object result = treeNode.classify(instances);
			if (null != result) {
				results.add((Object[]) treeNode.classify(instances));
			}
		}
		//投票选择
		return DataHandler.vote(results);
	}

	@Override
	public void readNode(DataInput dataInput) throws IOException {
		
	}

	@Override
	protected void writeNode(DataOutput dataOutput) throws IOException {
		
	}
	
	
}
