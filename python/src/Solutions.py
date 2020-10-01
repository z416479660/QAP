import numpy as np
import math
import random
import copy

from Instances import QAP

class Assignment:
    '''
    '''
    def __init__(self):
        self.qap = QAP.get_qap_instance()
        self.match = np.arange(self.qap.get_city_number())
        random.shuffle(self.match)
        self.makespan = self.qap.evaluate(self.match)
        self.pairs_number = len(self.qap.pairs)

    @staticmethod
    def swap_neighbor_rand(instance, match):
        p = random.randint(0,instance.get_pairs_number()-1)
        return Assignment.swap_neighbor(instance, match, p)

    @staticmethod
    def swap_neighbor(instance, match, p):
        p %= instance.get_pairs_number()
        delta, i, j = instance.delta_p(match,p)
        return delta, i, j

    @staticmethod
    def swap(match, i, j):
        match[i], match[j] = match[j], match[i]
        return

    def get_pairs_number(self):
        return self.pairs_number

    def get_makespan(self):
        return self.makespan

    def get_match(self):
        return self.makespan

    def __str__(self):
        result = str(self.makespan) + "\n"
        for t in self.match:
            result += str(t) + " "

        return result

if __name__=="__main__":
    file_name = "D:/SA4QAP/p32/20tai25a.txt"
    QAP.set_file_name(file_name)
    s = Assignment()
    print(s)

##    makespan, match = s.local_search()
##    print(makespan)
##    print(match)
    
    
        
        
