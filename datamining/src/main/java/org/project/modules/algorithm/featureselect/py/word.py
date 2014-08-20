#-*- coding: utf-8 -*-
'''

'''
import re
import jieba as ws

#词工具类
class WordUtils:

    #对文本进行分词
    @staticmethod
    def split(input):
        seg_list = ws.cut(input, cut_all=False)
        words = []
        for word in seg_list:
            if len(word) < 2 or re.match(r'\d+', word): continue
            words.append(word)
        return words
    
    #对文件进行分词
    @staticmethod
    def splitFile(path):
        file = open(path)
        words = []
        for line in file.readlines():
            line = line.strip();
            if len(line) > 0:
                for w in WordUtils.split(line):
                    words.append(w)
        file.close()
        return WordUtils.removeStopWords(words)
    
    #根据停用词文件移除词集中的停用词
    @staticmethod
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
                #if word.encode('utf-8') == stopword.encode('utf-8'):
                if word == stopword:
                    flag = False
                    break
            if flag and len(word.strip()) > 0:
                rwords.append(word)
        return rwords
    
    #词汇总并且去除重复
    @staticmethod
    def mergeAndRemoveRepeat(w1, w2):
        all = [i1 for i1 in w1]
        all += [i2 for i2 in w2]
        return [i for i in set(all)]
        #下面这种方式也可以 
        #all = set(l1) | set(l2)
        #return [i for i in all] 
        
def example():
    path = r"D:\resources\trainset\5.txt"
    words = WordUtils.splitFile(path)
    #rwords = WordUtils.removeStopWords(words)
    for word in words:
        print word

def example1():
    seg_list = ws.cut("我参观北京清华大学",cut_all=True)
    print "Full Mode:", "/ ".join(seg_list) #全模式
    
    seg_list = ws.cut("我参观北京清华大学",cut_all=False)
    print "Default Mode:", "/ ".join(seg_list) #默认模式
    
if __name__ == '__main__':
    example()
    
