package com.rm.panzoomcanvas.core;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author rmarquez
 */
public class FxPoint {

  private final double x;
  private final double y;
  private final SpatialRef sr;
  private Point regularPoint;
  private final Map<SpatialRef, Point> cache = new HashMap<>();
  private Geometry jtsPoint = null;

  /**
   *
   * @param x
   * @param y
   * @param sr
   */
  public FxPoint(double x, double y, SpatialRef sr) {
    Objects.requireNonNull(sr, "spatial reference cannot be null"); 
    this.x = x;
    this.y = y;
    this.sr = sr;
  }
  
  /**
   *
   * @param point
   * @param sr
   */
  public FxPoint(Point point, SpatialRef sr) {
    this.x = point.getX();
    this.y = point.getY();
    this.sr = sr;
  }

  /**
   *
   * @param fxPoints
   * @return
   */
  public static SpatialRef getSpatialRef(FxPoint... fxPoints) {
    SpatialRef result = null;
    for (FxPoint fxPoint : fxPoints) {
      SpatialRef newSpatialRef = fxPoint.getSpatialRef();
      if (result == null) {
        result = newSpatialRef;
      } else if (!Objects.equals(newSpatialRef, result)) {
        throw new IllegalStateException("Inconsistent spatial references");
      }
    }
    return result;
  }
  
  /**
   * 
   * @return 
   */
  public final SpatialRef getSpatialRef() {
    return sr;
  }
  
  /**
   * 
   * @return 
   */
  public final double getY() {
    return y;
  } 
  
  /**
   * 
   * @return 
   */
  public final double getX() {
    return x;
  }

  /**
   *
   * @return
   */
  public final Point asPoint() {
    if (this.regularPoint == null) {
      this.regularPoint = new Point(this.x, this.y); 
    }
    return this.regularPoint;
  }

  /**
   *
   * @return
   */
  public Geometry asJtsPoint() {
    if (this.jtsPoint == null) {
      this.jtsPoint = SpatialUtils.createJtsPoint(this.getX(), this.getY(), this.sr);
    }
    return this.jtsPoint;
  }
  
  /**
   * 
   * @param dx
   * @param dy
   * @return 
   */
  public FxPoint add(double dx, double dy) {
    double newx = this.x + dx;
    double newy = this.y + dy;
    FxPoint fxPoint = new FxPoint(newx, newy, this.sr);
    return fxPoint; 
  }
  

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public String toString() {
    return "FxPoint{" + "x=" + x + ", y=" + y + ", sr=" + sr.getSrid() + '}';
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
    hash = 37 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
    hash = 37 * hash + Objects.hashCode(this.sr);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FxPoint other = (FxPoint) obj;
    if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
      return false;
    }
    if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
      return false;
    }
    if (!Objects.equals(this.sr, other.sr)) {
      return false;
    }
    return true;
  }
  
  
  

}
