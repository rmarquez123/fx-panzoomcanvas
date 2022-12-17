package com.rm.panzoomcanvas;

import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.Point;
import com.rm.panzoomcanvas.core.SpatialRef;

/**
 *
 * @author Ricardo Marquez
 */
public class Degrees extends FxPoint{
  
  /**
   * 
   * @param lat
   * @param lon 
   */
  public Degrees(double lat, double lon) {
    super(lon, lat, new SpatialRef(4326, new Point(-180, -90), new Point(180, 90)) {
    });
  }
  
}
