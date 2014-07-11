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
    threshold = 0.9 * totalSum
    print 'threshold:%d' %threshold
    sum = 0.0; index = 0
    for eigValue in eigValues:
        sum += eigValue**2
        index += 1
        print 'sum:%d' %sum
        if (sum >= threshold): return index
    
#预估用户某一项的评分
def estimate(dataMat, user, item, similar):
    n = np.shape(dataMat)[1]
    totalSim = 0.0; totalUserSim = 0.0
    for i in range(n):
        print 'i: %d' % i
        userScore = dataMat[user, i]
        if userScore == 0 or i == item: continue
        print 'userScore: %d' % userScore
        #当前列与预估列同时有评分的用户
        users = np.nonzero(np.logical_and(\
            dataMat[:, item].A > 0, dataMat[:, i].A > 0))[0] 
        print 'users: %s' % users
        similarity = 0;
        if len(users) > 0: 
            similarity = similar(dataMat[users, item], dataMat[users, i])
        print 'the %d and %d similarity is: %f' % (item, i, similarity)
        totalSim += similarity
        totalUserSim += similarity * userScore
    if totalSim == 0: 
        return 0
    else :
        return totalUserSim / totalSim

#SVD降维后预估评分
def svdestimate(dataMat, user, item, similar):
    n = np.shape(dataMat)[1]
    totalSim = 0.0; totalUserSim = 0.0
    U, Sigma, VT = np.linalg.svd(dataMat)
    index = cutEigValue(Sigma)
    print 'sigma:%s index:%s' % (Sigma, index)
    #取前面4个特征值
    Sig = np.mat(np.eye(index) * Sigma[:index]) 
    newDataMat = dataMat.T * U[:, :index] * Sig.I  
    print 'newDataMat:%s' %newDataMat
    for i in range(n):
        userScore = dataMat[user, i]
        if userScore == 0 or i == item: continue
        similarity = similar(newDataMat[item, :].T, \
                             newDataMat[i, :].T)
        print 'the %d and %d similarity is: %f' % (item, i, similarity)
        totalSim += similarity
        totalUserSim += similarity * userScore
    if totalSim == 0: return 0
    else: return totalUserSim / totalSim
    
#推荐用户某项
def recommend(dataMat, user, N=3, similar=similarByConsine, estimateMethod=estimate):
    unScoredItems = np.nonzero(dataMat[user, :].A == 0)[1]
    if len(unScoredItems) == 0: return "not find unscored item"
    itemScores = []
    for item in unScoredItems:
        estimateScore = estimateMethod(dataMat, user, item, similar)
        itemScores.append((item, estimateScore))
    #print 'itemScores: %s' % itemScores
    return sorted(itemScores, key=lambda k : k[1], reverse=True)[:N]
    
if __name__ == '__main__':
    dataMat = np.mat([[4, 4, 0, 2, 2],
                      [4, 0, 0, 3, 3],
                      [4, 0, 0, 1, 1],
                      [1, 1, 1, 2, 0],
                      [2, 2, 2, 0, 0],
                      [1, 1, 1, 0, 0],
                      [5, 5, 5, 0, 0],
                      ])
    print dataMat
    print recommend(dataMat, 2)
    print recommend(dataMat, 2, estimateMethod=svdestimate)
    
