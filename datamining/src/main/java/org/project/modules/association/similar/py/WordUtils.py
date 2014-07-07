#-*- coding: utf-8 -*-
'''

'''

import jieba as ws

def split(input):
    seg_list = ws.cut(input, cut_all=False)
    words = []
    for word in seg_list:
        words.append(word)
    return words

def splitFile(path):
    file = open(path)
    words = []
    for line in file.readlines():
        line = line.strip();
        if len(line) > 0:
            for w in split(line):
                words.append(w)
    file.close()
    return words

def removeStopWords(words):
    file = open("stopwords.dic")
    stopwords = []
    for line in file.readlines():
        line = line.strip();
        if len(line) > 0:
            stopwords.append(line)
    file.close()
    rwords = []
    for word in words:
        flag = True
        for stopword in stopwords:
            if word == stopword:
                flag = False
                break
        if flag and len(word.strip()) > 0:
            rwords.append(word)
    return rwords

path = r"D:\resources\01-news-18828\alt.atheism\49960"
words = splitFile(path)
rwords = removeStopWords(words)
for word in rwords:
    print word

def example():
    seg_list = ws.cut("我参观北京清华大学",cut_all=True)
    print "Full Mode:", "/ ".join(seg_list) #全模式
    
    seg_list = ws.cut("我参观北京清华大学",cut_all=False)
    print "Default Mode:", "/ ".join(seg_list) #默认模式
    
        
    
