package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import com.rm.panzoomcanvas.layers.points.PointSymbology;
import com.rm.panzoomcanvas.layers.points.PointsLayer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 *
 * @author rmarquez
 */
public class PointShapeSymbology implements PointSymbology {

  private final Property<PointShape> pointShapeProperty = new SimpleObjectProperty<>(PointShape.CIRCLE);
  private final Property<Color> fillColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private final Property<Color> strokeColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private final Property<Integer> lineWidthProperty = new SimpleObjectProperty<>(2);
  private final Property<Function<PointMarker<?>, PointShapeSymbology>> markerSymbology
    = new SimpleObjectProperty<>();

  private final PointShapeSymbology selected;
  private final PointShapeSymbology hovered;
  private final Map<String, Property<?>> properties = new HashMap<>();
  private final Predicate<Pair<PointsLayer<?>, PointMarker<?>>> hoverableOrSelectablePredicate;
  
  /**
   *
   */
  public PointShapeSymbology() {
    this(null);
  }

  /**
   *
   */
  public Property<Function<PointMarker<?>, PointShapeSymbology>> markerSymbology() {
    return this.markerSymbology;
  }

  /**
   *
   * @param hoverOrSelectablePredicte
   */
  private PointShapeSymbology(Predicate<Pair<PointsLayer<?>, PointMarker<?>>> hoverOrSelectablePredicte) {
    if (hoverOrSelectablePredicte == null) {
      this.hovered = new PointShapeSymbology(new HoveredPredicate());
      this.hovered.fillColorProperty.setValue(null);
      this.hovered.lineWidthProperty.setValue(4);
      this.hovered.pointShapeProperty.setValue(null);
      this.hovered.strokeColorProperty.setValue(null);
      this.selected = new PointShapeSymbology(new SelectedPredicate());
      this.selected.fillColorProperty.setValue(Color.CYAN);
      this.selected.lineWidthProperty.setValue(null);
      this.selected.pointShapeProperty.setValue(null);
      this.selected.strokeColorProperty.setValue(null);
    } else {
      this.hovered = null;
      this.selected = null;
    }
    this.properties.put("pointShape", this.pointShapeProperty);
    this.properties.put("fillColor", this.fillColorProperty);
    this.properties.put("strokeColor", this.strokeColorProperty);
    this.properties.put("lineWidth", this.lineWidthProperty);
    this.hoverableOrSelectablePredicate = hoverOrSelectablePredicte;
  }

  /**
   *
   * @param props
   * @return
   */
  private static PointShapeSymbology create(Map<String, Property<?>> props) {
    PointShapeSymbology result = new PointShapeSymbology();
    result.fillColorProperty.setValue((Color) props.get("fillColor").getValue());
    result.strokeColorProperty.setValue((Color) props.get("strokeColor").getValue());
    result.lineWidthProperty.setValue((Integer) props.get("lineWidth").getValue());
    result.pointShapeProperty.setValue((PointShape) props.get("pointShape").getValue());
    return result;
  }

  /**
   *
   * @return
   */
  public PointShapeSymbology getSelected() {
    return selected;
  }

  /**
   *
   * @return
   */
  public PointShapeSymbology getHovered() {
    return hovered;
  }

  /**
   *
   * @return
   */
  public Property<Color> fillColorProperty() {
    return fillColorProperty;
  }

  /**
   *
   * @return
   */
  public Property<Color> strokeColorProperty() {
    return strokeColorProperty;
  }

  /**
   *
   * @return
   */
  public Property<PointShape> pointShapeProperty() {
    return pointShapeProperty;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public void apply(PointsLayer<?> layer, PointMarker<?> marker, DrawArgs args, ScreenPoint screenPoint) {
    PointShapeSymbology symbolizer = this.getMarkerSymbolizer(layer, marker);
    GraphicsContext g = ((Canvas) args.getLayerCanvas()).getGraphicsContext2D();
    double x1 = screenPoint.getX();
    double y1 = screenPoint.getY();
    if (symbolizer.strokeColorProperty.getValue() != null) {
      double radius = 9;
      double half = radius / 2.0;
      g.setStroke(symbolizer.strokeColorProperty.getValue());
      g.setLineWidth(symbolizer.lineWidthProperty.getValue());
      g.strokeOval(x1 - half, y1 - half, radius, radius);
    }
    if (symbolizer.fillColorProperty.getValue() != null) {
      double radius = 8;
      double half = radius / 2.0;
      g.setFill(symbolizer.fillColorProperty.getValue());
      g.fillOval(x1 - half, y1 - half, radius, radius);
    }
  }

  /**
   *
   * @param layer
   * @param marker
   * @return
   */
  private PointShapeSymbology getMarkerSymbolizer(PointsLayer<?> layer, PointMarker<?> marker) {
    PointShapeSymbology refSymbology; 
    if (this.markerSymbology.getValue() != null) {
      refSymbology = this.markerSymbology.getValue().apply(marker);
    } else {
      refSymbology = this;
    }
    PointShapeSymbology hoveredSymbology = override(layer, marker, refSymbology, this.hovered);
    PointShapeSymbology selectedSymbology = override(layer, marker, hoveredSymbology, this.selected);
    return selectedSymbology;

  }

  /**
   *
   * @param layer
   * @param marker
   * @param current
   * @param other
   * @return
   */
  static PointShapeSymbology override(PointsLayer<?> layer,
    PointMarker<?> marker, PointShapeSymbology current, PointShapeSymbology other) {
    PointShapeSymbology result;
    if (other.hoverableOrSelectablePredicate == null
      || other.hoverableOrSelectablePredicate.test(new Pair<>(layer, marker))) {

      HashMap<String, Property<?>> props = new HashMap<>();
      for (String key : current.properties.keySet()) {
        Property<?> currentProp = current.properties.get(key);
        Property<?> otherProp = other.properties.get(key);
        if (otherProp.getValue() != null) {
          props.put(key, otherProp);
        } else {
          props.put(key, currentProp);
        }
      }
      result = PointShapeSymbology.create(props);
    } else {
      result = PointShapeSymbology.create(current.properties);
    }
    return result;
  }
  

}
