package com.rm.panzoomcanvas.layers.polyline;

import com.rm.panzoomcanvas.core.FxEnvelope;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.Marker;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author rmarquez
 */
public class PolyLineMarker<T> implements Marker<T> {
  final double[] xArray;
  final double[] yArray;
  final int numPoints; 
  private final T userObj;
  private final SpatialRef sr;
  private final LineString linestring;
  
  private final StringProperty textProperty;
  
  
  /**
   * 
   * @param x
   * @param y
   * @param numPoints 
   */
  public PolyLineMarker(T userObj, SpatialRef sr, double[] x, double[] y, int numPoints) {
    if (x.length != numPoints) {
      throw new IllegalArgumentException(); 
    }
    if (y.length != numPoints) {
      throw new IllegalArgumentException(); 
    }
    this.xArray = x;
    this.yArray = y;
    this.numPoints = numPoints;
    this.userObj = userObj;
    this.sr = sr;
    GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), this.sr.getSrid());
    Coordinate[] coordinates = new Coordinate[this.numPoints];
    for (int i = 0; i < this.numPoints; i++) {
      double _x = this.xArray[i];
      double _y = this.yArray[i];
      coordinates[i] = new Coordinate(_x, _y);
    }
    this.linestring = factory.createLineString(coordinates);
    this.textProperty = new SimpleStringProperty(String.valueOf(userObj)); 
  }

  public SpatialRef getSpatialRef() {
    return sr;
  }
  
  /**
   * 
   * @param geomPoint
   * @return 
   */
  boolean contains(FxPoint geomPoint) {
    boolean result = linestring.intersects(geomPoint.asJtsPoint());
    return result;
  }

  @Override
  public StringProperty labelProperty() {
    return textProperty;
  }

  @Override
  public T getUserObject() {
    return userObj;
  }

  @Override
  public Geometry getJtsGeometry() {
    return this.linestring;
  }

  @Override
  public FxEnvelope getFxEnvelope() {
    return FxEnvelope.fromJtsEnvelope(this.linestring.getEnvelopeInternal(), this.sr);
  }

  @Override 
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.userObj);
    return hash;
  }

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
    final PolyLineMarker<?> other = (PolyLineMarker<?>) obj;
    if (!Objects.equals(this.userObj, other.userObj)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "PolyLineMarker{" + "userObj=" + userObj + '}';
  }
  
  
}
