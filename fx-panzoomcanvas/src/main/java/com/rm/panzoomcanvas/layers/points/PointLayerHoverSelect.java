package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.LayerMouseEvent;
import com.rm.panzoomcanvas.layers.LayerHoverSelect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricardo Marquez
 */
public class PointLayerHoverSelect extends LayerHoverSelect {

  public PointLayerHoverSelect(PointsLayer host) {
    super(host);
  }

  /**
   *
   * @param e
   * @return
   */
  @Override
  protected List getMouseEvtList(LayerMouseEvent e) {
    List<PointMarker<?>> fullList = ((PointsLayer) this.host).getMouseEvtList(e);
    PointMarker<?> closest = ((PointsLayer) this.host).getClosesPoint(e, fullList);
    List<PointMarker<?>> result;
    if (closest == null) {
      result = new ArrayList<>();
    } else {
      result = Arrays.asList(closest);
    }
    return result;
  }

}
