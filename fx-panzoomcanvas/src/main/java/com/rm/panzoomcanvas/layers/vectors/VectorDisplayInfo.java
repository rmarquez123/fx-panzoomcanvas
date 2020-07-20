package com.rm.panzoomcanvas.layers.vectors;

import com.rm.panzoomcanvas.core.SpatialRef;

/**
 *
 * @author Ricardo Marquez
 */
public class VectorDisplayInfo {
  
  final double u;
  final double v;
  final double scale;
  final SpatialRef sr;
  
  /**
   * 
   * @param u
   * @param v
   * @param scale 
   */
  public VectorDisplayInfo(double u, double v, double scale, SpatialRef sr) {
    this.u = u;
    this.v = v;
    this.scale = scale;
    this.sr = sr;
  }
  
}
