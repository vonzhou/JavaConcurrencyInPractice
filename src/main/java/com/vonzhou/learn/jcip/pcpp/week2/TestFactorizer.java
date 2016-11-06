package com.vonzhou.learn.jcip.pcpp.week2;// For week 2
// sestoft@itu.dk * 2014-08-28, 2014-09-10

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

class TestFactorizer {
  public static void main(String[] args) {
    // final Factorizer factorizer = new StatelessFactorizer();
    // final Factorizer factorizer = new UnsafeCountingFactorizer();
    // final Factorizer factorizer = new CountingFactorizer();
    // final Factorizer factorizer = new UnsafeCachingFactorizer();
    // final Factorizer factorizer = new TooSynchronizedCachingFactorizer();
    final Factorizer factorizer = new CachingFactorizer();
    // final Factorizer factorizer = new VolatileCachingFactorizer();
    final int range = 1_000_000;
    final int threadCount = 10;
    final Thread[] threads = new Thread[threadCount];
    for (int t=0; t<threadCount; t++) {
      threads[t] = 
	new Thread(new Runnable() { public void run() { 
	  for (int i=2; i<range; i++) {
	    long[] result = factorizer.getFactors(i);
	    if (!PrimeFactors.check(i, result))
	      throw new RuntimeException("CACHE FAILURE");
	    // Ignore result for now
	  }
	}});
      threads[t].start();
    }
    try {
      for (int t=0; t<threadCount; t++) 
	threads[t].join();
    } catch (InterruptedException exn) { }
    System.out.println("Number of calls: " + factorizer.getCount());
  }
}

class PrimeFactors {
  public static long[] compute(long p) {
    ArrayList<Long> factors = new ArrayList<Long>();
    long k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
	factors.add(k);
	p /= k;
      } else 
	k++;
    }
    // Now k * k > p and no number in 2..k divides p
    factors.add(p);
    long[] result = new long[factors.size()];
    for (int i=0; i<result.length; i++) 
      result[i] = factors.get(i);
    return result;
  }

  public static boolean check(long p, long[] factors) {
    long prod = 1;
    for (int i=0; i<factors.length; i++)
      prod *= factors[i];
    return p == prod;
  }
}

interface Factorizer {
  public long[] getFactors(long p);
  public long getCount();
}

// Like Goetz p. 18
class StatelessFactorizer implements Factorizer {
  public long[] getFactors(long p) {
    long[] factors = PrimeFactors.compute(p);
    return factors;
  }

  public long getCount() { 
    return 0; 
  }
}

// Like Goetz p. 19.  WARNING: Does not count correctly
class UnsafeCountingFactorizer implements Factorizer {
  private long count = 0;
  public long[] getFactors(long p) {
    long[] factors = PrimeFactors.compute(p);
    count++;
    return factors;
  }

  public long getCount() { 
    return count; 
  }
}

// Like Goetz p. 23
class CountingFactorizer implements Factorizer {
  private final AtomicLong count = new AtomicLong(0);
  public long[] getFactors(long p) {
    long[] factors = PrimeFactors.compute(p);
    count.incrementAndGet();
    return factors;
  }

  public long getCount() { 
    return count.get(); 
  }
}

// Like Goetz p. 24.  WARNING: Does not cache correctly
class UnsafeCachingFactorizer implements Factorizer {
  private final AtomicReference<Long> lastNumber =
    new AtomicReference<Long>();
  private final AtomicReference<long[]> lastFactors =
    new AtomicReference<long[]>();

  public long[] getFactors(long p) {
    if (lastNumber.get() != null && p == lastNumber.get())
      return lastFactors.get().clone();
    else {
      long[] factors = PrimeFactors.compute(p);
      lastNumber.set(p);
      lastFactors.set(factors);
      return factors;
    }
  }
  public long getCount() { return 0; }
}

// Like Goetz p. 26.  WARNING: Eliminates all parallelism
class TooSynchronizedCachingFactorizer implements Factorizer {
  private long lastNumber = 1;
  private long[] lastFactors = new long[0];

  public synchronized long[] getFactors(long p) {
    if (p == lastNumber)
      return lastFactors.clone();
    else {
      long[] factors = PrimeFactors.compute(p);
      lastNumber = p;
      lastFactors = factors;
      return factors;
    }
  }
  public long getCount() { return 0; }
}

// Like Goetz p. 31.
class CachingFactorizer implements Factorizer {
  private long lastNumber = 1;
  private long[] lastFactors = new long[0];

  public long[] getFactors(long p) {
    long[] factors = null;
    synchronized (this) {
      if (p == lastNumber)
	factors = lastFactors.clone();
    }
    if (factors == null) {
      factors = PrimeFactors.compute(p);
      synchronized (this) {
	lastNumber = p;
	lastFactors = factors.clone();
      }
    }
    return factors;
  }
  public long getCount() { return 0; }
}


// Like Goetz p. 50.
class VolatileCachingFactorizer implements Factorizer {
  private volatile OneValueCache cache 
    = new OneValueCache(1, new long[0]);

  public long[] getFactors(long p) {
    long[] factors = cache.getFactors(p);
    if (factors == null) {
      factors = PrimeFactors.compute(p);
      cache = new OneValueCache(p, factors);
    }
    return factors;
  }
  public long getCount() { return 0; }
}

// Like Goetz p. 49.
class OneValueCache {
  private final long lastNumber;
  private final long[] lastFactors;

  public OneValueCache(long p, long[] factors) {
    this.lastNumber = p;
    this.lastFactors = factors.clone();
  }
    
  public long[] getFactors(long p) {
    if (lastFactors == null || lastNumber != p)
      return null;
    else 
      return lastFactors.clone();
  }
}
