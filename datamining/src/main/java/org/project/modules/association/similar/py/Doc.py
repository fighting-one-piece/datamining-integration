#-*- coding: utf-8 -*-

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
        
class DocHelper:
    
    @staticmethod
    def docHasWord(doc, word):
        for dword in doc.getWords():
            if dword == word:
                return True
        return False
    
    @staticmethod
    def docWordsStatistics(doc):
        map = {}
        for word in doc.getWords():
            count = map.get(word)
            if count is None:
                count = 0
            map[word] = count + 1
        return map
    
    @staticmethod
    def wordInDocsStatistics(word, docs):
        sum = 0
        for doc in docs:
            if DocHelper.docHasWord(doc, word):
                sum += 1
        return sum
    
class DocOperation:
    
    @staticmethod
    def calculateTFIDF(docs):
        print 1
        
    @staticmethod
    def calculateSimilar(docs):
        print 1
        
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