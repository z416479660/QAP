import numpy as np
import math
import random
import copy
#from numba import jit

from Instances import QAP
from Solutions import Assignment

def local_search(assi):
    instance = assi.qap
    match = copy.copy(assi.match)
    makespan = assi.makespan
    improved = True
    while improved:
        improved = False
        minDelta = makespan
        x = -1
        y = -1
        for p in range(instance.get_pairs_number()):
            delta, i, j = Assignment.swap_neighbor(instance,match,p)
            if delta < minDelta:
                minDelta = delta
                x = i
                y = j
        if minDelta < 0:
            Assignment.swap(match,x,y)
            makespan += minDelta
            improved = True
    return makespan, match

def tabu_search(assi, M):
    instance = assi.qap
    match = copy.copy(assi.match)
    makespan = assi.makespan
    city_number = instance.get_city_number()
    pairs_number = instance.get_pairs_number()

    #best solution
    bMatch = copy.copy(match)
    bMakespan = makespan

    delta = np.zeros((city_number-1, city_number),dtype="int32")
    tabu = delta < 0

    for p in range(pairs_number):
        d,x,y = Assignment.swap_neighbor(instance,match, p)
        delta[x][y] = d    

    for m in range(M):
        minDelta = makespan
        x = -1
        y = -1
        for p in range(pairs_number):
            d,i,j = Assignment.swap_neighbor(instance,match,p)
            if d < minDelta and (not tabu[i][j] or d + makespan < bMakespan):
                minDelta = d
                x = i
                y = j
      
        Assignment.swap(match,x,y)
        makespan += minDelta
        tabu[x][y] = True
        if makespan < bMakespan:
            bMakespan = makespan
            bMatch = copy.copy(match)

        for p in range(pairs_number):
            d,x,y = Assignment.swap_neighbor(instance,match, p)
            delta[x][y] = d

    return bMakespan, bMatch

#@jit
def list_based_SA(assi, M=1000, B=1, L=200, W=1.0):
    instance = assi.qap
    match = copy.copy(assi.match)
    makespan = assi.makespan
    city_number = instance.get_city_number()
    pairs_number = instance.get_pairs_number()
    K = city_number*(city_number-1)/2

    #best solution
    bMatch = copy.copy(match)
    bMakespan = makespan
    
    #initial temperature lsit
    tq = produce_temperature_list(assi, L, W)
    rejected = 0
    p = -1
    for m in range(M):
        t = - tq.get()
        #print(t)
        totalT = 0
        count = 0
        k = 0
        while k < K:
            block = B if K-k>=B else K-k
            k += block
            p = (p+1) % pairs_number
            minD,minX,minY = Assignment.swap_neighbor(instance,match, p)
            block -= 1
            for b in range(block):
                p = (p+1) % pairs_number
                d,x,y = Assignment.swap_neighbor(instance,match, p)
                if d < minD:
                    minD, minX, minY = d, x, y

            r = random.random()
            if minD<0 or r < math.pow(math.e, -minD/t):
                makespan += minD
                match[minX],match[minY] = match[minY],match[minX]
                if makespan < bMakespan:
                    bMatch = copy.copy(match)
                    bMakespan = makespan
                if minD > 0:
                    totalT += minD / math.log(1.0/r)
                    count += 1
                rejected = 0
            else:
                rejected += 1
        #update temperature list
        if count > 0:
            tq.put(-totalT / count)
        else:
            tq.put(-t)

    return bMakespan, bMatch

import queue
def produce_temperature_list(assi, length = 200, weight = 1.0):
    instance = assi.qap
    match = assi.match
    pairs_number = instance.get_pairs_number()
    q = queue.PriorityQueue(length)
    for i in range(length):
        p = random.randint(0,pairs_number-1)
        d,x,y = Assignment.swap_neighbor(instance,match, p)
        q.put( weight*d if d < 0 else -weight*d )
    return q

def modified_SA():
    pass

if __name__=="__main__":
    file_name = "D:/SA4QAP/p32/20tai25a.txt"
    QAP.set_file_name(file_name)
    s = Assignment()
    print(s)

    makespan, match = list_based_SA(s, W=0.1)
    print(makespan)
    print(match)
