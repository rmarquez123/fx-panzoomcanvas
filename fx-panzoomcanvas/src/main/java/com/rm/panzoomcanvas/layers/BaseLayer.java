package com.rm.panzoomcanvas.layers;

import com.rm.panzoomcanvas.Content;
import com.rm.panzoomcanvas.FxCanvas;
import com.rm.panzoomcanvas.Layer;
import com.rm.panzoomcanvas.LayerGeometry;
import com.rm.panzoomcanvas.LayerMouseEvent;
import com.rm.panzoomcanvas.core.ScreenEnvelope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * @author rmarquez
 */
public abstract class BaseLayer implements Layer {

  private final String name;
  private final BooleanProperty visibleProperty = new SimpleBooleanProperty(true);
  private final BooleanProperty selectableProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty hoverableProperty = new SimpleBooleanProperty(false);
  private final long uuid = (long) (Math.random() * 100000);
  private final LayerGeometry geometry;
  private Node layerCanvas = null;
  private FxCanvas canvas;
  private final MouseEventProperties mouseEvtProps = new MouseEventProperties();

  /**
   *
   * @param name
   * @param layerGeometry
   */
  public BaseLayer(String name, LayerGeometry layerGeometry) {
    this.name = name;
    this.geometry = layerGeometry;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final BooleanProperty visibleProperty() {
    return this.visibleProperty;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final BooleanProperty selectableProperty() {
    return this.selectableProperty;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final BooleanProperty hoverableProperty() {
    return this.hoverableProperty;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final long getUuid() {
    return uuid;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public Content getContent() {
    return canvas == null ? null : this.canvas.getContent();
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final void redraw(FxCanvas canvas) {
    if (canvas != null) {
      this.clearCanvas(canvas);
      Parent p = canvas.getParent();
      if (p != null) {
        ScreenEnvelope layerScreenEnv = this.onGetScreenEnvelope(canvas);
        if (layerScreenEnv != null) {
          double areaX = layerScreenEnv.getMin().getX();
          double areaY = layerScreenEnv.getMin().getY();
          double areaWidth = layerScreenEnv.getWidth();
          double areaHeight = layerScreenEnv.getHeight();
          double width = canvas.getWidth();
          double height = canvas.getHeight();
          this.layerCanvas = this.createLayerCanvas(width, height);
          canvas.getContent().addLayerCanvas(this.uuid, this.layerCanvas);
          DrawArgs args = new DrawArgs(canvas, this.layerCanvas, areaX, areaY, areaHeight, areaWidth);
          this.onDraw(args);
        }
      }
      this.canvas = canvas;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final void purge(FxCanvas virtualCanvas) {
    this.clearCanvas(virtualCanvas);
  }

  /**
   *
   * @return
   */
  protected final Node getLayerCanvas() {
    return layerCanvas;
  }

  /**
   *
   */
  protected final void repaint() {
    if (this.canvas != null) {
      this.redraw(canvas);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public final LayerGeometry getLayerGeometry() {
    return this.geometry;
  }

  /**
   *
   * @return
   */
  public MouseEventProperties getMouseEvtProps() {
    return mouseEvtProps;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: Default implementation does nothing on mouse event. </p>
   */
  @Override
  public final void onMouseClicked(LayerMouseEvent evt) {
    if (this.visibleProperty.get()) {
      this.mouseEvtProps.trigger(MouseEventProperties.MouseEvent.CLICKED, evt);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: Default implementation does nothing on mouse event. </p>
   */
  @Override
  public final void onMouseHovered(LayerMouseEvent event) {
    this.mouseEvtProps.trigger(MouseEventProperties.MouseEvent.HOVERED, event);
  }

  /**
   *
   * @param canvas
   * @return
   */
  protected abstract ScreenEnvelope onGetScreenEnvelope(FxCanvas canvas);

  /**
   *
   * @param args
   */
  protected abstract void onDraw(DrawArgs args);

  /**
   *
   * @param width
   * @param height
   * @return
   */
  protected abstract Node createLayerCanvas(double width, double height);

  /**
   *
   */
  private void clearCanvas(FxCanvas contentCanvas) {
    if (contentCanvas != null && this.layerCanvas != null) {
      contentCanvas.getContent().removeLayerCanvas(this.uuid, this.layerCanvas);
      this.layerCanvas = null;
    }
  }
}
