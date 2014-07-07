package org.project.modules.classifier.adaboost;

import java.util.ArrayList;

public class AdaboostAlgorithm {
	private static final int T = 30; // 迭代次数

	private PerceptronApproach pa = new PerceptronApproach(); // 弱分类器

	public AdaboostResult adaboostClassify(ArrayList<ArrayList<Double>> dataSet) {
		AdaboostResult res = new AdaboostResult();
		int dataDimension = 0;
		if (null != dataSet && dataSet.size() > 0) {
			dataDimension = dataSet.get(0).size();
		} else {
			return null;
		}
		// 为每条数据的权重赋初值
		ArrayList<Double> dataWeightSet = new ArrayList<Double>();
		for (int i = 0; i < dataSet.size(); i++) {
			dataWeightSet.add(1.0 / (double) dataSet.size());
		}
		// 存储每个弱分类器的权重
		ArrayList<Double> classifierWeightSet = new ArrayList<Double>();
		// 存储每个弱分类器(特征属性的权重)
		ArrayList<ArrayList<Double>> weakClassifierSet = new ArrayList<ArrayList<Double>>();

		for (int i = 0; i < T; i++) {
			// 计算弱分类器 获取特征属性的相应权重
			ArrayList<Double> sensorWeightVector = pa.getWeightVector(dataSet,
					dataWeightSet);
			weakClassifierSet.add(sensorWeightVector);

			// 计算弱分类器误差
			double error = 0; // 分类数
			int rightClassifyNum = 0;
			ArrayList<Double> cllassifyResult = new ArrayList<Double>();
			for (int j = 0; j < dataSet.size(); j++) {
				double result = 0;
				for (int k = 0; k < dataDimension - 1; k++) {
					result += dataSet.get(j).get(k) * sensorWeightVector.get(k);
				}
				result += sensorWeightVector.get(dataDimension - 1);
				if (result < 0) { // 说明预测错误
					error += dataWeightSet.get(j);
					cllassifyResult.add(-1d);
				} else {
					cllassifyResult.add(1d);
					rightClassifyNum++;
				}
			}
			System.out.println("总数：" + dataSet.size() + "正确预测数"
					+ rightClassifyNum);
			if (dataSet.size() == rightClassifyNum) {
				classifierWeightSet.clear();
				weakClassifierSet.clear();
				classifierWeightSet.add(1.0);
				weakClassifierSet.add(sensorWeightVector);
				break;
			}
			// 更新数据集中每条数据的权重并归一化
			double dataWeightSum = 0;
			for (int j = 0; j < dataSet.size(); j++) {
				dataWeightSet.set(j,
						dataWeightSet.get(j)
								* Math.pow(Math.E,(-1) * 0.5 * 
										Math.log((1 - error) / error) * 
											cllassifyResult.get(j))); 
				// 按照http://wenku.baidu.com/view/49478920aaea998fcc220e98.html，更新的权重少除一个常数
				dataWeightSum += dataWeightSet.get(j);
			}
			for (int j = 0; j < dataSet.size(); j++) {
				dataWeightSet.set(j, dataWeightSet.get(j) / dataWeightSum);
			}

			// 计算次弱分类器的权重
			double currentWeight = (0.5 * Math.log((1 - error) / error));
			classifierWeightSet.add(currentWeight);
			System.out.println("classifier weight: " + currentWeight);
		}
		res.setClassifierWeightSet(classifierWeightSet);
		res.setWeakClassifierSet(weakClassifierSet);
		return res;
	}

	public int computeResult(ArrayList<Double> data, AdaboostResult classifier) {
		double result = 0;
		int dataSize = data.size();
		ArrayList<ArrayList<Double>> weakClassifierSet = classifier
				.getWeakClassifierSet();
		ArrayList<Double> classifierWeightSet = classifier
				.getClassifierWeightSet();
		for (int i = 0; i < weakClassifierSet.size(); i++) {
			for (int j = 0; j < dataSize; j++) {
				result += weakClassifierSet.get(i).get(j) * data.get(j)
						* classifierWeightSet.get(i);
			}
			result += weakClassifierSet.get(i).get(dataSize);
		}
		if (result > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	public static void main(String[] args) {
		/**
		 * 测试数据，产生两类随机数据一类位于圆内，另一类位于包含小圆的大圆内，成环状 小圆半径为1，大圆半径为2，公共圆心位于(2, 2)内
		 */
		final int SMALL_CIRCLE_NUM = 24;
		final int RING_NUM = 34;
		ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
		// 产生小圆数据
		for (int i = 0; i < SMALL_CIRCLE_NUM; i++) {
			double x = 1 + Math.random() * 2; // 1到3的随机数
			double y = 1 + Math.random() * 2; // 1到3的随机数
			if ((x - 2) * (x - 2) + (y - 2) * (y - 2) - 1 <= 0) { // 说明位于圆内
				ArrayList<Double> smallCircle = new ArrayList<Double>();
				smallCircle.add(x);
				smallCircle.add(y);
				smallCircle.add(1d); // 列别1
				dataSet.add(smallCircle);
			}
		}
		// 产生外围环形数据
		for (int i = 0; i < RING_NUM; i++) {
			double x1 = Math.random() * 4;
			double y1 = Math.random() * 4;
			if ((x1 - 2) * (x1 - 2) + (y1 - 2) * (y1 - 2) - 4 < 0
					&& (x1 - 2) * (x1 - 2) + (y1 - 2) * (y1 - 2) - 1 > 0) { // 说明位于环形区域内
				ArrayList<Double> ring = new ArrayList<Double>();
				ring.add(-x1);
				ring.add(-y1);
				ring.add(-1d); // 列别2
				dataSet.add(ring);
			}
		}
		AdaboostAlgorithm algo = new AdaboostAlgorithm();
		AdaboostResult result = algo.adaboostClassify(dataSet);
		// 产生测试数据
		for (int i = 0; i < 10; i++) {
			ArrayList<Double> testData = new ArrayList<Double>();
			double x1 = Math.random() * 4;
			double y1 = Math.random() * 4;
			if ((x1 - 2) * (x1 - 2) + (y1 - 2) * (y1 - 2) - 4 < 0
					&& (x1 - 2) * (x1 - 2) + (y1 - 2) * (y1 - 2) - 1 > 0) {
				testData.add(x1);
				testData.add(y1);
			}
			// double x = 1 + Math.random() * 2; // 1到3的随机数
			// double y = 1 + Math.random() * 2; // 1到3的随机数
			// if((x - 2) * (x - 2) + (y - 2) * (y - 2) - 1 <= 0) { //说明位于圆内
			// testData.add(x);
			// testData.add(y);
			// }
			algo.computeResult(testData, result);
			System.out.println(algo.computeResult(testData, result));
		}

	}

}
