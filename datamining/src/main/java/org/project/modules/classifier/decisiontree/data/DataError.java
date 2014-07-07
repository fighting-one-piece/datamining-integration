package org.project.modules.classifier.decisiontree.data;

public class DataError {

	/** 判定前类型*/
	private Object[] preCategories = null;
	/** 判定后类型*/
	private Object[] postCategories = null;
	/** 错误个数*/
	private int errorNum = 0;
	/** 总个数*/
	private int totalNum = 0;
	
	public DataError(Object[] preCategories, Object[] postCategories) {
		super();
		this.preCategories = preCategories;
		this.postCategories = postCategories;
		compare();
	}
	
	public Object[] getPreCategories() {
		return preCategories;
	}
	
	public void setPreCategories(Object[] preCategories) {
		this.preCategories = preCategories;
	}
	
	public Object[] getPostCategories() {
		return postCategories;
	}
	
	public void setPostCategories(Object[] postCategories) {
		this.postCategories = postCategories;
	}
	
	public int getRightNum() {
		return totalNum - errorNum;
	}
	
	public double getRightRatio() {
		if (getRightNum() == 0) return 0.0;
		return (getRightNum() * 1.0) / totalNum;
	}
	
	public int getErrorNum() {
		return errorNum;
	}
	
	public double getErrorRatio() {
		if (errorNum == 0) return 0.0;
		return (errorNum * 1.0) / totalNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public void compare() {
		if (preCategories.length != postCategories.length) {
			throw new RuntimeException("categories length not equal");
		}
		totalNum = preCategories.length;
		for (int i = 0; i < totalNum; i++) {
			if (!preCategories[i].equals(postCategories[i])) {
				errorNum += 1;
			}
		}
	}
	
	public double getAlpha() {
		double e = getErrorRatio();
		return (1 / 2) *  Math.log((1 - e) / e);
	}
	
	public void report() {
		StringBuilder sb = new StringBuilder();
		sb.append("TotalNum: ").append(totalNum).append("\n");
		sb.append("RightNum: ").append(getRightNum()).append("\t");
		sb.append("RightRatio: ").append(getRightRatio()).append("\n");
		sb.append("ErrorNum: ").append(getErrorNum()).append("\t");
		sb.append("ErrorRatio: ").append(getErrorRatio()).append("\n");
		System.out.println(sb.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TotalNum: ").append(totalNum).append("\n");
		sb.append("RightNum: ").append(getRightNum()).append("\t");
		sb.append("RightRatio: ").append(getRightRatio()).append("\n");
		sb.append("ErrorNum: ").append(getErrorNum()).append("\t");
		sb.append("ErrorRatio: ").append(getErrorRatio()).append("\n");
		return sb.toString();
	}
	
}
