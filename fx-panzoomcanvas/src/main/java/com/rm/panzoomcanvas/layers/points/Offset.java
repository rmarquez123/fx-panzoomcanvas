package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.core.Point;

/**
 *
 * @author Ricardo Marquez
 */
public class Offset {

  private final double north;
  private final double east;
  private final double south;
  private final double west;

  private Offset(double north, double east, double south, double west) {
    this.north = north;
    this.east = east;
    this.south = south;
    this.west = west;
  }
  
  /**
   * 
   * @param x1
   * @param y1
   * @return 
   */
  Point getPosition(double x1, double y1) {
    Point point = new Point(x1, y1);
    if (!Double.isNaN(this.north)) {
      point = point.plusY(-this.north); 
    }
    if (!Double.isNaN(this.east)) {
      point = point.plusX(this.east); 
    }
    if (!Double.isNaN(this.south)) {
      point = point.plusY(this.south); 
    }
    if (!Double.isNaN(this.west)) {
      point = point.plusX(-this.west); 
    }
    return point; 
  }

  /**
   *
   */
  public static class OffsetBuilder {

    private double north;
    private double east;
    private double south;
    private double west;

    /**
     *
     */
    public OffsetBuilder() {
    }

    public OffsetBuilder north(double north) {
      this.north = north;
      return this;
    }

    public OffsetBuilder east(double east) {
      this.east = east;
      return this;
    }

    public OffsetBuilder south(double south) {
      this.south = south;
      return this;
    }

    public OffsetBuilder west(double west) {
      this.west = west;
      return this;
    }

    /**
     *
     * @return
     */
    public Offset build() {
      return new Offset(north, east, south, west);
    }

  }
}
