package com.rm.panzoomcanvas.layers.polyline;

import com.rm.panzoomcanvas.LayerMouseEvent;
import com.rm.panzoomcanvas.layers.LayerHoverSelect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricardo Marquez
 */
class PolyLineHoverSelect extends LayerHoverSelect {

  public PolyLineHoverSelect(PolyLineLayer host) {
    super(host);
  }

  @Override
  protected List<PolyLineMarker> getMouseEvtList(LayerMouseEvent e) {
    List<PolyLineMarker> fullList = ((PolyLineLayer) this.host).getMouseEvtList(e);
    PolyLineMarker closest = ((PolyLineLayer) this.host).getClosesPoint(e, fullList);
    List<PolyLineMarker> result;
    if (closest == null) {
      result = new ArrayList<>();
    } else {
      result = Arrays.asList(closest);
    }
    return result;
  }

}
