package com.rm.panzoomcanvas.layers;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

/**
 *
 * @author rmarquez
 * @param <T>
 */
public final class LayerTooltip {
  
  private final LayerHoverSelect host;
  private final ToolTipHolder holder = new ToolTipHolder();
  private final int heightOffset;
  private final ChangeListener<? super HoveredMarkers<Marker<? extends Object>>> onHoveredListener;
  
  /**
   *
   * @param builder
   * @param host
   */
  private LayerTooltip(Builder builder, LayerHoverSelect host) {
    this.heightOffset = builder.heightOffset;
    this.host = host;
    this.onHoveredListener = (obs, old, change) -> onHovered(change);
    this.host.hovered().addListener(this.onHoveredListener);
  }
  
  /**
   *
   * @return
   */
  int getHeightOffset() {
    return heightOffset;
  }

  /**
   *
   */
  public void destroy() {
    this.host.hovered().removeListener(this.onHoveredListener);
  }

  /**
   *
   * @param hovered
   */
  private synchronized void onHovered(HoveredMarkers<Marker<?>> hovered) {
    if (holder.tooltip != null) {
      holder.tooltip.hide();
    } else {
      Node node = this.host.getNode();
      holder.tooltip = new Tooltip();
      Tooltip.install(node, holder.tooltip);
    }
    List<Marker<?>> markers = new ArrayList<>(hovered.markers);
    Node node = this.host.getNode();
    for (Marker<?> pointMarker : markers) {
      if (node != null) {
        String label = pointMarker.labelProperty().getValue();
        holder.tooltip.textProperty().setValue(label);
        holder.tooltip.getScene().cursorProperty().bind(node.getParent().cursorProperty());
        double x = hovered.mouseEvent.mouseEvt.getScreenX();
        double y = hovered.mouseEvent.mouseEvt.getScreenY();
        holder.tooltip.show(node, x, y - this.getHeightOffset());
      }
    }
  }

  /**
   *
   */
  public static class Builder {
    
    private int heightOffset = 44;
    /**
     *
     * @param heightOffset
     * @return same but modified instance
     */
    public Builder setHeightOffset(int heightOffset) {
      this.heightOffset = heightOffset;
      return this;
    }

    /**
     *
     * @param host
     * @return
     */
    public LayerTooltip build(LayerHoverSelect host) {
      return new LayerTooltip(this, host);
    }
  }

  private static class ToolTipHolder {

    Tooltip tooltip = null;

    public ToolTipHolder() {
    }

  }
}
