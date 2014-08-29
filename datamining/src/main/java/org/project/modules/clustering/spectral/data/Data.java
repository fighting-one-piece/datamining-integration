package org.project.modules.clustering.spectral.data;

import java.util.Map;

public class Data {
	
	private String[] row = null;
	
	private String[] column = null;

	private String[] docnames = null;
	
	private double[][] original = null;
	
	private Map<String, String> cmap = null;
	
	private Map<String, Map<String, Double>> nmap = null;

	public String[] getRow() {
		return row;
	}

	public void setRow(String[] row) {
		this.row = row;
	}

	public String[] getColumn() {
		return column;
	}

	public void setColumn(String[] column) {
		this.column = column;
	}
	
	public String[] getDocnames() {
		return docnames;
	}

	public void setDocnames(String[] docnames) {
		this.docnames = docnames;
	}

	public double[][] getOriginal() {
		return original;
	}

	public void setOriginal(double[][] original) {
		this.original = original;
	}
	
	public Map<String, String> getCmap() {
		return cmap;
	}

	public void setCmap(Map<String, String> cmap) {
		this.cmap = cmap;
	}

	public Map<String, Map<String, Double>> getNmap() {
		return nmap;
	}

	public void setNmap(Map<String, Map<String, Double>> nmap) {
		this.nmap = nmap;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, il = row.length; i < il; i++) {
			for (int j = 0, jl = column.length; j < jl; j++) {
				sb.append(original[i][j] + "  ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
