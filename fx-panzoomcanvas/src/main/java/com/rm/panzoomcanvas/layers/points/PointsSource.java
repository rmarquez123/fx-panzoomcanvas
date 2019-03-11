package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.LayerGeometry;
import com.rm.panzoomcanvas.core.SpatialRef;

/**
 *
 * @author rmarquez
 * @param <T>
 */
public interface PointsSource<T> extends LayerGeometry {

  /**
   *
   * @return
   */
  int getNumPoints();

  /**
   *
   * @param i
   * @return
   */
  PointMarker<T> getFxPoint(int i);

  /**
   *
   * @return
   */
  public SpatialRef getSpatialRef();
}
