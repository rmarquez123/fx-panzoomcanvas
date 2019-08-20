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
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public String toString() {
    return "FxPoint{" + "x=" + x + ", y=" + y + ", sr=" + sr.getSrid() + '}';
  }

}
