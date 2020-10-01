import numpy as np
import math
from functools import reduce

class QAP:
    '''
    '''

    qap_instance = None
    file_name = None
    def __init__(self, file_name):
        QAP.file_name = file_name
        
        file_handle = open(file_name, mode='r')
        print(file_handle.readline()) #ignore the first line
        self.city_number = int(file_handle.readline())
        print(self.city_number)
        self.best_known_value = int(file_handle.readline())
        print(self.best_known_value)
        
        self.distance = np.zeros((self.city_number,self.city_number), dtype='int32')  #to store distance
        i = 0
        j = 0
        k = 0
        while k < self.city_number * self.city_number:
            data = file_handle.readline().strip()
            while data == "":
                data = file_handle.readline().strip()
            data = data.split()
            for d in data:
                self.distance[i,j] = int(d)
                i = i if j < self.city_number-1 else i+1
                j = j+1 if j < self.city_number-1 else 0
                k += 1

        self.flow = np.zeros((self.city_number,self.city_number), dtype='int32')  #to store flow
        i = 0
        j = 0
        k = 0
        while k < self.city_number * self.city_number:
            data = file_handle.readline().strip()
            while data == "":
                data = file_handle.readline().strip()
            data = data.split()
            for d in data:
                self.flow[i,j] = int(d)
                i = i if j < self.city_number-1 else i+1
                j = j+1 if j < self.city_number-1 else 0
                k += 1
        
        file_handle.close()

        self.pairs = QAP.produce_pairs(self.city_number)

    @staticmethod
    def produce_pairs(n):
        pairs = []
        for i in range(n-1):
            for j in range(i+1, n):
                pairs.append((i,j))

        return pairs

    def evaluate(self, match):
        makespan = 0
        for i in range(self.city_number):
            task1 = match[i]
            for j in range(self.city_number):
                if j != i:
                    task2 = match[j]
                    makespan += self.distance[i,j]*self.flow[task1,task2]

        return makespan

    def delta_p(self, m, p):
        i = self.pairs[p][0]
        j = self.pairs[p][1]
        return self.delta(m, i, j)

    def delta(self, m, i, j):
        dist = self.distance
        flow = self.flow

        diff = (dist[i,i]- dist[j,j]) * (flow[m[j],m[j]]-flow[m[i],m[i]])
        diff += (dist[i,j]- dist[j,i]) * (flow[m[j],m[i]]-flow[m[i],m[j]])
        for k in range(len(m)):
            if k!=i and k!=j:
                diff += (dist[k,j]- dist[k,i]) * (flow[m[k],m[i]]-flow[m[k],m[j]])
                diff += (dist[j,k]- dist[i,k]) * (flow[m[i],m[k]]-flow[m[j],m[k]])
        return diff, i, j


    def __str__(self):
        result = QAP.file_name + "\n"
        result = result + str(self.city_number) + "\t" + str(self.best_known_value) + "\n"
        for row in self.distance: 
            result = result + reduce(lambda x, y: str(x) +" " + str(y), row) + "\n"

        result = result + "\n"
        for row in self.flow:
           result = result + reduce(lambda x, y: str(x) +" " + str(y), row) + "\n"

        return result

    def get_distance(self):
        return self.distance

    def get_distance(self, i, j):
        return self.distance[i,j]

    def get_flow(self):
        return self.flow

    def get_flow(self, i, j):
        return self.flow[i,j]

    def get_city_number(self):
        return self.city_number

    def get_best_known_value(self):
        return self.best_known_value

    def get_pairs_number(self):
        return len(self.pairs)

    @classmethod
    def get_qap_instance(cls):
        if cls.qap_instance is not None:
            return cls.qap_instance
        elif cls.file_name is not None:
            cls.qap_instance = QAP(cls.file_name)
            return cls.qap_instance
        else:
            return None

    @classmethod
    def set_file_name(cls, file_name):
        cls.file_name = file_name
        cls.qap_instance = None



if __name__=="__main__":
    file_name = "D:/SA4QAP/p32/20tai25a.txt"
    #qap_instance = QAP(file_name)
    #print(qap_instance)
    QAP.set_file_name(file_name)
    inst = QAP.get_qap_instance()
    print(inst)

    pairs = QAP.produce_pairs(10)
    for p in pairs:
        print(p)

 
