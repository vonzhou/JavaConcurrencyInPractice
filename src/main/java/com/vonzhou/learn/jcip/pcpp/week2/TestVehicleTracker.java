package com.vonzhou.learn.jcip.pcpp.week2;// For week 2

// Code from Goetz et al 5.6, written by Brian Goetz and Tim Peierls.
// Minor modifications by sestoft@itu.dk * 2014-09-02

import java.util.*;
import java.util.concurrent.*;


public class TestVehicleTracker {
  public static void main(String[] args) {


  }
}


/**
 * Mutable Point class similar to mutable java.awt.Point
 * @author Brian Goetz and Tim Peierls
 */

class MutablePoint {
  public int x, y;
  public MutablePoint() {
    x = 0; y = 0;
  }
  public MutablePoint(MutablePoint p) {
    this.x = p.x; this.y = p.y;
  }
}


/**
 * Monitor-based vehicle tracker implementation
 * @author Brian Goetz and Tim Peierls
 */

class MonitorVehicleTracker {
  private final Map<String, MutablePoint> locations;
  public MonitorVehicleTracker(Map<String, MutablePoint> locations) {
    this.locations = deepCopy(locations);
  }
  public synchronized Map<String, MutablePoint> getLocations() {
    return deepCopy(locations);
  }
  public synchronized MutablePoint getLocation(String id) {
    MutablePoint loc = locations.get(id);
    return loc == null ? null : new MutablePoint(loc);
  }
  public synchronized void setLocation(String id, int x, int y) {
    MutablePoint loc = locations.get(id);
    loc.x = x;
    loc.y = y;
  }
  private static Map<String, MutablePoint> deepCopy(Map<String, MutablePoint> m) {
    Map<String, MutablePoint> result = new HashMap<String, MutablePoint>(); 
    for (String id : m.keySet())
      result.put(id, new MutablePoint(m.get(id)));  
    return Collections.unmodifiableMap(result);
  }
}


/**
 * Immutable Point class used by DelegatingVehicleTracker
 * @author Brian Goetz and Tim Peierls
 */
class Point {
  public final int x, y;  
  public Point(int x, int y) {
    this.x = x; this.y = y;
  }
}


/**
 * Delegating thread safety to a ConcurrentHashMap, exposing it as an
 * unmodifiable Map, and using immutable points.
 * @author Brian Goetz and Tim Peierls
 */

class DelegatingVehicleTracker {
  private final ConcurrentMap<String, Point> locations;
  private final Map<String, Point> unmodifiableMap;
  
  public DelegatingVehicleTracker(Map<String, Point> points) {
    locations = new ConcurrentHashMap<String, Point>(points);
    unmodifiableMap = Collections.unmodifiableMap(locations);
  }

  public Map<String, Point> getLocations() {
    return unmodifiableMap;
  }

  public Point getLocation(String id) {
    return locations.get(id);
  }

  public void setLocation(String id, int x, int y) {
    if (locations.replace(id, new Point(x, y)) == null)
      throw new IllegalArgumentException("invalid vehicle name: " + id);
  }

  // Alternate version of getLocations (Listing 4.8)
  public Map<String, Point> getLocationsAsSnapshot() {
    return Collections.unmodifiableMap(new HashMap<String, Point>(locations));
  }
}

/**
 * @author Brian Goetz and Tim Peierls
 */
class SafePoint {
  private int x, y;
  private SafePoint(int[] a) { this(a[0], a[1]); }
  public SafePoint(SafePoint p) { this(p.get()); }
  public SafePoint(int x, int y) { this.set(x, y); }
  public synchronized int[] get() {
    return new int[]{x, y};
  }
  public synchronized void set(int x, int y) {
    this.x = x; this.y = y;
  }
}


/**
 * Vehicle tracker that safely publishes underlying state
 * @author Brian Goetz and Tim Peierls
 */
class PublishingVehicleTracker {
  private final Map<String, SafePoint> locations;
  private final Map<String, SafePoint> unmodifiableMap;

  public PublishingVehicleTracker(Map<String, SafePoint> locations) {
    this.locations = new ConcurrentHashMap<String, SafePoint>(locations);
    this.unmodifiableMap = Collections.unmodifiableMap(this.locations);
  }

  public Map<String, SafePoint> getLocations() {
    return unmodifiableMap;
  }

  public SafePoint getLocation(String id) {
    return locations.get(id);
  }

  public void setLocation(String id, int x, int y) {
    locations.get(id).set(x, y);
  }
}
