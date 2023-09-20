package com.rm.panzoomcanvas.layers.polygon;

import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.layers.Marker;
import org.locationtech.jts.geom.Geometry;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author rmarquez
 */
public class PolygonMarker<T> implements Marker<T> {
  
  private final T userObject;
  private final StringProperty labelProperty = new SimpleStringProperty();
  private final PolygonPoints points;
  
  /**
   * 
   * @param userObject 
   * @param points 
   */
  public PolygonMarker(T userObject, PolygonPoints points) {
    this.userObject = userObject;
    this.labelProperty.setValue(String.valueOf(userObject));
    this.points = points;
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public StringProperty labelProperty() {
    return this.labelProperty;
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public T getUserObject() {
    return this.userObject;
  }
  
  /**
   * 
   * @return 
   */
  public PolygonPoints getPoints() {
    return points;
  }
  
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public Geometry getJtsGeometry() {
    return this.points.asJtsPolygon();
  }

  @Override
  public FxEnvelope getFxEnvelope() {
    return this.points.getFxEnvelope(); 
  }
  
  
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + Objects.hashCode(this.userObject);
    hash = 59 * hash + Objects.hashCode(this.labelProperty);
    return hash;
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PolygonMarker<?> other = (PolygonMarker<?>) obj;
    if (!Objects.equals(this.userObject, other.userObject)) {
      return false;
    }
    if (!Objects.equals(this.labelProperty, other.labelProperty)) {
      return false;
    }
    return true;
  }
  
  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public String toString() {
    return "PolygonMarker{" + "userObject=" + userObject + ", labelProperty=" + labelProperty + '}';
  }
  
}
