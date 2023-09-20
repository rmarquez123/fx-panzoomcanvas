package com.rm.panzoomcanvas.impl.polygon;

import com.rm.panzoomcanvas.ParamsIntersects;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.SpatialUtils;
import com.rm.panzoomcanvas.layers.polygon.PolygonMarker;
import com.rm.panzoomcanvas.layers.polygon.PolygonSource;
import org.locationtech.jts.geom.Geometry;

/**
 *
 * @author rmarquez
 */
public abstract class BasePolygonSource<T> implements PolygonSource<T> {

  private final SpatialRef spatialRef;
  private double buffer = 5.0;

  /**
   *
   * @param spatialRef
   */
  public BasePolygonSource(SpatialRef spatialRef) {
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
  public SpatialRef getSpatialRef() {
    return this.spatialRef;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public boolean intersect(FxPoint refPoint) {
    boolean result;
    PolygonMarker<T> pts = this.getScreenPoints(null); 
    Geometry geometry = pts.getJtsGeometry();
    result = SpatialUtils.intersects(geometry, refPoint, this.buffer);
    return result;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public boolean intersects(ParamsIntersects args) {
    FxPoint geomPoint = args.getGeomPoint(this.getSpatialRef()); 
    boolean result = this.intersect(geomPoint); 
    return result; 
  }

}
