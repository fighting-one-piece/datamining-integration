package org.project.modules.association.similar.data;

public class DocumentSimilarity {

	private String docName1 = null;
	
	private String docName2 = null;
	
	private double[] vector1 = null;
	
	private double[] vector2 = null;
	
	private double cosine = 0;

	public String getDocName1() {
		return docName1;
	}

	public void setDocName1(String docName1) {
		this.docName1 = docName1;
	}

	public String getDocName2() {
		return docName2;
	}

	public void setDocName2(String docName2) {
		this.docName2 = docName2;
	}

	public double[] getVector1() {
		return vector1;
	}

	public void setVector1(double[] vector1) {
		this.vector1 = vector1;
	}

	public double[] getVector2() {
		return vector2;
	}

	public void setVector2(double[] vector2) {
		this.vector2 = vector2;
	}

	public double getCosine() {
		return cosine;
	}

	public void setCosine(double cosine) {
		this.cosine = cosine;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("doc1:").append(docName1).append("--");
		sb.append("doc2:").append(docName2).append("--");
		sb.append("cosine:").append(cosine);
		return sb.toString();
	}
	
}
