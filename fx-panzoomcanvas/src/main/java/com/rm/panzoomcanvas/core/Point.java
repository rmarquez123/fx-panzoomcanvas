package com.rm.panzoomcanvas.core;

/**
 *
 * @author rmarquez
 */
public final class Point {
  
  private final double x, y;

  /**
   *
   * @param x
   * @param y
   */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   *
   * @return
   */
  public double getX() {
    return x;
  }

  /**
   *
   * @return
   */
  public double getY() {
    return y;
  }
  
  /**
   * 
   * @param x
   * @return 
   */
  public Point plusX(double x) {
    return new Point(this.x + x, this.y); 
  }
  /**
   * 
   * @param x
   * @return 
   */
  public Point plusY(double y) {
    return new Point(this.x, this.y + y); 
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public String toString() {
    return "Point{" + "x=" + x + ", y=" + y + '}';
  }

  
  

}
