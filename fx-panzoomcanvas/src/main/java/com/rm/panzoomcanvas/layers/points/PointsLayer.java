package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.FxCanvas;
import com.rm.panzoomcanvas.GeometricLayer;
import com.rm.panzoomcanvas.LayerMouseEvent;
import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.layers.BaseLayer;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.layers.HoveredMarkers;
import com.rm.panzoomcanvas.layers.LayerHoverSelect;
import com.rm.panzoomcanvas.layers.LayerTooltip;
import com.rm.panzoomcanvas.projections.Projector;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

/**
 *
 * @author rmarquez
 * @param <T> The type of the user object.
 */
public class PointsLayer<T> extends BaseLayer implements GeometricLayer {

  private final PointSymbology symbology;
  private final PointsSource<T> source;
  private LayerTooltip tooltip;
  private final LayerHoverSelect<PointMarker<T>, T> hoverSelect;
  private final Property<PointsLabel> pointsLabelProperty = new SimpleObjectProperty<>();
  private final ReadOnlyObjectWrapper<FxPoint> centerProperty = new ReadOnlyObjectWrapper<>();
  private final ReadOnlyObjectWrapper<FxEnvelope> envelopeProperty = new ReadOnlyObjectWrapper<>();

  /**
   *
   * @param name
   * @param symbology
   * @param source
   */
  public PointsLayer(String name, PointSymbology symbology, PointsSource<T> source) {
    super(name, source);
    Objects.requireNonNull(symbology, "Symbology cannot be null");
    this.source = source;
    this.symbology = symbology;
    this.hoverSelect = new PointLayerHoverSelect(this);
    this.hoverSelect.selected().addListener((obs, oldVal, change) -> this.repaint());
    this.pointsLabelProperty.addListener((obs, old, change) -> this.repaint());
    this.source.pointMarkersProperty().addListener((ListChangeListener.Change<? extends PointMarker<T>> change) -> {
      if (change.next()) {
        this.setEnvelopeAndCenter(); 
        super.repaint();
      }
    });
  }

  /**
   *
   * @return
   */
  public PointsSource<T> getSource() {
    return source;
  }

  /**
   *
   * @throws RuntimeException
   */
  private void setEnvelopeAndCenter() throws RuntimeException {
    Geometry ref = this.getReferenceGeometryFromSource();
    if (ref != null) {
      Point centroid = ref.getCentroid();
      if (!centroid.isEmpty()) {
        SpatialRef spatialRef = this.source.getSpatialRef();
        Envelope envelope = ref.getEnvelopeInternal();
        FxEnvelope fxEnvelope = FxEnvelope.fromJtsEnvelope(envelope, spatialRef);
        this.envelopeProperty.setValue(fxEnvelope);

        FxPoint center = new FxPoint(centroid.getX(), centroid.getY(), spatialRef);
        this.centerProperty.setValue(center);
      } else {
        throw new RuntimeException("Reference point has an 'empty' centroid.");
      }
    } else {
      this.centerProperty.set(null);
      this.envelopeProperty.set(null);
    }
  }

  /**
   *
   * @return
   */
  private Geometry getReferenceGeometryFromSource() {
    Geometry ref = null;
    List<PointMarker<T>> copy = new ArrayList<>(this.source.pointMarkersProperty());
    for (PointMarker<T> marker : copy) {
      Geometry point = marker.getJtsGeometry().getEnvelope();
      if (ref == null) {
        ref = point;
      } else {
        ref = ref.union(point);
      }
    }
    return ref;
  }

  /**
   *
   * @return
   */
  public Property<PointsLabel> labelProperty() {
    return this.pointsLabelProperty;
  }

