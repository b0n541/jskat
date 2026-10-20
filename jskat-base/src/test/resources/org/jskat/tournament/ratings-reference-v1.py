"""Independent numerical oracle: scalar bisection, no Newton/matrix implementation.
Run with Python 3; supplies literal rating fixtures for zero/one/two wins.
Bootstrap uses explicit unsigned 64-bit arithmetic and type-7 interpolation.
"""
import math
MASK=(1<<64)-1

def fit(n, wins):
    lo,hi=-4000,4000
    for _ in range(200):
        d=(lo+hi)/2
        p=1/(1+math.exp(-2*d*math.log(10)/400))
        derivative=d/400**2+math.log(10)/400*(n*p-wins)
        if derivative>0: hi=d
        else: lo=d
    return 1500+d

class SplitMix:
    def __init__(self,seed): self.state=seed
    def index(self,bound):
        while True:
            self.state=(self.state+0x9e3779b97f4a7c15)&MASK
            z=self.state
            z=((z^(z>>30))*0xbf58476d1ce4e5b9)&MASK
            z=((z^(z>>27))*0x94d049bb133111eb)&MASK
            bits=(z^(z>>31))>>1
            value=bits%bound
            if bits-value+bound-1 < 1<<63:return value

def percentile(values,q):
    at=(len(values)-1)*q
    i=int(at)
    return values[i]+(values[min(i+1,len(values)-1)]-values[i])*(at-i)

if __name__=='__main__':
    print('one win',fit(1,1))
    print('two wins',fit(2,2))
    rng=SplitMix(123)
    outcomes=[1,0,.5,1,1]
    samples=sorted(fit(5,sum(outcomes[rng.index(5)] for _ in outcomes)) for _ in range(5000))
    print('five epochs percentile interval',percentile(samples,.025),percentile(samples,.975))
    print('first ten indices', [SplitMix(seed).index(5) for seed in range(10)])
