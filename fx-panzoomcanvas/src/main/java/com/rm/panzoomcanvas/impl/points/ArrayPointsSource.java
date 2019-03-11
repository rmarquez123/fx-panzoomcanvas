package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.layers.points.PointMarker;

/**
 *
 * @author rmarquez
 */
public final class ArrayPointsSource<T> extends BasePointsSource<T> {

  private final PointMarker[] points;

  /**
   *
   * @param point
   */
  public ArrayPointsSource(PointMarker<T> point) {
    this(new PointMarker[]{point});
  }

  /**
   *
   * @param points
   */
  public ArrayPointsSource(PointMarker<T>[] points) {
    super(FxPoint.getSpatialRef(PointMarker.getPoints(points)));
    this.points = points;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public int getNumPoints() {
    int result = this.points.length;
    return result;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public PointMarker getFxPoint(int i) {
    PointMarker result = this.points[i];
    return result;
  }

}
