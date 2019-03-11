package com.rm.panzoomcanvas;

import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.Level;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.ScrollInvoker;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.layers.Marker;
import com.rm.panzoomcanvas.projections.Projector;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 *
 * @author rmarquez
 */
public class FxCanvas extends Canvas {

  private static final ScreenPoint INITIAL_SCREEN_POINT = new ScreenPoint(0, 0);
  private final Content content;
  private final Property<VirtualEnvelope> virtualEnvelope = new SimpleObjectProperty<>();
  private final Property<ScreenEnvelope> screenEnvelopeProperty = new SimpleObjectProperty<>();
  private final Property<Level> level = new SimpleObjectProperty<>(new Level(0, null));
  private final Property<ScreenPoint> center = new SimpleObjectProperty<>(INITIAL_SCREEN_POINT);
  private final Projector projector;
  private final BooleanProperty scrolling = new SimpleBooleanProperty(false);
  private Property<StackPane> mapToolsPane = new SimpleObjectProperty<>();

  /**
   *
   * @param content
   * @param projector
   */
  public FxCanvas(Content content, Projector projector) {
    if (projector == null) {
      throw new IllegalArgumentException("Projector cannot be null");
    }
    this.projector = projector;
    this.content = content;
    this.widthProperty().addListener((e) -> this.updateScreenView());
    this.heightProperty().addListener((e) -> this.updateScreenView());
    this.center.addListener((e) -> this.updateScreenView());
    this.level.addListener((observable, oldLevel, newLevel) -> {
      this.updateCenterAfterLevelChanged(newLevel, oldLevel);
    });

    this.screenEnvelopeProperty.addListener((e) -> this.updateVirtualView());
    this.parentProperty().addListener((obs, newVal, oldVal) -> {
      onParentPropertyChanged();
    });
    MapBindings.bindLevelScrolling(this);
    MapBindings.bindPanning(this);
    this.content.setVirtualCanvas(this);
    this.setToolsPane();

  }

  private void onParentPropertyChanged() {
    Platform.runLater(() -> {
      this.addMouseListeners();
      this.setInitialCenter();
    });
  }

  /**
   *
   * @return
   */
  public final ScreenPoint getCenterOfScreenPoint() {
    return new ScreenPoint(0.5 * this.getWidth(), 0.5 * this.getHeight());
  }

  /**
   *
   * @return
   */
  public ReadOnlyProperty<ScreenEnvelope> screenEnvelopeProperty() {
    return screenEnvelopeProperty;
  }

  /**
   *
   * @return
   */
  public ReadOnlyProperty<VirtualEnvelope> virtualEnvelopeProperty() {
    return this.virtualEnvelope;
  }

  /**
   *
   * @return
   */
  public Property<Level> levelProperty() {
    return level;
  }

  /**
   *
   * @return
   */
  public Content getContent() {
    return content;
  }

  /**
   *
   * @param envelope
   */
  public void zoomToEnvelope(FxEnvelope envelope) {
    ScreenEnvelope screenEnv = this.screenEnvelopeProperty.getValue();
    if (screenEnv != null) {
      ScreenEnvelope projectedEnv = this.projector.projectGeoToScreen(envelope, screenEnv);
      this.screenEnvelopeProperty.setValue(projectedEnv);
    }
  }

  /**
   *
   * @param geometricLayer
   */
  public void zoomToLayer(GeometricLayer geometricLayer) {
    FxPoint value = geometricLayer.centerProperty().getValue();
    int levelInt = this.level.getValue().getValue();
    this.zoomToVirtualPoint(levelInt, value);
  }

