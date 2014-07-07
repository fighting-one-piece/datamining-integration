package org.project.modules.classifier.decisiontree.mr.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.project.modules.classifier.decisiontree.node.TreeNode;
import org.project.modules.classifier.decisiontree.node.TreeNodeHelper;

public class TreeNodeWritable implements Writable, Cloneable {
	
	private TreeNode treeNode = null;
	
	public TreeNodeWritable() {
		
	}
	
	public TreeNodeWritable(TreeNode treeNode) {
		this.treeNode = treeNode;
	}
	
	public TreeNode getTreeNode() {
		return treeNode;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		boolean isReadTree = dataInput.readBoolean();
		if (isReadTree) {
			treeNode = TreeNodeHelper.readTreeNode(dataInput);
		}
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeBoolean(null != treeNode);
		if (null != treeNode) {
			treeNode.write(dataOutput);
		}
	}

}
