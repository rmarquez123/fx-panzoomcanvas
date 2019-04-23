package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.layers.DrawArgs;

/**
 *
 * @author rmarquez
 */
public interface PointSymbology {
  ;
  
  /**
   *
   * @param layer
   * @param marker
   * @param args
   * @param screenPoint
   */
  public void apply(PointsLayer<?> layer, PointMarker<?> marker, DrawArgs args, ScreenPoint screenPoint);
}
