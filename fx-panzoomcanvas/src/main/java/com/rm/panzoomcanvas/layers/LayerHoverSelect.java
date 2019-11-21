package com.rm.panzoomcanvas.layers;

import com.rm.panzoomcanvas.LayerMouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;

/**
 *
 * @author rmarquez
 * @param <TMarker>
 * @param <TObj>
 */
public abstract class LayerHoverSelect<TMarker extends Marker<TObj>, TObj> {

  private final ListProperty<TMarker> selected = new SimpleListProperty<>(FXCollections.emptyObservableList());
  private final Property<HoveredMarkers<TMarker>> hovered = new SimpleObjectProperty<>();
  private final LayerCursorHelper<TObj> cursorHelper;
  public final BaseLayer host;
  private final BooleanProperty deSelectOnClickProperty = new SimpleBooleanProperty(true);

  /**
   *
   * @param host
   */
  public LayerHoverSelect(BaseLayer host) {
    this.host = host;
    this.cursorHelper = new LayerCursorHelper<>(this);
    this.initMouseEvents();
  }

  /**
   *
   */
  private void initMouseEvents() {
    MouseEventProperties.MouseEvent HOVERED = MouseEventProperties.MouseEvent.HOVERED;
    this.host.getMouseEvtProps().addListener(HOVERED, (type, event) -> {
      this.onMouseHovered(event);
    });
    MouseEventProperties.MouseEvent CLICKED = MouseEventProperties.MouseEvent.CLICKED;
    this.host.getMouseEvtProps().addListener(CLICKED, (type, event) -> {
      this.onMouseClicked(event);
    });
  }

  public BooleanProperty selectableProperty() {
    return this.host.selectableProperty();
  }

  public ListProperty<TMarker> selected() {
    return selected;
  }

  public Property<HoveredMarkers<TMarker>> hovered() {
    return hovered;
  }

  /**
   *
   * @return
   */
  public Node getNode() {
    return this.host.getLayerCanvas();
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  public void onMouseClicked(LayerMouseEvent e) {
    List<TMarker> newVal = new ArrayList<>(this.getMouseEvtList(e));
    if (deSelectOnClick()) {
      newVal.removeIf((TMarker t) -> selected.contains(t));
    }
    this.selected.setValue(FXCollections.observableArrayList(newVal));
  }

  /**
   *
   * @return
   */
  public boolean deSelectOnClick() {
    return deSelectOnClickProperty.get();
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  public void onMouseHovered(LayerMouseEvent e) {
    List<TMarker> newVal = new ArrayList<>(this.getMouseEvtList(e));
    HoveredMarkers<TMarker> oldVal = this.hovered.getValue();
    HoveredMarkers<TMarker> result = new HoveredMarkers<>(e, newVal);
    this.hovered.setValue(result);
    this.repaintIfHoveredListChanged(oldVal == null ? Collections.EMPTY_LIST : oldVal.markers, newVal);
  }

  /**
   *
   * @param oldVal
   * @param newVal
   */
  private void repaintIfHoveredListChanged(List<TMarker> oldVal, List<TMarker> newVal) {
    boolean changed;
    HoveredMarkers<TMarker> currentHoveredVal = this.hovered.getValue();
    if (currentHoveredVal == null) {
      changed = true;
    } else {
      changed = !listEqualsIgnoreOrder(oldVal, newVal);
    }
    if (changed) {
      this.host.repaint();
    }
  }

  /**
   *
   * @param <T>
   * @param list1
   * @param list2
   * @return
   */
  private static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
    return new HashSet<>(list1).equals(new HashSet<>(list2));
  }

  /**
   *
   * @param e
   * @return
   */
  protected abstract List<TMarker> getMouseEvtList(LayerMouseEvent e);
  
  /**
   * 
   * @return 
   */
  public BooleanProperty deSelectOnClickProperty() {
    return this.deSelectOnClickProperty;
  }

}
