#-*- coding: utf-8 -*-

import os
import math
from doc import *
from word import *
from distance import *

#文档操作工具类        
class FeatureSelectUtils:
    
    @staticmethod
    def calculateTF(doc):
        tf = {}
        for word in doc.getWords():
            if tf.has_key(word):
                tf[word] = tf.get(word) + 1
            else:
                tf[word] = 1
        return tf
    
    #计算TFIDF
    @staticmethod
    def calculateTFIDF(docs):
        docTotalCount = float(len(docs))
        for doc in docs:
            wordTotalCount = len(doc.getWords())
            tfidfWords = {}
            docWords = DocHelper.docWordsStatistics(doc)
            for word in docWords.keys():
                wordCount = docWords.get(word)
                tf = float(wordCount) / wordTotalCount
                docCount = DocHelper.wordInDocsStatistics(word, docs) + 1
                if docCount > docTotalCount:
                    docCount = docTotalCount
                idf = math.log(docTotalCount / docCount);
                tfidf = tf * idf
                tfidfWords[word] = tfidf
            doc.setTfidfWords(tfidfWords)
            
    #根据开方检验特征选择算法计算文档集中各个文档的词与类别的开方值
    @staticmethod
    def calculateCHI(docs):
        docTotalCount = len(docs)
        for doc in docs:
            chiWords = {}
            words = doc.getWords()
            belongDocs,nobelongDocs = DocHelper.categorySplit(\
                doc.getCategory(), docs)
            for word in words:
                a = DocHelper.wordInDocsStatistics(word, belongDocs)
                b = DocHelper.wordInDocsStatistics(word, nobelongDocs)
                c = DocHelper.wordNotInDocsStatistics(word, belongDocs)
                d = DocHelper.wordNotInDocsStatistics(word, nobelongDocs)
                x = float((a*d-b*c)**2) / ((a+b)*(c+d))
                chiWords[word] = x
            doc.setCHIWords(chiWords)
            
    #根据信息增益特征选择算法计算文档集中词的信息增益
    @staticmethod
    def calculateInformationGain(docs):
        docTotalCount = len(docs)
        splits = DocHelper.docCategorySplit(docs)
        categories = []
        pcSum = 0
        for item in splits.items():
            categories.append(item[0])
            categoryCount = float(len(item[1]))
            pc = categoryCount / docTotalCount
            pcSum += pc * (math.log(pc) / math.log(2))
        words = []
        for doc in docs:
            words += [i for i in doc.getWords()]
        wordDict = {}
        for word in words:
            belongDocs,nobelongDocs = DocHelper.wordDocsSplit(word, docs)
            wordInDocsCount = len(belongDocs)
            wordNotInDocsCount = len(nobelongDocs)
            pctSum = 0;pcntSum = 0
            for category in categories:
                ctCount = len(DocHelper.categorySplit(category, belongDocs)[0])
                pct = float(ctCount) / wordInDocsCount
                if pct != 0:
                    pctSum += pct * (math.log(pct) / math.log(2))
                cntCount = len(DocHelper.categorySplit(category, nobelongDocs)[0])
                if cntCount != 0:
                    pcnt = float(cntCount) / wordNotInDocsCount
                    if pcnt != 0:
                        pcntSum += pcnt * (math.log(pcnt) / math.log(2))
            pt = float(wordInDocsCount) / docTotalCount
            pnt = float(wordNotInDocsCount) / docTotalCount
            ig = -pcSum + pt * pctSum + pnt * pcntSum
            wordDict[word] = ig
        return DocHelper.sortWordValueMap(wordDict)
    
    #计算文档集中词的交叉期望熵
    @staticmethod
    def calculateKL(docs):
        docTotalCount = len(docs)
        allWords = []
        categories = []
        cateToCount = {}
        wordToCount = {}
        for doc in docs:
            cate = doc.getCategory()
            categories.append(cate)
            cateCount = cateToCount.get(cate)
            if cateCount is None:
                cateToCount[cate] = 1
            else:
                cateToCount[cate] = cateCount + 1 
            words = doc.getWords()
            for word in words:
                allWords.append(word)
                count = wordToCount.get(word)
                if count is None:
                    wordToCount[word] = 1
                else :
                    wordToCount[word] = count + 1
        allWords = set(allWords)
        categories = set(categories)
        wordDict = {}
        word_len = len(allWords)
        for word in allWords:
            pt = float(wordToCount.get(word)) / word_len
            sum = 0; cd = 0; dd = 0
            nt = DocHelper.wordInDocsStatistics(word, docs)
            for category in categories:
                cateCount = cateToCount.get(category)
                pc = float(cateCount) / docTotalCount
                pct = DocHelper.wordCategoryInDocsPercent(word, category, docs)
                sum += pct * math.log(pct / pc)
                nct = DocHelper.categoryWordStatistics(category, word, docs)
                cd += float(nct) / nt
                dd += float(nct) / cateCount
            wordDict[word] = cd * dd * pt * sum
        return DocHelper.sortWordValueMap(wordDict)    
                

            
def testCHI():
    path = r'D:\resources\test'
    docs = DocHelper.genDocs(path)
    FeatureSelectUtils.calculateCHI(docs)
    for doc in docs:
        print '----------'
        for item in DocHelper.sortWordValueMap(doc.getCHIWords())[0:10]:
            print '%s-%s' %(item[0],item[1])
                
def testInformationGain():
    path = r'D:\resources\test'
    docs = DocHelper.genDocs(path)
    wordDict = FeatureSelectUtils.calculateInformationGain(docs)
    for item in wordDict[0:30]:
        print '%s-%s' %(item[0],item[1])
    
def testKL():
    path = r'D:\resources\test'
    docs = DocHelper.genDocs(path)
    wordDict = FeatureSelectUtils.calculateKL(docs)
    for item in wordDict[0:30]:
        print '%s-%s' %(item[0],item[1])

if __name__ == '__main__':
    print '-----CHI-----' 
    testCHI()
    print '-----IG-----' 
    testInformationGain()
    print '-----KL-----' 
    testKL()
    print '----------' 
