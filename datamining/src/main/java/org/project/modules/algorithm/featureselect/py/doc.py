#-*- coding: utf-8 -*-

import os
import math
from word import *
from distance import *

class Doc:
    
    def __init__(self, name):
        self._name = name
     
    def setName(self, name):
        self._name = name
    
    def getName(self):
        return self._name
    
    def setCategory(self, category):
        self._category = category
        
    def getCategory(self):
        return self._category
        
    def setWords(self, words):
        self._words = words
        
    def getWords(self):
        return self._words
    
    def setTfidfWords(self, tfidfWords):
        self._tfidfWords = tfidfWords
        
    def getTfidfWords(self):
        return self._tfidfWords
    
    def getSortedTfidfWords(self):
        results = [sorted(self._tfidfWords.items(), key=lambda i : i[1], reverse=True), ]
        return results
    
    def setCHIWords(self, chiWords):
        self._chiWords = chiWords
        
    def getCHIWords(self):
        return self._chiWords

    def setSimilarities(self, similarities):
        self._similarities = similarities
        
    def getSimilarities(self):
        return self._similarities
    
class DocSimilarity:
    
    def getName1(self):
        return self._name1

    def setName1(self, name1):
        self._name1 = name1
        
    def getName2(self):
        return self._name2

    def setName2(self, name2):
        self._name2 = name2
    
    def getVector1(self):
        return self._vector1
    
    def setVector1(self, vector1):
        self._vector1 = vector1
        
    def getVector2(self):
        return self._vector2
    
    def setVector2(self, vector2):
        self._vector2 = vector2
        
    def getCosine(self):
        return self._cosine
        
    def setCosine(self, cosine):
        self._cosine = cosine
    
#文档操作工具类        
class DocHelper:
    
    #获取目录下所有的文档
    @staticmethod
    def genDocs(path):
        docs = []
        DocHelper.genDocsIterator(path, docs)
        return docs
    
    #遍历目录获取目录下所有的文档
    @staticmethod
    def genDocsIterator(path, docs):
        if os.path.isdir(path):
            for subPathName in os.listdir(path):
                subPath = os.path.join(path, subPathName)
                DocHelper.genDocsIterator(subPath, docs)
        else:
            name = path[path.rfind('\\') + 1 : path.rfind('.')]
            doc = Doc(name)
            doc.setCategory(path.split('\\')[-2])
            doc.setWords(WordUtils.splitFile(path));
            docs.append(doc)
    
    #文档中是否包含指定词
    @staticmethod
    def docHasWord(doc, word):
        for dword in doc.getWords():
            if dword == word:
                return True
        return False
    
    #文档中词频统计
    @staticmethod
    def docWordsStatistics(doc):
        map = {}
        for word in doc.getWords():
            count = map.get(word)
            if count is None:
                count = 0
            map[word] = count + 1
        return map
    
    #根据文档所属类型分割文档集
    @staticmethod
    def docCategorySplit(docs):
        docSplits = {}
        for doc in docs:
            category = doc.getCategory()
            if docSplits.has_key(category):
                cDocs = docSplits.get(category)
                cDocs.append(doc)
            else :
                cDocs = [doc]
                docSplits[category] = cDocs
        return docSplits
    
    #根据TFIDF取文档排行前N的词
    @staticmethod
    def docTopNWords(doc, n):
        sortedWords = DocHelper.sortWordValueMap(doc.getTfidfWords())
        words = []
        for item in sortedWords[0:n]:
            words.append(item[0])
        return words
                    
    #文档中词的向量化
    @staticmethod
    def docWordsVector(doc, words):
        vector = []
        docWords = DocHelper.docWordsStatistics(doc)
        for word in words:
            count = docWords.get(word)
            if count is None:
                vector.append(0)
            else :
                vector.append(count)
        return vector
    
    #根据词所属文档类型获取同类文档集和非同类文档集
    @staticmethod
    def categorySplit(category, docs):
        belongDocs = []
        nobelongDocs = []
        for doc in docs:
            if category == doc.getCategory():
                belongDocs.append(doc)
            else:
                nobelongDocs.append(doc)
        return belongDocs, nobelongDocs
    
    #根据词所属文档类型获取同类文档集数量
    @staticmethod
    def categoryStatistics(category, docs):
        sum = 0
        for doc in docs:
            if category == doc.getCategory():
                sum = sum + 1
        return sum

    #根据词所属文档类型获取文档集中含有词数量
    @staticmethod
    def categoryWordStatistics(category, word, docs):
        sum = 0
        for doc in docs:
            if category == doc.getCategory() and \
                DocHelper.docHasWord(doc, word):
                sum = sum + 1
        return sum
    
    #获取包含词文档集和非包含词文档集
    @staticmethod
    def wordDocsSplit(word, docs):
        belongDocs = []
        nobelongDocs = []
        for doc in docs:
            flag = False
            for dword in doc.getWords():
                if word == dword:
                    flag = True
                    belongDocs.append(doc)
                    break;
            if flag == False:        
                nobelongDocs.append(doc)
        return belongDocs, nobelongDocs
    
    #统计文档集包含词的文档数
    @staticmethod
    def wordInDocsStatistics(word, docs):
        sum = 0
        for doc in docs:
            if DocHelper.docHasWord(doc, word):
                sum += 1
        return sum

    #统计文档集不包含词的文档数
    @staticmethod
    def wordNotInDocsStatistics(word, docs):
        sum = 0
        for doc in docs:
            if DocHelper.docHasWord(doc, word) == False:
                sum += 1
        return sum
    
    #文档集包含词的文档数所属类型在包含次文档数中的概率
    @staticmethod
    def wordCategoryInDocsPercent(word, category, docs):
        sumWord = 0
        sumCategory = 0
        for doc in docs:
            if DocHelper.docHasWord(doc, word):
                sumWord += 1
            if category == doc.getCategory():
                sumCategory += 1
        return float(sumCategory) / sumWord
    
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
                x = float((a*d-b*c)**2)/ ((a+b)*(c+d))
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
                
            
    #计算文档集之间的相似度    
    @staticmethod
    def calculateSimilar(docs):
        for doc in docs:
            topWords = DocHelper.docTopNWords(doc, 20)
            similarities = []
            for odoc in docs:
                otopWords = DocHelper.docTopNWords(odoc, 20)
                words = WordUtils.mergeAndRemoveRepeat(topWords, otopWords);
                v1 = DocHelper.docWordsVector(doc, words)
                v2 = DocHelper.docWordsVector(odoc, words)
                cosine = DistanceUtils.cosine(v1,v2)
                similarity = DocSimilarity()
                similarity.setName1(doc.getName())
                similarity.setName2(odoc.getName())
                similarity.setVector1(v1)
                similarity.setVector2(v2)
                similarity.setCosine(cosine)
                similarities.append(similarity)
            doc.setSimilarities(similarities)
    
    #根据字典的value字段倒排序            
    @staticmethod
    def sortWordValueMap(wordValueMap):
        results = sorted(wordValueMap.items(), key=lambda i : i[1], reverse=True)
        return results
    
if __name__ == '__main__':
    doc = Doc('doc1')
    doc.setWords(['hadoop', 'hbase', 'hive', 'hadoop'])
    map = DocHelper.docWordsStatistics(doc)
    print map
    print DocHelper.docHasWord(doc, 'hive')
    print DocHelper.docHasWord(doc, 'spark')
    doc2 = Doc('doc2')
    doc2.setWords(['hadoop', 'hbase', 'hive', 'hadoop'])
    docs = [doc, doc2]
    print DocHelper.wordInDocsStatistics('hive', docs)