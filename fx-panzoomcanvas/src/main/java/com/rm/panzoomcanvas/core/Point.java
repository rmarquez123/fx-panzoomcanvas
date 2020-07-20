package com.rm.panzoomcanvas.core;

/**
 *
 * @author rmarquez
 */
public final class Point {

  public static PointArrays toArrays(Point... points ) {
    int length = points.length;
    double[] xarray = new double[length];
    double[] yarray = new double[length];
    for (int i = 0; i < length; i++) {
      xarray[i] = points[i].x;
      yarray[i] = points[i].y;
    }
    PointArrays result = new PointArrays(xarray, yarray, length);
    return result;
  }

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
   *
   * @param radians
   * @return
   */
  public Point rotate(double radians) {
    double newx = this.x * Math.cos(radians) - this.y * Math.sin(radians);
    double newy = this.x * Math.sin(radians) + this.y * Math.cos(radians);
    Point result = new Point(newx, newy);
    return result;

  }

  /**
   *
   * @param point
   * @return
   */
  public Point add(Point point) {
    double newx = this.x + point.x;
    double newy = this.y + point.y;
    Point result = new Point(newx, newy);
    return result;
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
