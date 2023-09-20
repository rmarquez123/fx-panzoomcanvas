package com.rm.panzoomcanvas.layers.line;

import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.SpatialUtils;
import com.rm.panzoomcanvas.layers.Marker;
import org.locationtech.jts.geom.Geometry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

/**
 *
 * @author rmarquez
 */
public class LineMarker<T> implements Marker<T> {

  private final T userObject;
  private final FxPoint point1;
  private final FxPoint point2;
  private final Pair<FxPoint, FxPoint> pair;
  private final StringProperty labelProperty = new SimpleStringProperty();

  /**
   *
   * @param object
   * @param point1
   * @param point2
   */
  public LineMarker(T object, FxPoint point1, FxPoint point2) {
    this.userObject = object;
    this.point1 = point1;
    this.point2 = point2;
    this.pair = new Pair<>(point1, point2);
    this.labelProperty.setValue(String.valueOf(this.userObject));
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
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public T getUserObject() {
    return this.userObject;
  }

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public Geometry getJtsGeometry() {
    return SpatialUtils.createJtsLine(this.point1, this.point2);
  } 
  
  /**
   * 
   * @return 
   */
  @Override
  public FxEnvelope getFxEnvelope() {
    return new FxEnvelope(point1, point2);
  }
  
  

  /**
   *
   * @return
   */
  public Pair<FxPoint, FxPoint> getPoints() {
    return this.pair;
  }

  @Override
  public String toString() {
    return "LineMarker{" + "object=" + userObject + ", point1=" + point1 + ", point2=" + point2 + '}';
  }

}
