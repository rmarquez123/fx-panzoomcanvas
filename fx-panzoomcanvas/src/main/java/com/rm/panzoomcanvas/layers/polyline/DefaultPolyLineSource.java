package com.rm.panzoomcanvas.layers.polyline;

import com.rm.panzoomcanvas.ParamsIntersects;
import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.core.VirtualPoint;
import com.rm.panzoomcanvas.layers.DrawArgs;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Ricardo Marquez
 */
public class DefaultPolyLineSource implements PolyLineSource {

  private final ObservableList<PolyLineMarker> markers = FXCollections.observableArrayList();
  private final SpatialRef spatialRef;

  public DefaultPolyLineSource(SpatialRef ref) {
    this.spatialRef = ref;
  }

  /**
   *
   * @return
   */
  @Override
  public SpatialRef getSpatialRef() {
    return spatialRef;
  }

  /**
   *
   * @param args
   * @return
   */
  @Override
  public List<PolyLineMarker> screenPoints(DrawArgs args) {
    return new ArrayList<>(markers);
  }

  /**
   *
   * @return
   */
  @Override
  public ObservableList<PolyLineMarker> markers() {
    return this.markers;
  }

  /**
   *
   * @param args
   * @return
   */
  @Override
  public boolean intersects(ParamsIntersects args) {
    boolean result = false;
    double eX = args.screenPoint.getX();
    double eY = args.screenPoint.getY();
    ScreenPoint mouseScnPt = new ScreenPoint(eX, eY);
    ScreenEnvelope screenEnv = args.screenEnv;
    VirtualPoint virtualPt = args.projector.projectScreenToVirtual(mouseScnPt, screenEnv);
    for (PolyLineMarker marker : this.markers) {
      FxPoint geoPnt = args.projector.projectVirtualToGeo(virtualPt.asPoint(), marker.getSpatialRef());
      ScreenEnvelope screenbuff = new ScreenEnvelope(new ScreenPoint(0, 0), new ScreenPoint(5, 5),
        screenEnv.getLevel(), mouseScnPt);
      VirtualEnvelope virtualBuff = args.projector.projectScreenToVirtual(screenbuff);
      FxEnvelope geobuff = args.projector.projectVirtualToGeo(virtualBuff, marker.getSpatialRef());
      Double width = geobuff.getWidth();
      if (marker.getJtsGeometry().intersects(geoPnt.asJtsPoint().buffer(width))) {
        result = true;
        break;
      }
    }
    return result;
  }

  /**
   *
   * @param marker
   * @return
   */
  @Override
  public boolean contains(PolyLineMarker marker) {
    return this.markers.contains(marker);
  }

}
