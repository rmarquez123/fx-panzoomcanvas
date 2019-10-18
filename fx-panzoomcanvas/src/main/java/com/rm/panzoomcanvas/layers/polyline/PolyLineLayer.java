package com.rm.panzoomcanvas.layers.polyline;

import com.rm.panzoomcanvas.FxCanvas;
import com.rm.panzoomcanvas.GeometricLayer;
import com.rm.panzoomcanvas.LayerMouseEvent;
import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.core.VirtualPoint;
import com.rm.panzoomcanvas.layers.BaseLayer;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.layers.LayerHoverSelect;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

/**
 *
 * @author rmarquez
 */
public class PolyLineLayer<T> extends BaseLayer implements GeometricLayer {

  private final PolyLineSource<T> source;
  private final PolyLineSymbology symbology;
  private final LayerHoverSelect<PolyLineMarker<T>, T> hoverSelect;
  private final ReadOnlyObjectWrapper<FxPoint> centerProperty = new ReadOnlyObjectWrapper<>();
  private final ReadOnlyObjectWrapper<FxEnvelope> envelopeProperty = new ReadOnlyObjectWrapper<>();

  /**
   *
   * @param name
   * @param source
   */
  public PolyLineLayer(String name, PolyLineSource<T> source, PolyLineSymbology symbology) {
    super(name, source);
    this.source = source;
    this.symbology = symbology;
    this.hoverSelect = new PolyLineHoverSelect(this);
    this.hoverSelect.selected().addListener((obs, old, change) -> this.repaint());
    this.source.markers().addListener((ListChangeListener.Change<? extends PolyLineMarker<T>> c) -> {
      if (c.next()) {
        this.setEnvelopeAndCenter();
        super.repaint();
      }
    });
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
    ArrayList<PolyLineMarker<T>> copy = new ArrayList<>(this.source.markers());
    for (PolyLineMarker<T> marker : copy) {
      Geometry point = marker.getJtsGeometry();
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
  public PolyLineSource source() {
    return source;
  }

  /**
   *
   * @return
   */
  public ListProperty<PolyLineMarker<T>> selectedProperty() {
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
    List<PolyLineMarker<T>> markers = this.source.screenPoints(args);
    DrawDelegate delegate = new DrawDelegate(this, args);
    markers.forEach(delegate::drawMarker);
  }

  /**
   *
   * @param e
   * @return
   */
  List<PolyLineMarker> getMouseEvtList(LayerMouseEvent e) {
    double eX = e.mouseEvt.getX();
    double eY = e.mouseEvt.getY();
    ScreenPoint mouseScnPt = new ScreenPoint(eX, eY);
    ScreenEnvelope screenEnv = e.screenEnv;
    VirtualPoint virtualPt = e.projector.projectScreenToVirtual(mouseScnPt, screenEnv);
    List<PolyLineMarker> result = new ArrayList<>();
    for (PolyLineMarker marker : this.source.markers()) {
      FxPoint geoPnt = e.projector.projectVirtualToGeo(virtualPt.asPoint(), marker.getSpatialRef());
      ScreenEnvelope screenbuff = new ScreenEnvelope(new ScreenPoint(0, 0), new ScreenPoint(5, 5),
        screenEnv.getLevel(), mouseScnPt);
      VirtualEnvelope virtualBuff = e.projector.projectScreenToVirtual(screenbuff);
      FxEnvelope geobuff = e.projector.projectVirtualToGeo(virtualBuff, marker.getSpatialRef());
      Double width = geobuff.getWidth();
      if (marker.getJtsGeometry().intersects(geoPnt.asJtsPoint().buffer(width))) {
        result.add(marker);
      }
    }
    return result;
  }

  /**
   *
   * @param e
   * @param markers
   * @return
   */
  PolyLineMarker getClosesPoint(LayerMouseEvent e, List<PolyLineMarker> markers) {
    PolyLineMarker result = null;
    double minDistance = Double.NaN;
    ScreenPoint mouseScnPt = new ScreenPoint(e.mouseEvt.getX(), e.mouseEvt.getY());
    ScreenEnvelope screenEnv = e.screenEnv;
    VirtualPoint virtualPt = e.projector.projectScreenToVirtual(mouseScnPt, screenEnv);
    for (PolyLineMarker marker : markers) {
      FxPoint geoPnt = e.projector.projectVirtualToGeo(virtualPt.asPoint(), marker.getSpatialRef());
      double d = marker.getJtsGeometry().distance(geoPnt.asJtsPoint());
      if (Double.isNaN(minDistance) || d < minDistance) {
        minDistance = d;
        result = marker;
      }
    }
    return result;
  }

  /**
   *
   * @param userObj
   */
  public void selectByUserObject(Object userObj) {
    if (userObj != null) {
      List<PolyLineMarker<T>> list = this.source.markers().stream()
        .filter((m) -> Objects.equals(m.getUserObject(), userObj))
        .collect(Collectors.toList());
      if (!list.isEmpty()
        && (this.selectedProperty().get() == null || this.isNotSelected(userObj))) {
        ObservableList<PolyLineMarker<T>> newlist = FXCollections.observableArrayList(list);
        this.selectedProperty().setValue(newlist);
      }

    } else {
      ObservableList<PolyLineMarker<T>> newlist = FXCollections.observableArrayList();
      this.selectedProperty().setValue(newlist);
    }

  }

  /**
   *
   * @param userObj
   * @return
   */
  public boolean isSelected(Object userObj) {
    boolean result = this.selectedProperty().get().stream()
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
   *
   */
  private void removeInvalidSelected() {
    List<PolyLineMarker<T>> selectedMarkers = this.selectedProperty().getValue();
    List<PolyLineMarker<T>> valid = selectedMarkers.stream()
      .filter(this.source::contains)
      .collect(Collectors.toList());
    this.selectedProperty().setValue(FXCollections.observableArrayList(valid));
  }

  /**
   *
   */
  private static class DrawDelegate {

    private final PolyLineLayer host;

    private final DrawArgs args;

    /**
     *
     * @param host
     * @param args
     */
    DrawDelegate(PolyLineLayer host, DrawArgs args) {
      this.host = host;
      this.args = args;
    }

    /**
     *
     * @param m
     */
    private void drawMarker(PolyLineMarker m) {

      List<PolyLineMarker> list = (List<PolyLineMarker>) this.host.hoverSelect.selected().get();
      if (!list.contains(m)) {
        this.host.symbology.apply(this.host, m, args);
      } else {
        this.host.symbology.applySelected(this.host, m, args);
      }
    }
  }

}
