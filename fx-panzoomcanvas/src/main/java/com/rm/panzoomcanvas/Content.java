package com.rm.panzoomcanvas;

import com.rm.panzoomcanvas.core.ScreenEnvelope;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.VirtualEnvelope;
import com.rm.panzoomcanvas.projections.Projector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author rmarquez
 */
public class Content {
  
  private FxCanvas canvas;
  private final Map<Long, Node> nodes = new HashMap<>();
  private final ListProperty<Layer> layers = new SimpleListProperty<>(FXCollections.observableArrayList());
  /**
   *
   */
  public Content() {
    this.layers.addListener((ListChangeListener.Change<? extends Layer> c) -> onLayerItemsChanged(c));
    this.layers.addListener((observable, oldValue, newValue) -> onLayerListChanged());
  }

  /**
   *
   * @return
   */
  public ListProperty<Layer> getLayers() {
    return layers;
  }

  /**
   *
   * @param canvas
   */
  void setVirtualCanvas(FxCanvas canvas) {
    this.canvas = canvas;
    ReadOnlyProperty<VirtualEnvelope> envelope = this.canvas.virtualEnvelopeProperty();
    envelope.addListener((e) -> redraw());
    redraw();
  }

  /**
   *
   */
  public void redraw() {
    if (Platform.isFxApplicationThread()) {
      if (this.canvas != null) {
        this.layers.stream().forEach((l) -> l.redraw(this.canvas));
      }
    } else {
      Platform.runLater(() -> {
        if (this.canvas != null) {
          this.layers.stream().forEach((l) -> l.redraw(this.canvas));
        }
      });
    }

  }

  /**
   *
   * @param c
   */
  private void onLayerItemsChanged(ListChangeListener.Change<? extends Layer> c) {
    while (c.next()) {
      if (c.wasAdded()) {
        c.getAddedSubList().stream().forEach((layer) -> {
          if (layer == null) {
            throw new RuntimeException("Layer cannot be null");
          }
          layer.redraw(this.canvas);
          layer.visibleProperty().addListener((obs, old, change) -> {
            Node get = this.nodes.get(layer.getUuid());
            if (get != null) {
              get.setVisible(change);
            }
          });
        });
      } else if (c.wasRemoved()) {
        c.getRemoved().stream()
          .forEach((t) -> t.purge(this.canvas));
        
      }
    }
  }

  /**
   *
   */
  private void onLayerListChanged() {
    if (this.canvas != null) {
      this.layers.forEach((t) -> {
        t.redraw(this.canvas);
      });

    }
  }

  /**
   *
   * @param uuid
   * @param layerCanvas
   */
  public void addLayerCanvas(long uuid, Node layerCanvas) {
    this.canvas.addLayerCanvas(layerCanvas);
    this.nodes.put(uuid, layerCanvas);
    this.layers.filtered((Layer t) -> t.getUuid() == uuid).forEach((t) -> {
      layerCanvas.setVisible(t.visibleProperty().getValue());
    });
  }

  /**
   *
   * @param uuid
   * @param layerCanvas
   */
  public void removeLayerCanvas(long uuid, Node layerCanvas) {
    this.canvas.removeLayerCanvas(layerCanvas);
    this.nodes.remove(uuid, layerCanvas);
  }

  /**
   *
   * @param s
   * @return
   */
  List<Layer> getSelectableLayers(ScreenPoint s) {
    ScreenEnvelope screenEnv = this.canvas.screenEnvelopeProperty().getValue();
    Projector projector = this.canvas.getProjector();
    ParamsIntersects args = new ParamsIntersects(s, projector, screenEnv);
    ListProperty<Layer> listProp = this.getLayers();
    List<Layer> result = listProp.getValue()
            .stream()
            .filter((l) -> l.selectableProperty().getValue())
            .filter(new LayerIntersectsPoint(args))
            .collect(Collectors.toList());
    return result;
  }
  
  /**
   *
   * @param s
   * @return
   */
  List<Layer> getHoverableLayers() {
    ListProperty<Layer> listProp = this.getLayers();
    List<Layer> result = listProp.getValue()
            .stream()
            .filter((l) -> l.hoverableProperty().getValue())
            .collect(Collectors.toList());
    return result;
  }

  /**
   *
   * @param layers
   */
  void onLayersMouseClicked(MouseEvent e, List<Layer> layers) {
    Projector projector = this.canvas.getProjector();
    ScreenEnvelope screenEnv = this.canvas.screenEnvelopeProperty().getValue();
    LayerMouseEvent layerMouseEvent = new LayerMouseEvent(e, projector, screenEnv);
    for (Layer layer : layers) {
      layer.onMouseClicked(layerMouseEvent);
    }
  }

  void onLayersMouseHovered(MouseEvent e, List<Layer> layers) {
    Projector projector = this.canvas.getProjector();
    ScreenEnvelope screenEnv = this.canvas.screenEnvelopeProperty().getValue();
    LayerMouseEvent layerMouseEvent = new LayerMouseEvent(e, projector, screenEnv);
    for (Layer layer : layers) {
      layer.onMouseHovered(layerMouseEvent);
    }
  }
  

}
