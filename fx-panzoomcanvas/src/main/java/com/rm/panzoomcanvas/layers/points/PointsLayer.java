package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.layers.LayerTooltip;
import com.rm.panzoomcanvas.FxCanvas;
import com.rm.panzoomcanvas.LayerMouseEvent;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.core.VirtualPoint;
import com.rm.panzoomcanvas.layers.BaseLayer;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.layers.HoveredMarkers;
import com.rm.panzoomcanvas.layers.LayerHoverSelect;
import com.rm.panzoomcanvas.projections.Projector;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

/**
 *
 * @author rmarquez
 * @param <T> A user object type.
 */
public class PointsLayer<T> extends BaseLayer {
  
  private final PointShapeSymbology symbology;
  private final PointsSource<T> source;
  private LayerTooltip tooltip;
  private final LayerHoverSelect<PointMarker<T>, T> hoverSelect;

  /**
   *
   * @param name
   * @param symbology
   * @param source
   */
  public PointsLayer(String name, PointShapeSymbology symbology, PointsSource<T> source) {
    super(name, source);
    this.source = source;
    if (symbology == null) {
      throw new NullPointerException("Symbology cannot be null");
    }
    this.symbology = symbology;
    PointsLayer<T> self = this;
    this.hoverSelect = new LayerHoverSelect<PointMarker<T>, T>(this) {
      @Override
      protected List<PointMarker<T>> getMouseEvtList(LayerMouseEvent e) {
        return self.getMouseEvtList(e);
      }
    }; 
    
  }

  /**
   *
   */
  Node getNode() {
    return this.getLayerCanvas();
  }

  /**
   *
   * @return
   */
  public ReadOnlyProperty<HoveredMarkers<PointMarker<T>>> hoveredMarkersProperty() {
    return this.hoverSelect.hovered(); 
  }

  /**
   *
   * @return
   */
  public ReadOnlyListProperty<PointMarker<T>> selectedMarkersProperty() {
    return this.hoverSelect.selected();
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  protected Node createLayerCanvas(double width, double height) {
    return new Canvas(width, height);
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  protected ScreenEnvelope onGetScreenEnvelope(FxCanvas canvas) {
    VirtualEnvelope virtualEnv = canvas
            .virtualEnvelopeProperty()
            .getValue();
    ScreenEnvelope screenEnv = canvas.screenEnvelopeProperty().getValue();
    ScreenEnvelope result = canvas.getProjector()
            .projectVirtualToScreen(virtualEnv, screenEnv);
    return result;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  protected void onDraw(DrawArgs args) {
    Projector projector = args.getCanvas().getProjector();
    int numPoints = this.source.getNumPoints();
    for (int i = 0; i < numPoints; i++) {
      PointMarker marker = this.source.getFxPoint(i);
      FxPoint point = marker.getPoint();
      ScreenPoint screenPoint = projector.projectGeoToScreen(point, args.getScreenEnv());
      this.symbology.apply(this, marker, args, screenPoint);
    }
  }

  /**
   *
   * @param pointsTooltipBuilder
   */
  public void setTooltip(LayerTooltip.Builder pointsTooltipBuilder) {
    if (this.tooltip != null) {
      this.tooltip.destroy();
    }
    this.tooltip = pointsTooltipBuilder.build(this.hoverSelect);
  }
  

  /**
   *
   * @param e
   * @return
   */
  private List<PointMarker<T>> getMouseEvtList(LayerMouseEvent e) {
    double eX = e.mouseEvt.getX();
    double eY = e.mouseEvt.getY();
    ScreenPoint scrnPt = new ScreenPoint(eX, eY);
    ScreenEnvelope env = e.screenEnv;
    VirtualPoint virtual = e.projector.projectScreenToVirtual(scrnPt, env);
    List<PointMarker<T>> result = new ArrayList<>();
    for (int i = 0; i < this.source.getNumPoints(); i++) {
      PointMarker<T> marker = this.source.getFxPoint(i);
      SpatialRef spatialRef = marker.getPoint().getSpatialRef();
      FxPoint refPoint = e.projector.projectVirtualToGeo(virtual.asPoint(), spatialRef);
      FxPoint currPoint = marker.getPoint();
      boolean pointsIntersect = this.source.intersects(refPoint, currPoint);
      if (pointsIntersect) {
        result.add(marker);
      }
    }
    return result;
  }

}
