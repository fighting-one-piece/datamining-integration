package org.project.modules.association.apriori.node;

import java.util.Set;

import org.project.modules.association.apriori.data.ItemSet;

public class AssociationRuleHelper {
	
	public static void print(AssociationRule associationRule) {
		System.out.print("[" + convert(associationRule.getLeft()) + "]");
		System.out.print("------>");
		System.out.print("[" + convert(associationRule.getRight()) + "]");
		System.out.println("{confidence: " + associationRule.getConfidence() + "}");
	}

	public static void print(AssociationRule associationRule, int level) {
		for (int i = 0; i <= level; i++) {
			System.out.print("|----");
		}
		System.out.print("[" + convert(associationRule.getLeft()) + "]");
		System.out.print("------>");
		System.out.print("[" + convert(associationRule.getRight()) + "]");
		System.out.println("{confidence: " + associationRule.getConfidence() + "}");
		for (AssociationRule child : associationRule.getChildren()) {
			print(child, level + 1);
		}
	}
	
	public static boolean isContain(Set<AssociationRule> ars, AssociationRule ar) {
		for (AssociationRule associationRule : ars) {
			if (associationRule.isEqual(ar)) {
				return true;
			}
		}
		return false;
	}
	
	public static String convert(ItemSet itemSet) {
		StringBuilder sb = new StringBuilder();
		for (String item : itemSet.getItems()) {
			sb.append(item).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public static String convert(String[] items) {
		StringBuilder sb = new StringBuilder();
		for (String item : items) {
			sb.append(item).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
