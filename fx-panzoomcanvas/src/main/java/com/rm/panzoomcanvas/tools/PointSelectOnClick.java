package com.rm.panzoomcanvas.tools;

import com.rm.panzoomcanvas.FxCanvas;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.core.VirtualPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Ricardo Marquez
 */
public class PointSelectOnClick {

  private final FxCanvas canvas;
  private final Property<Boolean> activated = new SimpleObjectProperty<>(false);
  private final EventHandler<? super MouseEvent> clickListener;
  private final SpatialRef spatialRef;
  private final List<PointSelectEvent.Listener> listeners = new ArrayList<>();
  private final EventHandler<KeyEvent> keyReleasedListener;

  /**
   *
   * @param canvas
   */
  public PointSelectOnClick(FxCanvas canvas, SpatialRef spatialRef) {
    this.canvas = canvas;
    this.spatialRef = spatialRef;
    this.keyReleasedListener = this::deactiveIfEscapeKey;
    this.clickListener = this::triggerPointSelectEvt;
    this.activated.addListener((obs, old, change) -> {
      if (change) {
        this.activateSelectFunctionality();
      } else {
        this.deactivateSelectFunctionality();
      }
    });
  }

  /**
   *
   * @param listener
   */
  public void addEventListener(PointSelectEvent.Listener listener) {
    this.listeners.add(listener);
  }

  /**
   *
   * @param listener
   */
  public void removeEventListener(PointSelectEvent.Listener listener) {
    this.listeners.remove(listener);
  }

  /**
   *
   * @param activate
   */
  public void activate(boolean activate) {
    this.activated.setValue(activate);
  }

  /**
   *
   * @return
   */
  public ReadOnlyProperty<Boolean> activated() {
    return this.activated;
  }

  /**
   *
   * @return
   */
  public boolean isActivated() {
    return this.activated.getValue();
  }

  /**
   *
   */
  private void activateSelectFunctionality() {
    this.canvas.setToolCursor(Cursor.CROSSHAIR);
    this.canvas.getParent().addEventHandler(MouseEvent.MOUSE_CLICKED, this.clickListener);
    this.canvas.getParent().addEventHandler(KeyEvent.KEY_RELEASED, this.keyReleasedListener);
  }

  /**
   *
   */
  private void deactivateSelectFunctionality() {
    this.canvas.setToolCursor(null);
    this.canvas.getParent().removeEventHandler(MouseEvent.MOUSE_CLICKED, clickListener);
    this.canvas.getParent().removeEventHandler(KeyEvent.KEY_RELEASED, this.keyReleasedListener);
  }

  /**
   *
   * @param e
   */
  private void triggerPointSelectEvt(MouseEvent e) {
    if (this.isActivated()) {
      ScreenPoint p = new ScreenPoint(e.getX(), e.getY());
      VirtualPoint v = this.canvas.getProjector().projectScreenToVirtual(p, this.canvas.screenEnvelopeProperty().getValue());
      FxPoint geoPnt = this.canvas.getProjector().projectVirtualToGeo(v.asPoint(), this.spatialRef);
      PointSelectEvent event = new PointSelectEvent(geoPnt);
      this.dispatchEvent(event);
    }
  }

  /**
   *
   * @param event
   */
  private void dispatchEvent(PointSelectEvent event) {
    List<PointSelectEvent.Listener> copy = new ArrayList<>(this.listeners);
    for (PointSelectEvent.Listener listener : copy) {
      listener.handle(event);
    }
  }

  /**
   *
   * @param e
   */
  private void deactiveIfEscapeKey(KeyEvent e) {
    if (e.getCode() == KeyCode.ESCAPE) {
      this.activate(false);
    }
  }
}
