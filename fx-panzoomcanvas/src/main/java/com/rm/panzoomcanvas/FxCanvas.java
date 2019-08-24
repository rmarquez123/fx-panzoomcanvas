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
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
  private final Property<StackPane> mapToolsPaneProperty = new SimpleObjectProperty<>();
  private final Property<Cursor> toolCursor = new SimpleObjectProperty<>();

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
    this.toolCursor.addListener((obs, old, change) -> {
      if (change != null) {
        this.getParent().setCursor(change);
      } else {
        this.getParent().setCursor(Cursor.DEFAULT);
      }
    });
  }

  /**
   *
   * @return
   */
  public final ScreenPoint getCenterOfScreenPoint() {
    return new ScreenPoint(0.5 * this.getWidth(), 0.5 * this.getHeight());
  }

  public void setToolCursor(Cursor cursor) {
    this.toolCursor.setValue(cursor);
  }

  public ReadOnlyProperty<Cursor> toolCursor() {
    return toolCursor;
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
    if (value != null) {
      int levelInt = this.level.getValue().getValue();
      this.zoomToVirtualPoint(levelInt, value);
    }
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
   * @param mapToolNode
   */
  public void addTool(Node mapToolNode) {
    if (this.mapToolsPaneProperty.getValue() != null) {
      this.mapToolsPaneProperty.getValue().getChildren().add(mapToolNode);
    } else {
      this.mapToolsPaneProperty.addListener((obs, old, change) -> {
        this.mapToolsPaneProperty.getValue().getChildren().add(mapToolNode);
      });
    }
  }

  /**
   *
   * @param layerCanvas
   */
  void addLayerCanvas(Node layerCanvas) {
    ((StackPane) this.getParent()).getChildren().add(layerCanvas);
    StackPane.setAlignment(layerCanvas, Pos.TOP_LEFT);
    StackPane pane = this.mapToolsPaneProperty.getValue();
    if (pane != null && pane.getParent() != null) {
      pane.toFront();
    }
  }

  /**
   *
   * @param layerCanvas
   */
  void removeLayerCanvas(Node layerCanvas) {
    Parent p = this.getParent();
    if (p != null) {
      ObservableList<Node> children = ((StackPane) p).getChildren();
      try {
        children.remove(layerCanvas);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }

    }
  }

  /**
   *
   * @param newLevel
   * @param oldLevel
   */
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
   * @param mapToolsPane
   */
  private void setToolsPane() {
    if (this.getParent() != null) {
      this.mapToolsPaneProperty.setValue(new StackPane());
      this.setToolsPaneImpl(this.mapToolsPaneProperty.getValue());
    } else {
      this.parentProperty().addListener((obs, old, change) -> {
        this.mapToolsPaneProperty.setValue(new StackPane());
        this.setToolsPaneImpl(this.mapToolsPaneProperty.getValue());
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

    });
  }

  /**
   *
   */
  private void onParentPropertyChanged() {
    Platform.runLater(() -> {
      this.addMouseListeners();
      this.setInitialCenter();
      this.getParent().cursorProperty().addListener((obs, old, change) -> {
        Platform.runLater(() -> {
          if (change == Cursor.DEFAULT && this.toolCursor.getValue() != null) {
            this.getParent().setCursor(this.toolCursor.getValue());
          }
        });
      });
    });
  }

}
