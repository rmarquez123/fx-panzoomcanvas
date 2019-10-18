package com.rm.panzoomcanvas.layers.line;

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
import com.rm.panzoomcanvas.layers.LayerTooltip;
import com.rm.panzoomcanvas.projections.Projector;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.util.Pair;

/**
 *
 * @author rmarquez
 * @param <T>
 */
public class LineLayer<T> extends BaseLayer {

  private final LineLayerSource source;
  private LineSymbology symbology;
  private final LayerHoverSelect<LineMarker<T>, T> hoverSelect;
  private LayerTooltip tooltip;

  /**
   *
   * @param name
   * @param source
   */
  public LineLayer(String name, LineSymbology symbology, LineLayerSource source) {
    super(name, source);
    this.source = source;
    if (symbology == null) {
      throw new NullPointerException("Symbology cannot be null");
    }
    this.symbology = symbology;
    LineLayer<T> self = this;
    this.hoverSelect = new LayerHoverSelect<LineMarker<T>, T>(this) {
      @Override
      protected List<LineMarker<T>> getMouseEvtList(LayerMouseEvent e) {
        return self.getMouseEvtList(e);
      }
    };
  }
  
  /**
   * 
   * @return 
   */
  public LineLayerSource getSource() {
    return source;
  }
  
  

  /**
   *
   * @return
   */
  public ReadOnlyProperty<HoveredMarkers<LineMarker<T>>> hoveredMarkersProperty() {
    return this.hoverSelect.hovered();
  }

  /**
   *
   * @return
   */
  public ReadOnlyListProperty<LineMarker<T>> selectedMarkersProperty() {
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
    ScreenEnvelope layerScreenEnv = canvas.getProjector()
            .projectVirtualToScreen(virtualEnv, screenEnv);
    return layerScreenEnv;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  protected void onDraw(DrawArgs args) {
    LineMarker marker = this.source.getLineMarker();
    Projector projector = args.getCanvas().getProjector();
    LineMarker<T> lineMarker = this.source.getLineMarker();
    Pair<FxPoint, FxPoint> points = lineMarker.getPoints();
    ScreenPoint a = projector.projectGeoToScreen(points.getKey(), args.getScreenEnv());
    ScreenPoint b = projector.projectGeoToScreen(points.getValue(), args.getScreenEnv());
    Pair<ScreenPoint, ScreenPoint> screenPoints = new Pair<>(a, b);
    this.symbology.apply(this, marker, args, screenPoints);
  }

  /**
   *
   * @param e
   * @return
   */
  private List<LineMarker<T>> getMouseEvtList(LayerMouseEvent e) {
    double eX = e.mouseEvt.getX();
    double eY = e.mouseEvt.getY();
    ScreenPoint scrnPt = new ScreenPoint(eX, eY);
    ScreenEnvelope env = e.screenEnv;
    VirtualPoint virtual = e.projector.projectScreenToVirtual(scrnPt, env);
    SpatialRef spatialRef = this.source.getSpatialRef();
    FxPoint refPoint = e.projector.projectVirtualToGeo(virtual.asPoint(), spatialRef);
    boolean intersects = this.source.intersects(refPoint);
    List<LineMarker<T>> result = (intersects) ? Arrays.asList(this.source.getLineMarker()) : Collections.EMPTY_LIST;
    return result;
  }
}
