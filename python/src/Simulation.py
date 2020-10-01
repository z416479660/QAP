import numpy as np
import os

from Instances import QAP
from Solutions import Assignment
from Methods import list_based_SA

def test_single_instance(file_name):
    TIMES = 5
    QAP.set_file_name(file_name)
    bkv = QAP.get_qap_instance().get_best_known_value()
    ms = []
    for t in range(TIMES):
        s = Assignment()
        m, match = list_based_SA(s, W=0.1)
        ms.append(m)
        print(str(t)+" : " + str(m))

    return stat_values(bkv, ms)

def test_multiple_instance(path):
    results = []
    for file in os.listdir(path):
        results.append(test_single_instance(path+file))
    return results

def stat_values(bkv, m):
    maxValue = max(m)
    minValue = min(m)
    avrValue = round(sum(m)/len(m),2)
    maxPE = round((maxValue-bkv)/bkv*100,4)
    minPE = round((minValue-bkv)/bkv*100,4)
    avrPE = round((avrValue-bkv)/bkv*100,4)
    return [bkv, maxValue, minValue, avrValue,maxPE, minPE, avrPE]

if __name__=="__main__":
##    file_name = "D:/SA4QAP/p32/20tai25a.txt"
##    results = test_single_instance(file_name)
##    print(results)

    path_name = "D:/SA4QAP/p32/"
    results = test_multiple_instance(path_name)
    print(results)
    
