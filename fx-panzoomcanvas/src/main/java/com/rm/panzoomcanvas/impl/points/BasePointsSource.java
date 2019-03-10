package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.ParamsIntersects;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import com.rm.panzoomcanvas.layers.points.PointsSource;

/**
 *
 * @author rmarquez
 */
public abstract class BasePointsSource<T> implements PointsSource<T> {

  private final SpatialRef spatialRef;
  private double buffer = 5.0;
  
  /**
   *
   * @param spatialRef
   */
  protected BasePointsSource(SpatialRef spatialRef) {
    this.spatialRef = spatialRef;
  }

  /**
   *
   * @param buffer
   */
  public void setBuffer(double buffer) {
    this.buffer = buffer;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final boolean intersects(ParamsIntersects args) {
    boolean result = false;
    for (int i = 0; i < this.getNumPoints(); i++) {
      PointMarker<T> marker = this.getFxPoint(i);
      FxPoint currPoint = marker.getPoint();
      ScreenPoint markerScreenPt = args.projector.projectGeoToScreen(currPoint, args.screenEnv);
      boolean pointsIntersect = args.screenPoint.intesects(markerScreenPt, 5); 
      if (pointsIntersect) {
        result = true;
        break;
      }
    }
    return result;
  }
}
