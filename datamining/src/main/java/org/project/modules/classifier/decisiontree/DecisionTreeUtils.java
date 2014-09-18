package org.project.modules.classifier.decisiontree;

import org.project.modules.classifier.decisiontree.builder.Builder;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.DataLoader;
import org.project.modules.classifier.decisiontree.node.TreeNode;

public class DecisionTreeUtils {

	public static TreeNode build(Data data, Builder builder) {
		return (TreeNode) builder.build(data);
	}
	
	public static TreeNode build(String path, Builder builder) {
		Data data = DataLoader.loadWithId(path);
		return (TreeNode) builder.build(data);
	}
	
	public static Object[] classify(TreeNode treeNode, Data data) {
		return (Object[]) treeNode.classify(data);
	}
	
	public static Object[] classify(TreeNode treeNode, String path) {
		Data data = DataLoader.loadNoId(path);
		return (Object[]) treeNode.classify(data);
	}
	
}