  /**
   *
   * @param newLevel
   * @param point
   */
  public void zoomToVirtualPoint(int newLevel, FxPoint point) {
    ScreenEnvelope screenEnv = this.screenEnvelopeProperty.getValue();
    ScreenPoint refPoint = this.getProjector().projectGeoToScreen(point, screenEnv);
    ScreenPoint centerVal = this.getCenterOfScreenPoint();
    ScreenPoint diff = centerVal.difference(refPoint);
    double f = 1;
    ScreenPoint newCenter = this.center.getValue().add(diff.multiply(f));
    this.center.setValue(newCenter);
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        final Level change;
        if (newLevel < level.getValue().getValue()) {
          change = level.getValue().subtractOne(null);
        } else if (newLevel > level.getValue().getValue()) {
          change = level.getValue().addOne(null);
        } else {
          change = null;
        }
        if (change != null) {
          level.setValue(change);
        } else {
          this.stop();
        }
      }
    }.start();

  }

  /**
   *
   * @return
   */
  public Property<ScreenPoint> centerProperty() {
    return center;
  }

  public ReadOnlyBooleanProperty scrollingProperty() {
    return scrolling;
  }

  /**
   *
   * @return
   */
  public Projector getProjector() {
    return projector;
  }

  private void updateCenterAfterLevelChanged(Level newLevel, Level oldLevel) {
    ScreenPoint refPoint;
    ScrollInvoker invoker = newLevel.getInvoker();
    try {
      if (!invoker.isScrollEvent()) {
        refPoint = this.getCenterOfScreenPoint();
      } else {
        this.scrolling.setValue(invoker.isScrolling());
        double x = invoker.getX();
        double y = invoker.getY();
        refPoint = new ScreenPoint(x, y);
      }
      ScreenPoint centerVal = FxCanvas.this.center.getValue();
      ScreenPoint diff = centerVal.difference(refPoint);
      double f = Math.pow(2.0, -(oldLevel.getValue() - newLevel.getValue()));
      ScreenPoint newCenter = refPoint.add(diff.multiply(f));
      FxCanvas.this.center.setValue(newCenter);
    } finally {
      this.scrolling.setValue(Boolean.FALSE);
    }
  }

  /**
   *
   */
  private void addMouseListeners() {
    this.getParent().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
      ScreenPoint s = new ScreenPoint(e.getX(), e.getY());
      List<Layer> layers = this.getContent().getSelectableLayers(s);
      this.getContent().onLayersMouseClicked(e, layers);
    });

    this.getParent().addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
      List<Layer> layers = this.getContent().getHoverableLayers();
      this.getContent().onLayersMouseHovered(e, layers);
    });
  }

  private void setInitialCenter() {
    if (this.center.getValue() == INITIAL_SCREEN_POINT) {
      this.center.setValue(this.getCenterOfScreenPoint());
    }
  }

  /**
   *
   */
  private void updateVirtualView() {
    ScreenEnvelope screenEnvVal = this.screenEnvelopeProperty.getValue();
    VirtualEnvelope newVal = this.projector.projectScreenToVirtualStrict(screenEnvVal);
    this.virtualEnvelope.setValue(newVal);
  }

  /**
   *
   */
  private void updateScreenView() {
    ScreenPoint min = new ScreenPoint(0, 0);
    ScreenPoint max = new ScreenPoint(this.getWidth(), this.getHeight());
    Level levelVal = this.level.getValue();
    ScreenPoint centerVal = this.center.getValue();
    ScreenEnvelope newScreenEnv = new ScreenEnvelope(min, max, levelVal, centerVal);
    this.screenEnvelopeProperty.setValue(newScreenEnv);
  }

  /**
   *
   * @param layerCanvas
   */
  void addLayerCanvas(Node layerCanvas) {
    ((StackPane) this.getParent()).getChildren().add(layerCanvas);
    StackPane.setAlignment(layerCanvas, Pos.TOP_LEFT);
  }

  /**
   *
   * @param layerCanvas
   */
  void removeLayerCanvas(Node layerCanvas) {
    Parent p = this.getParent();
    if (p != null) {
      ((StackPane) p).getChildren().remove(layerCanvas);
    }
  }

  /**
   *
   * @param marker
   * @return
   */
  public boolean isInView(Marker<?> marker) {
    FxEnvelope geometry = marker.getFxEnvelope();
    ScreenEnvelope value = this.screenEnvelopeProperty.getValue();
    ScreenEnvelope markerScreenEnv = this.projector.projectGeoToScreen(geometry, value);
    boolean result = value.contains(markerScreenEnv);
    return result;
  }

  /**
   *
   * @param mapToolsPane
   */
  private void setToolsPane() {
    if (this.getParent() != null) {
      this.mapToolsPane.setValue(new StackPane());
      this.setToolsPaneImpl(this.mapToolsPane.getValue());
    } else {
      this.parentProperty().addListener((obs, old, change) -> {
        this.mapToolsPane.setValue(new StackPane());
        this.setToolsPaneImpl(this.mapToolsPane.getValue());
      });
    }
  }

  /**
   *
   * @param mapToolsPane
   */
  private void setToolsPaneImpl(StackPane mapToolsPane) {
    StackPane root = (StackPane) this.getParent();
    root.getChildren().add(mapToolsPane);
    root.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
      Platform.runLater(() -> {
        mapToolsPane.toFront();
      });
    });
  }

  /**
   *
   * @param positionBar
   */
  public void addTool(Node positionBar) {
    if (this.mapToolsPane.getValue() != null) {
      this.mapToolsPane.getValue().getChildren().add(positionBar);
    } else {
      this.mapToolsPane.addListener((obs, old, change) -> {
        this.mapToolsPane.getValue().getChildren().add(positionBar);
      });
    }
  }

}
