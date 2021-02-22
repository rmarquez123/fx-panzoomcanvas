package com.rm.panzoomcanvas.layers.vectors;

import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.Point;
import com.rm.panzoomcanvas.core.PointArrays;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import com.rm.panzoomcanvas.layers.points.PointSymbology;
import com.rm.panzoomcanvas.layers.points.PointsLayer;
import com.rm.panzoomcanvas.projections.Projector;
import java.util.Objects;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Ricardo Marquez
 */
public class VectorPointSymbology implements PointSymbology {

  private final Function<? super Object, VectorDisplayInfo> displaySupplier;
  private final Property<Color> colorProperty;

  /**
   *
   * @param dispalySupplier
   */
  public VectorPointSymbology(Function<? super Object, VectorDisplayInfo> dispalySupplier, Property<Color> colorProperty) {
    this.displaySupplier = dispalySupplier;
    this.colorProperty = colorProperty;
  }

  /**
   *
   * @param layer
   * @param marker
   * @param args
   * @param screenPoint
   */
  @Override
  public void apply(PointsLayer<?> layer, PointMarker<?> marker, DrawArgs args, ScreenPoint screenPoint) {
    GraphicsContext g = ((Canvas) args.getLayerCanvas()).getGraphicsContext2D();
    
    double x1 = screenPoint.getX();
    double y1 = screenPoint.getY();
    Object userObject = marker.getUserObject();
    VectorDisplayInfo info = this.displaySupplier.apply(userObject);
    double dx = info.u;
    double dy = info.v;
    SpatialRef sr = info.sr;
    Projector projector = args.getCanvas().getProjector();
    FxPoint p = projector.getGeomProject().project(marker.getPoint(), sr);
    p = p.add(info.scale * dx, info.scale * dy);
    ScreenPoint s = projector.projectGeoToScreen(p, args.getScreenEnv());
    g.setLineWidth(2);
    g.setStroke(this.colorProperty.getValue());
    g.strokeLine(x1, y1, s.getX(), s.getY());

    double h = 8;
    double b = 0.75*h;
    Point p1 = new Point(0.5 * b, -0.5 * h);
    Point p2 = new Point(-0.5 * b, -0.5 * h);
    Point p3 = new Point(0.0, 0.5 * h);
    double theta = -Math.atan2(info.v, info.u) - Math.PI / 2.0;
    p1 = p1.rotate(theta).add(new Point(s.getX(), s.getY()));
    p2 = p2.rotate(theta).add(new Point(s.getX(), s.getY()));
    p3 = p3.rotate(theta).add(new Point(s.getX(), s.getY()));
    PointArrays arr = Point.toArrays(p1, p2, p3, p1);
    
    g.setFill(this.colorProperty.getValue());
    g.fillPolygon(arr.xarray, arr.yarray, arr.length);
    
  }

  /**
   *
   */
  public static class Builder {

    private Function<? super Object, VectorDisplayInfo> displaySupplier;
    private Property<Color> colorProperty = new SimpleObjectProperty<>(Color.BLUEVIOLET);

    /**
     *
     * @param dispalySupplier
     * @return
     */
    public Builder setDispalySupplier(Function<? super Object, VectorDisplayInfo> dispalySupplier) {
      this.displaySupplier = dispalySupplier;
      return this;
    }

    public Builder setColorProperty(Property<Color> colorProperty) {
      this.colorProperty = colorProperty;
      return this;
    }
    

    /**
     *
     * @return
     */
    public VectorPointSymbology build() {
      Objects.requireNonNull(this.displaySupplier, "Display Supplier cannot be null");
      VectorPointSymbology result = new VectorPointSymbology(this.displaySupplier, colorProperty);
      return result;
    }
  }

}
