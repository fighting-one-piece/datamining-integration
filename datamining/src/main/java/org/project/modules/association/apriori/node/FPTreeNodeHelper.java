package org.project.modules.association.apriori.node;

public class FPTreeNodeHelper {
	
	public static void print(FPTreeNode treeNode, int level) {
		for (int i = 0; i <= level; i++) {
			System.out.print("|----");
		}
		System.out.print("[" + treeNode.getName() + "]");
		System.out.println("{count: " + treeNode.getCount() + "}");
		for (FPTreeNode child : treeNode.getChildren()) {
			print(child, level + 1);
		}
	}
	
}
