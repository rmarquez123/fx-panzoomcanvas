package com.rm.panzoomcanvas.core;

import java.util.Objects;

/**
 *
 * @author rmarquez
 */
public class FxEnvelope {

  private Point min;
  private Point max;
  private final SpatialRef sr;

  /**
   *
   * @param min
   * @param max
   */
  public FxEnvelope(FxPoint min, FxPoint max) {
    if (!Objects.equals(min.getSpatialRef(), max.getSpatialRef())) {
      throw new IllegalArgumentException("Arguments do not have the same spatial reference.  Check args : {"
              + "maxSR : " + max.getSpatialRef()
              + "minSR: " + min.getSpatialRef()
              + "}");
    }
    double minX;
    double minY;
    double maxX;
    double maxY;
    if (min.getX() > max.getX()) {
      minX = max.getX();
      maxX = min.getX(); 
    } else {
      minX = min.getX();
      maxX = max.getX(); 
    }
    if (min.getY() > max.getY()) {
      minY = max.getY();
      maxY = min.getY(); 
    } else {
      minY = min.getY();
      maxY = max.getY(); 
    }
    this.min = new Point(minX, minY);
    this.max = new Point(maxX, maxY);
    this.sr = max.getSpatialRef();
  }

  public final Point getMin() {
    return min;
  }
  
  public final FxPoint getMinFxPoint() {
    return new FxPoint(min.getX(), min.getY(), sr);
  }
  
  public final FxPoint getMaxFxPoint() {
    return new FxPoint(max.getX(), max.getY(), sr);
  }

  public final Point getMax() {
    return max;
  }
  public final Double getWidth() {
    return this.max.getX() - this.min.getX();
  }
  
  public final Double getHeight() {
    return this.max.getY() - this.min.getY();
  }
  /**
   *
   * @return
   */
  public final SpatialRef getSr() {
    return sr;
  }

  /**
   *
   * @return
   */
  public final Point getCenterPoint() {
    return new Point(0.5 * (this.getMax().getX() + this.getMin().getX()),
            0.5 * (this.getMax().getY() + this.getMin().getY()));
  }
  /**
   *
   * @return
   */
  public final FxPoint getCenterFxPoint() {
    FxPoint result = new FxPoint(this.getCenterPoint(), sr); 
    return result; 
  }

  @Override
  public String toString() {
    return "FxEnvelope{" + "min=" + min + ", max=" + max + ", sr=" + sr.getSrid() + '}';
  }

  /**
   *
   * @return
   */
  public final VirtualEnvelope withStrictDimensions() {
    Point center = this.getMin();
    double width = this.getSr().getWidth();
    double height = this.getSr().getHeight();
    VirtualPoint newMin = new VirtualPoint(center.getX() - 0.5 * width, center.getY() - 0.5 * height);
    VirtualPoint newMax = new VirtualPoint(center.getX() + 0.5 * width, center.getY() + 0.5 * height);
    VirtualEnvelope result = new VirtualEnvelope(newMin, newMax);
    return result;
  }

}
