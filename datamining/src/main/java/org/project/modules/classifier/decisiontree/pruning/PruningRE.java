package org.project.modules.classifier.decisiontree.pruning;

import java.util.Map;

import org.project.modules.classifier.decisiontree.node.TreeNode;

/** 错误率降低剪枝*/
public class PruningRE implements Pruning {

	@Override
	public void pruning(TreeNode treeNode) {
		if (treeNode.hasBranchNode()) {
			Map<Object, Object> children = treeNode.getChildren();
			for (Map.Entry<Object, Object> entry : children.entrySet()) {
				if (entry.getValue() instanceof TreeNode) {
					pruning((TreeNode) entry.getValue());
				}
			}
		} else {

		}
	}

}
