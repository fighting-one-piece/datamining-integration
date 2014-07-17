#-*- coding: utf-8 -*-

import os
from doc import *
from word import *

def testSplitWord():
    path = r'D:\resources\chinese'
    docs = DocHelper.genDocs(path)
    for doc in docs:
        print '----------'
        for word in doc.getWords():
            print word

def testSimilarity(): 
    path = r'D:\resources\chinese'
    docs = DocHelper.genDocs(path)
    DocHelper.calculateTFIDF(docs)
    DocHelper.calculateSimilar(docs)
    for doc in docs:
        print '----------'
        for similarity in doc.getSimilarities():
            print '%s-%s-%s' %(similarity.getName1(),\
                    similarity.getName2(), similarity.getCosine())
            
def testCHI():
    path = r'D:\resources\chinese'
    docs = DocHelper.genDocs(path)
    DocHelper.calculateCHI(docs)
    for doc in docs:
        print '----------'
        for item in DocHelper.sortWordValueMap(doc.getCHIWords())[0:10]:
            print '%s-%s' %(item[0],item[1])
                
def testInformationGain():
    path = r'D:\resources\chinese'
    docs = DocHelper.genDocs(path)
    wordDict = DocHelper.calculateInformationGain(docs)
    for item in wordDict[0:30]:
        print '%s-%s' %(item[0],item[1])

if __name__ == '__main__':
    testCHI()
    print '----------' 
    testInformationGain()

    
