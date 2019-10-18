package com.rm.panzoomcanvas.projections;

import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.GeometryProjection;
import com.rm.panzoomcanvas.core.Level;
import com.rm.panzoomcanvas.core.Point;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.core.VirtualPoint;

/**
 *
 * @author rmarquez
 */
public class Projector {

  
  private final SpatialRef baseSpatialRef;
  private final MapCanvasSR virtualSr = new MapCanvasSR();
  private final GeometryProjection geomProject; 
  /**
   *
   * @param baseSpatialRef
   * @param geomProj
   */
  public Projector(SpatialRef baseSpatialRef, GeometryProjection geomProj) {
    this.baseSpatialRef = baseSpatialRef;
    this.geomProject = geomProj;
  }

  public SpatialRef getBaseSpatialRef() {
    return baseSpatialRef;
  }

  public GeometryProjection getGeomProject() {
    return geomProject;
  }
  

  /**
   *
   * @param screenEnvVal
   * @return
   */
  public VirtualEnvelope projectScreenToVirtualStrict(ScreenEnvelope screenEnvVal) {
    VirtualPoint vMinCheck = this.projectScreenToVirtual(screenEnvVal.getMin(), screenEnvVal);
    VirtualPoint vMaxCheck = this.projectScreenToVirtual(screenEnvVal.getMax(), screenEnvVal);
    double width = this.baseSpatialRef.getWidth();
    double height = this.baseSpatialRef.getHeight();
    double vxMin = Math.max(0 - 0.5 * width, vMinCheck.getX());
    double vyMin = Math.max(0 - 0.5 * height, vMaxCheck.getY());
    double vxMax = Math.min(0 + 0.5 * width, vMaxCheck.getX());
    double vyMax = Math.min(0 + 0.5 * height, vMinCheck.getY());

    VirtualPoint vMin = new VirtualPoint(vxMin, vyMin);
    VirtualPoint vMax = new VirtualPoint(vxMax, vyMax);
    VirtualEnvelope result = new VirtualEnvelope(vMin, vMax);
    return result;
  }

  /**
   * Projects screen envelope to a virtual envelope.
   *
   * @param screenEnv
   * @return the virtual envelope
   */
  public VirtualEnvelope projectScreenToVirtual(ScreenEnvelope screenEnv) {
    VirtualEnvelope result;
    ScreenPoint max = screenEnv.getMax();
    VirtualPoint vMax = this.projectScreenToVirtual(max, screenEnv);
    ScreenPoint min = screenEnv.getMin();
    VirtualPoint vMin = this.projectScreenToVirtual(min, screenEnv);
    result = new VirtualEnvelope(vMin, vMax);
    return result;
  }

  /**
   * Projects screen coordinate to a virtual coordinate.
   *
   * @param scrnPt
   * @param env
   * @return the virtual point.
   */
  public VirtualPoint projectScreenToVirtual(ScreenPoint scrnPt, ScreenEnvelope env) {
    VirtualPoint result;
    Level level = env.getLevel();
    ScreenPoint scrnCenter = env.getCenter();
    double width = this.virtualSr.getWidth();
    double height = this.virtualSr.getHeight();
    Point virtualMin = this.virtualSr.getMin();
    Point virtualMax = this.virtualSr.getMax();
    double f = Math.pow(2, -level.getValue());
    double scaleX = 1;
    double scaleY = 1;
    double vX = virtualMin.getX() + scaleX * f * (scrnPt.getX() - scrnCenter.getX()) + 0.5 * width;
    double vY = virtualMax.getY() - scaleY * f * (scrnPt.getY() - scrnCenter.getY()) - 0.5 * height;
    result = new VirtualPoint(vX, vY);
    return result;
  }

  /**
   *
   * @param virtualEnv
   * @param screenEnv
   * @return
   */
  public ScreenEnvelope projectVirtualToScreen(
          VirtualEnvelope virtualEnv, ScreenEnvelope screenEnv) {
    ScreenPoint min = this.projectVirtualToScreen(virtualEnv.getMin(), screenEnv);
    ScreenPoint max = this.projectVirtualToScreen(virtualEnv.getMax(), screenEnv);
    ScreenEnvelope result = new ScreenEnvelope(
            new ScreenPoint(min.getX(), max.getY()),
            new ScreenPoint(max.getX(), min.getY()),
            screenEnv.getLevel(),
            screenEnv.getCenter());
    return result;
  }

