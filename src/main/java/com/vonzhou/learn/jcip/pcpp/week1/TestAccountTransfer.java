package com.vonzhou.learn.jcip.pcpp.week1;// For week 1
// sestoft@itu.dk * 2014-08-21

// Points: even when there is only one writing thread, both the write
// and read operations must be synchronized; atomic access is ensured
// only if all write and read operations are protected by the same
// lock; the volatile modifier may make field updates visible by other
// threads, but does not help atomic access. 

// This is a candidate to be implemented using transactional memory
// instead.

import java.util.Random;

class TestAccountTransfer {
  public static void main(String[] args) {
    // oneClerk();
    twoClerks();
  }

  private static void oneClerk() {
    final Random rnd = new Random();
    final Bank bank = new Bank();
    final int transfers = 10_000_000;
    final Thread clerk = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<transfers; i++) 
	    bank.transfer(rnd.nextInt(10000));
	}
      });
    clerk.start();
    for (int i=0; i<100; i++) 
      System.out.println(bank.getSum());
    try { clerk.join(); } catch (InterruptedException exn) { }
    System.out.println("Final sum is " + bank.getSum());
  }

  private static void twoClerks() {
    final Random rnd = new Random();
    final Bank bank = new Bank();
    final int transfers = 10_000_000;
    final Thread clerk1 = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<transfers; i++) 
	    bank.transfer(rnd.nextInt(10000));
	}
      });
    final Thread clerk2 = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<transfers; i++) 
	    bank.transfer(rnd.nextInt(10000));
	}
      });
    clerk1.start(); clerk2.start();
    try { clerk1.join(); clerk2.join(); } catch (InterruptedException exn) { }
    System.out.println("Final sum is " + bank.getSum());
  }
}

class Bank {
  private long account1 = 3000, account2 = 2000;
  // Transfer amount from account1 to account2
  public synchronized void transfer(long amount) {
    account1 -= amount; 
    account2 += amount;
  }
  public synchronized long getSum() { 
    return account1 + account2;
  }
}
