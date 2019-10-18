package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.LayerGeometry;
import com.rm.panzoomcanvas.core.SpatialRef;
import javafx.collections.ObservableList;

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
  public SpatialRef getSpatialRef();
  
  
  /**
   * 
   */
  public ObservableList<PointMarker<T>> pointMarkersProperty(); 
  
  /**
   * 
   */
  public boolean contains(PointMarker<T> needle); 
}
