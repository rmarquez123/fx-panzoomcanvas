package com.rm.panzoomcanvas.core;

import org.locationtech.jts.geom.Envelope;

/**
 *
 * @author rmarquez
 */
public class ScreenEnvelope {

  private final ScreenPoint min;

  private final ScreenPoint max;

  private final Level level;

  private final ScreenPoint center;

  /**
   *
   * @param min
   * @param max
   * @param level
   * @param center
   */
  public ScreenEnvelope(ScreenPoint min, ScreenPoint max, Level level, ScreenPoint center) {
    this.min = min;
    this.max = max;
    this.level = level;
    this.center = center;
  }

  public ScreenPoint getMin() {
    return min;
  }

  public ScreenPoint getMax() {
    return max;
  }

  public Level getLevel() {
    return level;
  }

  public ScreenPoint getCenter() {
    return center;
  }

  /**
   *
   * @return
   */
  public double getWidth() {
    return this.max.getX() - this.min.getX();
  }

  /**
   *
   * @return
   */
  public double getHeight() {
    return this.max.getY() - this.min.getY();
  }

  /**
   *
   * @param other
   * @return
   */
  public boolean contains(ScreenEnvelope other) {
    Envelope thisEnv = this.toJtsEnvelope();
    Envelope otherEnv = other.toJtsEnvelope();
    boolean result = thisEnv.contains(otherEnv);
    return result;
  }

  /**
   *
   * @return
   */
  private Envelope toJtsEnvelope() {
    double x1 = this.min.getX();
    double x2 = this.max.getX();
    double y1 = this.min.getY();
    double y2 = this.max.getY();
    Envelope result = new Envelope(x1, x2, y1, y2);
    return result;
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return "{" + "min=" + min + ", max=" + max + ", level=" + level + '}';
  }

}
