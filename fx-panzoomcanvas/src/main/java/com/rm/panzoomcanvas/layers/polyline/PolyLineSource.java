package com.rm.panzoomcanvas.layers.polyline;

import com.rm.panzoomcanvas.LayerGeometry;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.DrawArgs;
import java.util.List;
import javafx.collections.ObservableList;

/**
 *
 * @author rmarquez
 */
public interface PolyLineSource<T> extends LayerGeometry {

  /**
   * 
   * @return 
   */
  public SpatialRef getSpatialRef();
  
  /**
   *
   * @param args
   * @return
   */
  List<PolyLineMarker<T>> screenPoints(DrawArgs args);
  
    
  
  /**
   * 
   * @return 
   */
  ObservableList<PolyLineMarker<T>> markers();
  
  /**
   * 
   * @return 
   */
  boolean contains(PolyLineMarker<T> marker);

  
  
  
  
}
