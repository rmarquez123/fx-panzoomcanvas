package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import com.rm.panzoomcanvas.layers.points.PointSymbology;
import com.rm.panzoomcanvas.layers.points.PointsLayer;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 *
 * @author Ricardo Marquez
 */
public class PointValueSymbology<T> implements PointSymbology {
  private final Property<T> parameterProperty = new SimpleObjectProperty<>(); 
  private final Property<ValueMarkerSymbology<T>> markerSymbology = new SimpleObjectProperty<>();
  private final PointShapeSymbology delegate;
  private final Cache<T> cache = new Cache<>();
  private DrawArgs lastDrawArgs;
  
  /**
   * 
   */
  public PointValueSymbology(T initialValue) {
    this.delegate = new PointShapeSymbology();
    this.parameterProperty.setValue(initialValue);
    this.delegate.markerSymbology().setValue((Function<PointMarker<?>, PointShapeSymbology>) (t) -> {
      if (!cache.contains(t, parameterProperty.getValue())) {
        T value1 = parameterProperty.getValue(); 
        Pair<PointMarker<?>, T> pair = new Pair<>(t, value1);
        PointShapeSymbology symbology = markerSymbology.getValue().apply(pair);
        this.cache.put(t, value1, symbology); 
      } 
      PointShapeSymbology result = this.cache.get(t, parameterProperty.getValue()); 
      return result;
    });
    
    this.parameterProperty.addListener((obs, old, change)->{
      if (this.lastDrawArgs != null) {
        this.lastDrawArgs.getCanvas().getContent().redraw();
      }
      this.cache.clear();
    });
  }

  public Property<ValueMarkerSymbology<T>> markerSymbology() {
    return markerSymbology;
  }
  
  
  /**
   * 
   * @return 
   */
  public Property<T> parameterProperty() {
    return this.parameterProperty;
  }
  /**
   * 
   * @param layer
   * @param marker
   * @param args
   * @param screenPoint 
   */
  @Override
  public void apply(PointsLayer<?> layer, PointMarker<?> marker, 
    DrawArgs args, ScreenPoint screenPoint) {
    this.delegate.apply(layer, marker, args, screenPoint);
    this.lastDrawArgs = args;
  }
  
}
