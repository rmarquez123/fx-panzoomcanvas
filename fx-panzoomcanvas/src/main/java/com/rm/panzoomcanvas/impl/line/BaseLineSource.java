package com.rm.panzoomcanvas.impl.line;

import com.rm.panzoomcanvas.ParamsIntersects;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.SpatialUtils;
import com.rm.panzoomcanvas.layers.line.LineLayerSource;
import org.locationtech.jts.geom.Geometry;

/**
 *
 * @author rmarquez
 */
public abstract class BaseLineSource<T> implements LineLayerSource<T> {

  private final SpatialRef spatialRef;
  private double buffer = 2.0;

  /**
   *
   * @param spatialRef
   */
  protected BaseLineSource(SpatialRef spatialRef) {
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
  public boolean intersects(ParamsIntersects args) {
    Geometry jtsLine = this.getLineMarker().getJtsGeometry(); 
    FxPoint geomPoint = args.getGeomPoint(this.spatialRef);
    boolean result = SpatialUtils.intersects(jtsLine, geomPoint, this.buffer);
    return result;
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final SpatialRef getSpatialRef() {
    return this.spatialRef;
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public boolean intersects(FxPoint refPoint) {
    Geometry geom = this.getLineMarker().getJtsGeometry(); 
    boolean result = SpatialUtils.intersects(geom, refPoint, this.buffer);
    return result;
  }
}
