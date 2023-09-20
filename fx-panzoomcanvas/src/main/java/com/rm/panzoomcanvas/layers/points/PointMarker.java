package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.layers.Marker;
import org.locationtech.jts.geom.Geometry;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

/**
 *
 * @author rmarquez
 * @param <T>
 */
public final class PointMarker<T> implements Marker<T> {

  private final T userObject;
  private final FxPoint point;
  private final StringProperty labelProperty = new SimpleStringProperty();

  /**
   *
   * @param userObject
   * @param point
   */
  public PointMarker(T userObject, FxPoint point) {
    this.userObject = userObject;
    this.point = point;
    this.labelProperty.setValue(String.valueOf(userObject));
  }

  /**
   *
   * @param <T>
   * @param pointMarkers
   * @return
   */
  public static <T> FxPoint[] getPoints(PointMarker<T>[] pointMarkers) {
    FxPoint[] result = new FxPoint[pointMarkers.length];
    for (int i = 0; i < pointMarkers.length; i++) {
      result[i] = pointMarkers[i].getPoint();
    }
    return result;
  }
  
  /**
   *
   * @param <T>
   * @param pointMarkers
   * @return
   */
  public static <T> FxPoint[] getPoints(List<PointMarker<T>> pointMarkers) {
    FxPoint[] result = new FxPoint[pointMarkers.size()];
    int i = -1 ;
    for (PointMarker<T> pointMarker : pointMarkers) {
      i++;
      result[i] = pointMarker.getPoint();
    }
    return result;
  }

  /**
   * 
   * @return 
   */
  @Override
  public FxEnvelope getFxEnvelope() {
    return new FxEnvelope(this.point, this.point); 
  }
  
  

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public StringProperty labelProperty() {
    return labelProperty;
  }

  /**
   *
   * @return
   */
  @Override
  public T getUserObject() {
    return userObject;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public Geometry getJtsGeometry() {
    return this.point.asJtsPoint();
  }

  /**
   *
   * @return
   */
  public FxPoint getPoint() {
    return point;
  }

  /**
   *
   * @param stringConverter
   */
  public void setLabelProperty(StringConverter<T> stringConverter) {
    String newLabel = stringConverter.toString(userObject);
    this.labelProperty.setValue(newLabel);
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 53 * hash + Objects.hashCode(this.userObject);
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
    final PointMarker<?> other = (PointMarker<?>) obj;
    if (!Objects.equals(this.userObject, other.userObject)) {
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
    return "PointMarker{" + "userObject=" + userObject + ", point=" + point + '}';
  }

}
