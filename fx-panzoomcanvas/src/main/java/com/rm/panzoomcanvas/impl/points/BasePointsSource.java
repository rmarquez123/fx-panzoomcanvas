package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.ParamsIntersects;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import com.rm.panzoomcanvas.layers.points.PointsSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.collections.ObservableList;

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
    Objects.requireNonNull(spatialRef, "Spatail reference cannot be null");
    this.spatialRef = spatialRef;

  }

  /**
   *
   * @param buffer
   */
  public void setBuffer(double buffer) {
    this.buffer = buffer;
  }

  @Override
  public final SpatialRef getSpatialRef() {
    return spatialRef;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final boolean intersects(ParamsIntersects args) {
    boolean result = false;
    List<PointMarker<T>> copy = new ArrayList<>(this.pointMarkersProperty());
    for (PointMarker<T> marker : copy) {
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

  @Override
  public ObservableList<PointMarker<T>> pointMarkersProperty() {
    //To change body of generated methods, choose Tools | Templates.
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean contains(PointMarker<T> needle) {
    //To change body of generated methods, choose Tools | Templates.
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
