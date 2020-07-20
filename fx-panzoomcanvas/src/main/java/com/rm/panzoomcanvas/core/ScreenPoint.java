package com.rm.panzoomcanvas.core;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 *
 * @author rmarquez
 */
public class ScreenPoint {

  private final double x;
  private final double y;

  /**
   *
   * @param x
   * @param y
   */
  public ScreenPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  /**
   *
   * @param other
   * @return
   */
  public ScreenPoint difference(ScreenPoint other) {
    return new ScreenPoint(this.x - other.x, this.y - other.y);
  }
  
  /**
   * 
   * @param other
   * @return 
   */
  public double distance(ScreenPoint other) {
    return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)); 
  }

  /**
   *
   * @param other
   * @return
   */
  public ScreenPoint add(ScreenPoint other) {
    return new ScreenPoint(this.x + other.x, this.y + other.y);
  }

  /**
   *
   * @param factor
   * @param other
   * @return
   */
  public ScreenPoint multiply(double factor) {
    return new ScreenPoint(this.x * factor, this.y * factor);
  }
  /**
   *
   * @param other
   * @param buffer
   * @return
   */
  public boolean intesects(ScreenPoint other, int buffer) {
    GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
    Point thisJtsPoint = factory.createPoint(new Coordinate(this.x, this.y));
    Point otherJtsPoint = factory.createPoint(new Coordinate(other.x, other.y));
    boolean result = thisJtsPoint.buffer(buffer).intersects(otherJtsPoint.buffer(buffer));
    return result;
  }
  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return "ScreenPoint{" + "x=" + x + ", y=" + y + '}';
  }

  



}