  /**
   *
   * @param pt
   * @param scrnEvn
   * @return
   */
  public ScreenPoint projectVirtualToScreen(Point pt, ScreenEnvelope scrnEvn) {
    Level level = scrnEvn.getLevel();
    ScreenPoint scrnCenter = scrnEvn.getCenter();
    double width = this.virtualSr.getWidth();
    double height = this.virtualSr.getHeight();
    Point virtualMin = this.virtualSr.getMin();
    Point virtualMax = this.virtualSr.getMax();
    double f = Math.pow(2, -level.getValue());
    double scaleX = 1.0;
    double scaleY = 1.0;
    double vX = pt.getX();
    double vY = pt.getY();
    double sX = (vX - virtualMin.getX() - 0.5 * width) / (scaleX * f) + scrnCenter.getX();
    double sY = (-vY - virtualMax.getY() + 0.5 * height) / (scaleY * f) + scrnCenter.getY();
    ScreenPoint result = new ScreenPoint(sX, sY);
    return result;
  }

  /**
   * 
   * @param screenEnv
   * @param d
   * @return 
   */
  private double[] projectVirtualToScreen(ScreenEnvelope screenEnv, double[] d) {
    Level level = screenEnv.getLevel();
    ScreenPoint scrnCenter = screenEnv.getCenter();
    double width = this.virtualSr.getWidth();
    double height = this.virtualSr.getHeight();
    Point virtualMin = this.virtualSr.getMin();
    Point virtualMax = this.virtualSr.getMax();
    double f = Math.pow(2, -level.getValue());
    double scaleX = 1.0;
    double scaleY = 1.0;
    double vX = d[0];
    double vY = d[1];
    double sX = (vX - virtualMin.getX() - 0.5 * width) / (scaleX * f) + scrnCenter.getX();
    double sY = (-vY - virtualMax.getY() + 0.5 * height) / (scaleY * f) + scrnCenter.getY();
    return new double[]{sX, sY};
  }

  /**
   *
   * @param virtual
   * @param screenEnv
   * @return
   */
  private double[][] projectVirtualToScreen(double[][] virtual, ScreenEnvelope screenEnv) {
    double[][] result = new double[virtual.length][2];
    for (int i = 0; i < virtual.length; i++) {
      result[i] = this.projectVirtualToScreen(screenEnv, virtual[i]);
    }
    return result;
  }

  /**
   * Projects virtual envelope to a geometric envelope.
   *
   * @param virtualEnv
   * @param destSr
   * @return the geometric envelope.
   */
  public FxEnvelope projectVirtualToGeo(VirtualEnvelope virtualEnv, SpatialRef destSr) {
    FxPoint min = this.projectVirtualToGeo(virtualEnv.getMin(), destSr);
    FxPoint max = this.projectVirtualToGeo(virtualEnv.getMax(), destSr);
    return new FxEnvelope(min, max);
  }

  /**
   * Projects virtual coordinate to a geometric coordinate.
   *
   * @param virtualPt
   * @param destSr
   * @return the geometric point.
   */
  public FxPoint projectVirtualToGeo(Point virtualPt, SpatialRef destSr) {
    Point srcMax = this.baseSpatialRef.getMax();
    Point srcMin = this.baseSpatialRef.getMin();
    Point virtualMax = virtualSr.getMax();
    Point virtualMin = virtualSr.getMin();
    double srcDeltaX = srcMax.getX() - srcMin.getX();
    double srcDeltaY = srcMax.getY() - srcMin.getY();
    double virtualDeltaX = virtualMax.getX() - virtualMin.getX();
    double virtualDeltaY = virtualMax.getY() - virtualMin.getY();
    double x = srcMin.getX() + (virtualPt.getX() - virtualMin.getX()) * srcDeltaX / virtualDeltaX;
    double y = srcMin.getY() + (virtualPt.getY() - virtualMin.getY()) * srcDeltaY / virtualDeltaY;
    FxPoint result = new FxPoint(x, y, this.baseSpatialRef);
    FxPoint a = this.geomProject.project(result, destSr); 
    return a;
  }

  /**
   *
   * @param spatialRef
   * @param points
   * @return
   */
  public double[][] projectGeoToVirtual(SpatialRef spatialRef, double[][] points) {
    double[][] result = new double[points.length][2];
    for (int i = 0; i < points.length; i++) {
      result[i] = this.projectGeoToVirtual(spatialRef, points[i]);
    }
    return result;
  }

  /**
   *
   * @param x
   * @param spatialRef
   * @return
   */
  public double[] projectGeoToVirtual(SpatialRef spatialRef, double[] x) {
    Point srcMax = spatialRef.getMax();
    Point srcMin = spatialRef.getMin();
    double srcMinX = srcMin.getX();
    double srcMinY = srcMin.getY();
    double srcDeltaX = srcMax.getX() - srcMinX;
    double srcDeltaY = srcMax.getY() - srcMinY;
    Point virtualMax = this.virtualSr.getMax();
    Point virtualMin = this.virtualSr.getMin();
    double vMinX = virtualMin.getX();
    double vMinY = virtualMin.getY();
    double virtualDeltaX = virtualMax.getX() - vMinX;
    double virtualDeltaY = virtualMax.getY() - vMinY;
    double[] result = new double[2];
    result[0] = (x[0] - srcMinX) * virtualDeltaX / srcDeltaX + vMinX;
    result[1] = (x[1] - srcMinY) * virtualDeltaY / srcDeltaY + vMinY;
    return result;
  }

