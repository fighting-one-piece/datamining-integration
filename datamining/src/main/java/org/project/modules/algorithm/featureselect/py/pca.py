#-*- coding: utf-8 -*-

'''

'''
from numpy import *

def loadDataSet(fileName, delim='\t'):
    fr = open(fileName)
    stringArr = [line.strip().split(delim) for line in fr.readlines()]
    datArr = [map(float,line) for line in stringArr]
    return mat(datArr)

def pca(dataMat, topNfeat=9999999):
    print shape(dataMat)
    #计算平均值
    meanVals = mean(dataMat, axis=0)
    #数据去除平均值
    meanRemoved = dataMat - meanVals #remove mean
    #计算协方差
    covMat = cov(meanRemoved, rowvar=0)
    #获取特征值和特征向量
    eigVals,eigVects = linalg.eig(mat(covMat))
    print 'eigVects:%s' %eigVects
    #排序特征值
    eigValInd = argsort(eigVals)            #sort, sort goes smallest to largest
    #截取特征值，最大值开始
    print 'before eigValInd:%s' %eigValInd
    eigValInd = eigValInd[:-(topNfeat+1):-1]  #cut off unwanted dimensions
    print 'eigValInd:%s' %eigValInd
    #截取与特征值大小相同的列的特征向量
    redEigVects = eigVects[:,eigValInd]       #reorganize eig vects largest to smallest
    print 'redEigVects:%s' %redEigVects
    print shape(meanRemoved)
    print shape(redEigVects)
    lowDDataMat = meanRemoved * redEigVects #transform data into new dimensions
    reconMat = (lowDDataMat * redEigVects.T) + meanVals
    return lowDDataMat, reconMat

def replaceNanWithMean(): 
    datMat = loadDataSet('secom.data', ' ')
    numFeat = shape(datMat)[1]
    for i in range(numFeat):
        meanVal = mean(datMat[nonzero(~isnan(datMat[:,i].A))[0],i]) #values that are not NaN (a number)
        datMat[nonzero(isnan(datMat[:,i].A))[0],i] = meanVal  #set NaN values to mean
    return datMat

if __name__ == '__main__':
    dataMat = loadDataSet('testSet.txt')
    lowDDataMat, reconMat = pca(dataMat, 1)
    print shape(lowDDataMat)
    print shape(reconMat)