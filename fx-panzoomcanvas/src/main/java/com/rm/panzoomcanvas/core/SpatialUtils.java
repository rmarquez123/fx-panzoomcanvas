package com.rm.panzoomcanvas.core;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

/**
 *
 * @author rmarquez
 */
public class SpatialUtils {

  /**
   *
   */
  private SpatialUtils() {
  }

  /**
   *
   * @param p1
   * @param p2
   * @param buffer
   * @return
   */
  public static boolean intersects(FxPoint p1, FxPoint p2, double buffer) {
    double diffX = p1.getX() - p2.getX();
    double diffY = p1.getY() - p2.getY();
    double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
    boolean result = distance <= buffer;
    return result;
  }

  /**
   *
   * @param geom
   * @param geomPoint
   * @param buffer
   */
  public static boolean intersects(Geometry geom, FxPoint geomPoint, double buffer) {
    Geometry refPoint = geomPoint.asJtsPoint();
    boolean result = geom.buffer(buffer).intersects(refPoint);
    return result;
  }
  

  /**
   *
   * @param point1
   * @param point2
   * @return
   */
  public static Geometry createJtsLine(FxPoint point1, FxPoint point2) {
    int srid = point1.getSpatialRef().getSrid();
    PrecisionModel model = new PrecisionModel(PrecisionModel.FLOATING);
    GeometryFactory geomFactory = new GeometryFactory(model, srid);
    Geometry result = geomFactory.createLineString(new Coordinate[]{
      new Coordinate(point1.getX(), point1.getX()),
      new Coordinate(point2.getX(), point2.getY()),});
    return result;
  }

  /**
   *
   * @param x
   * @param y
   * @param sr
   * @return
   */
  public static Geometry createJtsPoint(double x, double y, SpatialRef sr) {
    PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.FLOATING);
    GeometryFactory geom = new GeometryFactory(precisionModel, sr.getSrid());
    Point jtsPoint = geom.createPoint(new Coordinate(x, y));
    return jtsPoint;
  }
}
