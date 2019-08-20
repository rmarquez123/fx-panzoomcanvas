package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author rmarquez
 */
public final class ArrayPointsSource<T> extends BasePointsSource<T> {

  private final ObservableList<PointMarker<T>> points;
  
  /**
   *
   * @param point
   */
  public ArrayPointsSource(PointMarker<T> point) {
    this(Arrays.asList(point));
  }
  
  /**
   *
   * @param point
   */
  public ArrayPointsSource(SpatialRef sptialRef) {
    super(sptialRef);
    this.points = FXCollections.observableArrayList();
  }

  /**
   *
   * @param points
   */
  public ArrayPointsSource(PointMarker<T>[] points) {
    super(FxPoint.getSpatialRef(PointMarker.getPoints(points)));
    this.points = FXCollections.observableArrayList(points);
  }
  
  /**
   *
   * @param points
   */
  public ArrayPointsSource(List<PointMarker<T>> points) {
    super(FxPoint.getSpatialRef(PointMarker.getPoints(points)));
    this.points = FXCollections.observableArrayList(points);
    
  }
    
  /**
   * 
   * @return 
   */
  @Override
  public ObservableList<PointMarker<T>> pointMarkersProperty() {
    return this.points;
  }
  

}
