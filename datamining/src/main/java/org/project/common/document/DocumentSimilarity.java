package org.project.common.document;

public class DocumentSimilarity {
	
	private Document doc1 = null;
	
	private Document doc2 = null;
	
	private double distance = 0;

	public Document getDoc1() {
		return doc1;
	}

	public void setDoc1(Document doc1) {
		this.doc1 = doc1;
	}

	public Document getDoc2() {
		return doc2;
	}

	public void setDoc2(Document doc2) {
		this.doc2 = doc2;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("doc1:").append(doc1.getName()).append("[")
		  .append(doc1.getCategory()).append("]--");
		sb.append("doc2:").append(doc2.getName()).append("[")
		  .append(doc2.getCategory()).append("]--");
		sb.append("distance:").append(distance);
		return sb.toString();
	}
	
}