  /**
   *
   * @param userObject
   * @return
   */
  public PointMarker<T> getMarker(T userObject) {
    PointMarker<T> result = this.source
      .pointMarkersProperty().stream()
      .filter(p -> Objects.equals(p.getUserObject(), userObject))
      .findFirst()
      .orElse(null);
    return result;
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
  public ListProperty<PointMarker<T>> selectedMarkersProperty() {
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
    if (!this.visibleProperty().get()) {
      return;
    }
    Projector projector = args.getCanvas().getProjector();
    ObservableList<PointMarker<T>> copy = this.source.pointMarkersProperty();
    for (PointMarker<T> marker : copy) {
      FxPoint point = marker.getPoint();
      ScreenPoint screenPoint = projector.projectGeoToScreen(point, args.getScreenEnv());
      this.symbology.apply(this, marker, args, screenPoint);
      PointsLabel label = this.pointsLabelProperty.getValue();
      if (label != null) {
        label.apply(this, marker, args, screenPoint);
      }
    }
    List<PointMarker<T>> selected = this.selectedMarkersProperty().getValue();
    if (selected != null) {
      for (PointMarker<T> marker : selected) {
        if (marker == null || !this.source.contains(marker)) {
          continue;
        }
        FxPoint point = marker.getPoint();
        ScreenPoint screenPoint = projector.projectGeoToScreen(point, args.getScreenEnv());
        this.symbology.apply(this, marker, args, screenPoint);
        PointsLabel label = this.pointsLabelProperty.getValue();
        if (label != null) {
          label.apply(this, marker, args, screenPoint);
        }
      }
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
   * @param markers
   * @return
   */
  PointMarker<T> getClosesPoint(LayerMouseEvent e, List<PointMarker<T>> markers) {
    PointMarker<T> result = null;
    double minDistance = Double.NaN;
    ScreenPoint mouseScnPt = new ScreenPoint(e.mouseEvt.getX(), e.mouseEvt.getY());
    ScreenEnvelope screenEnv = e.screenEnv;
    for (PointMarker<T> marker : markers) {
      FxPoint currPoint = marker.getPoint();
      ScreenPoint markerScreenPt = e.projector.projectGeoToScreen(currPoint, screenEnv);
      double d = mouseScnPt.distance(markerScreenPt);
      if (Double.isNaN(minDistance) || d < minDistance) {
        minDistance = d;
        result = marker;
      }
    }
    return result;
  }

  /**
   *
   * @param e
   * @return
   */
  List<PointMarker<T>> getMouseEvtList(LayerMouseEvent e) {
    double eX = e.mouseEvt.getX();
    double eY = e.mouseEvt.getY();

    ScreenPoint mouseScnPt = new ScreenPoint(eX, eY);
    ScreenEnvelope screenEnv = e.screenEnv;
    List<PointMarker<T>> result = new ArrayList<>();
    for (PointMarker<T> marker : this.source.pointMarkersProperty()) {
      FxPoint currPoint = marker.getPoint();
      ScreenPoint markerScreenPt = e.projector.projectGeoToScreen(currPoint, screenEnv);
      boolean pointsIntersect = mouseScnPt.intesects(markerScreenPt, 5);
      if (pointsIntersect) {
        result.add(marker);
      }
    }
    return result;
  }

  /**
   *
   * @return
   */
  @Override
  public ReadOnlyObjectProperty<FxPoint> centerProperty() {
    return this.centerProperty.getReadOnlyProperty();
  }

  /**
   *
   * @return
   */
  @Override
  public ReadOnlyObjectProperty<FxEnvelope> envelopeProperty() {
    return this.envelopeProperty.getReadOnlyProperty();
  }

  /**
   * 8
   *
   * @param userObj
   */
  public void selectByUserObject(Object userObj) {
    if (userObj != null) {
      List<PointMarker<T>> list = this.source.pointMarkersProperty().stream()
        .filter((m) -> Objects.equals(m.getUserObject(), userObj))
        .collect(Collectors.toList());
      if (!list.isEmpty()
        && (this.selectedMarkersProperty().get() == null || this.isNotSelected(userObj))) {
        ObservableList<PointMarker<T>> newlist = FXCollections.observableArrayList(list);
        this.selectedMarkersProperty().setValue(newlist);
      }
    } else {
      ObservableList<PointMarker<T>> newlist = FXCollections.observableArrayList();
      this.selectedMarkersProperty().setValue(newlist);
    }
  }

  /**
   *
   * @param userObj
   * @return
   */
  public boolean isSelected(Object userObj) {
    boolean result = this.selectedMarkersProperty().get().stream()
      .anyMatch(m -> Objects.equals(m.getUserObject(), userObj));
    return result;
  }

  /**
   *
   * @param userObj
   * @return
   */
  public boolean isNotSelected(Object userObj) {
    boolean result = !this.isSelected(userObj);
    return result;
  }
}
