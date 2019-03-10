package com.rm.panzoomcanvas;

import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualPoint;
import com.rm.panzoomcanvas.projections.Projector;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ParamsIntersects {

  public final ScreenPoint screenPoint;
  public final Projector projector;
  public final ScreenEnvelope screenEnv;
  private final Map<SpatialRef, FxPoint> geomPoint = new HashMap<>();

  public ParamsIntersects(ScreenPoint screenPoint, Projector projector, ScreenEnvelope screenEnv) {
    this.screenPoint = screenPoint;
    this.projector = projector;
    this.screenEnv = screenEnv;
  }

  /**
   *
   * @param spatialRef
   * @return
   */
  public FxPoint getGeomPoint(SpatialRef spatialRef) {
    VirtualPoint virtual = this.getVirtualPoint();
    FxPoint result = this.getGeomPointFromVirtual(virtual, spatialRef);
    return result;
  }

  /**
   *
   * @param virtual
   * @param spatialRef
   * @return
   */
  private FxPoint getGeomPointFromVirtual(VirtualPoint virtual, SpatialRef spatialRef) {
    FxPoint result;
    if (geomPoint.containsKey(spatialRef)) {
      result = this.geomPoint.get(spatialRef);
    } else {
      result = projector.projectVirtualToGeo(virtual.asPoint(), spatialRef);
      this.geomPoint.put(spatialRef, result);
    }
    return result;
  }

  /**
   *
   * @return
   */
  private VirtualPoint getVirtualPoint() {
    VirtualPoint virtual = projector.projectScreenToVirtual(this.screenPoint, this.screenEnv);
    return virtual;
  }
  
}
