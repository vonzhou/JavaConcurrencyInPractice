package com.vonzhou.learn.jcip.pcpp.week2;// For week 2
// sestoft@itu.dk * 2014-08-19

// Points: A collection such as ArrayList, HashSet, LinkedList or
// TreeSet does not support concurrent updates (additions, deletions)
// and lookups; this may lose updates (and produce wrong results) or
// even destroy the internal integrity of the collection class (and
// throw obscure internal exceptions).

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

public class TestCollection {
  public static void main(String[] args) {
    final Collection<Integer> coll = new HashSet<Integer>();
    // final Collection<Integer> coll = Collections.synchronizedCollection(new HashSet<Integer>());
    final int itemCount = 100_000;
    Thread addEven = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<itemCount; i++) 
	    coll.add(2 * i);
	}
      });
    Thread addOdd = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<itemCount; i++) 
	    coll.add(2 * i + 1);
	}
      });
    addEven.start(); addOdd.start();
    // How many items in the collection when the adding is finished?
    try { addEven.join(); addOdd.join(); } catch (InterruptedException exn) { }
    System.out.println("There are " + coll.size() + " items, should be " + 2 * itemCount);
    
  }
}
