#-*- coding: utf-8 -*-

import numpy as np

#欧氏距离
def similarByEclud(x, y):
    return 1.0 / (1.0 + np.linalg.norm(x - y))

#皮尔逊相关系数
def similarByPearsonCorrelation(x, y):
    if len(x) < 3: return 1.0
    return 0.5 + 0.5 * np.corrcoef(x, y, rowvar=0)[0][1]

#余弦相似度
def similarByConsine(x, y):
    a = float(x.T * y)
    b = np.linalg.norm(x) * np.linalg.norm(y)
    return 0.5 + 0.5 * (a / b)

#截取特征值
def cutEigValue(eigValues):
    totalSum = 0.0
    for eigValue in eigValues:
        totalSum += eigValue**2
        print totalSum
    threshold = 0.98 * totalSum
    print 'threshold:%s' %threshold
    sum = 0.0; index = 0
    for eigValue in eigValues:
        sum += eigValue**2
        index += 1
        print 'sum:%s' %sum
        if (sum >= threshold): return index
    
def loadDataSet(path):
    ids = [];categories = []
    attributes = [];datas = []
    fr = open(path)
    for line in fr.readlines():
        line = line.strip()
        splits = line.split()
        data = []
        for i in range(len(splits)):
            if (i == 0):
                ids.append(splits[i])
            elif(i == 1):
                categories.append(splits[i])
            else:
                kv = splits[i].split(':')
                attributes.append(kv[0])
                data.append(float(kv[1]))
        datas.append(data)
    attributes = set(attributes)
    return ids,categories,attributes,np.mat(datas)
    
if __name__ == '__main__':
    path = r'D:\resources\trainset\attribute_10_r_10.txt'
    result = loadDataSet(path)
    print result[0]
    print result[1]
    print result[2]
    print result[3]
    eigValues, eigVectors = np.linalg.eig(result[3])
    print eigValues
    sorted(eigValues, reverse=True)
    print eigValues
    print cutEigValue(eigValues)
