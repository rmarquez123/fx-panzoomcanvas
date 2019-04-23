package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.layers.points.PointMarker;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ricardo Marquez
 */
public final class Cache<T> {

  Map<MarkerParameterPair, PointShapeSymbology> symbologies = new HashMap<>();

  /**
   *
   * @param marker
   * @param parameter
   */
  public boolean contains(PointMarker<?> marker, T parameter) {
    return this.symbologies.containsKey(new MarkerParameterPair(marker, parameter));
  }

  /**
   *
   * @param marker
   * @param parameter
   * @param symbology
   */
  public void put(PointMarker<?> marker, T parameter, PointShapeSymbology symbology) {
    this.symbologies.put(new MarkerParameterPair(marker, parameter), symbology);
  }

  /**
   *
   */
  public PointShapeSymbology get(PointMarker<?> marker, T parameter) {
    return this.symbologies.get(new MarkerParameterPair(marker, parameter));
  }
  
  /**
   * 
   */
  void clear() {
    this.symbologies.clear();
  }

}
