#-*- coding: utf-8 -*-
'''

'''
import math

class DistanceUtils:
    
    @staticmethod
    def cosine(v1, v2):
        if len(v1) != len(v2):
            print 'v1 len %s not equal v2 len %s' %(len(v1), len(v2))
        a = 0;b = 0;c = 0;
        for i in range(len(v1)):
            a += v1[i] * v2[i]
            b += v1[i]**2;
            c += v2[i]**2;
        return a / (math.sqrt(b) * math.sqrt(c))

    
if __name__ == '__main__':
    print 1
    