  /**
   *
   * @param geomPoint
   * @return
   */
  public VirtualPoint projectGeoToVirtual(FxPoint geomPoint) {
    
    FxPoint projectedToBaseRef = this.getGeomProject().project(geomPoint, this.baseSpatialRef); 
    Point srcMax = this.baseSpatialRef.getMax();
    Point srcMin = this.baseSpatialRef.getMin();
    Point virtualMax = this.virtualSr.getMax();
    Point virtualMin = this.virtualSr.getMin();

    double srcDeltaX = srcMax.getX() - srcMin.getX();
    double srcDeltaY = srcMax.getY() - srcMin.getY();

    double virtualDeltaX = virtualMax.getX() - virtualMin.getX();
    double virtualDeltaY = virtualMax.getY() - virtualMin.getY();
    double vX = ( projectedToBaseRef.getX() - srcMin.getX()) * virtualDeltaX / srcDeltaX + virtualMin.getX();
    double vY = ( projectedToBaseRef.getY() - srcMin.getY()) * virtualDeltaY / srcDeltaY + virtualMin.getY();
    
    
    return new VirtualPoint(vX, vY);
  }

  /**
   *
   * @param geomEnv
   * @return
   */
  private VirtualEnvelope projectGeoToVirtual(FxEnvelope geomEnv) {
    VirtualPoint min = this.projectGeoToVirtual(geomEnv.getMinFxPoint());
    VirtualPoint max = this.projectGeoToVirtual(geomEnv.getMaxFxPoint());
    VirtualEnvelope result = new VirtualEnvelope(min, max);
    return result;
  }

  /**
   *
   * @param geomEnv
   * @param screenEnv
   * @return
   */
  public ScreenEnvelope projectGeoToScreen(FxEnvelope geomEnv, ScreenEnvelope screenEnv) {
    FxEnvelope geomEnvMerc = projectGeometry(geomEnv, this.baseSpatialRef);
    VirtualEnvelope virtualEnv = this.projectGeoToVirtual(geomEnvMerc);
    return this.projectVirtualToScreen(virtualEnv, screenEnv);
  }

  /**
   *
   * @param geomPoint
   * @param screenEnv
   * @return
   */
  public ScreenPoint projectGeoToScreen(FxPoint geomPoint, ScreenEnvelope screenEnv) {
    FxPoint geomPointMerc = this.projectGeometry(geomPoint, this.baseSpatialRef);
    VirtualPoint virtualPt = this.projectGeoToVirtual(geomPointMerc);
    ScreenPoint result = this.projectVirtualToScreen(virtualPt.asPoint(), screenEnv);
    return result;
  }

  /**
   * 
   * @param screenEnv
   * @param points
   * @return 
   */
  public double[][] projectGeoToScreen(ScreenEnvelope screenEnv, SpatialRef sr, double[][] points) {
    double[][] result = new double[points.length][2]; 
    for (int i = 0; i < points.length; i++) {
      ScreenPoint a = projectGeoToScreen(new FxPoint(points[i][0], points[i][1], sr), screenEnv);
      result[i] = new double[]{a.getX(), a.getY()};
    }
    return result;
  }
  

  /**
   * 
   * @param screenEnv
   * @param points
   * @return 
   */
  public double[][] projectGeoToScreen(ScreenEnvelope screenEnv, double[][] points) {
    double[][] virtual = this.projectGeoToVirtual(this.baseSpatialRef, points);
    double[][] result = this.projectVirtualToScreen(virtual, screenEnv);
    return result;
  }

  /**
   *
   * @param geomPoint
   * @param baseSpatialRef
   * @return
   */
  private FxPoint projectGeometry(FxPoint geomPoint, SpatialRef baseSpatialRef) {
    FxPoint result;
    if (geomPoint.getSpatialRef().equals(baseSpatialRef)) {
      result = geomPoint;
    } else {
      result = this.geomProject.project(geomPoint, baseSpatialRef);
    }
    return result;
  }

  /**
   *
   * @param geomEnv
   * @param baseSpatialRef
   * @return
   */
  private FxEnvelope projectGeometry(FxEnvelope geomEnv, SpatialRef baseSpatialRef) {
    FxEnvelope result;
    if (geomEnv.getSr().equals(baseSpatialRef)) {
      result = geomEnv;
    } else {
      FxPoint newMin = this.geomProject.project(geomEnv.getMinFxPoint(), baseSpatialRef);
      FxPoint newMax = this.geomProject.project(geomEnv.getMaxFxPoint(), baseSpatialRef);
      result = new FxEnvelope(newMin, newMax);
    }
    return result;
  }

}
