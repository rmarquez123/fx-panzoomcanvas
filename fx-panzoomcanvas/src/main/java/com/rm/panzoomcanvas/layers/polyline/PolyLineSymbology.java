package com.rm.panzoomcanvas.layers.polyline;

import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.layers.DrawArgs;
import com.rm.panzoomcanvas.projections.Projector;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Ricardo Marquez
 */
public class PolyLineSymbology {

  private final Property<Color> colorProperty = new SimpleObjectProperty<>(Color.RED);
  private final Property<Double> widthProperty = new SimpleObjectProperty<>(1.0);

  /**
   * 
   * @return 
   */
  public Property<Color> colorProperty() {
    return this.colorProperty;
  }
  
  /**
   * 
   * @return 
   */
  public Property<Double> widthProperty() {
    return this.widthProperty;
  }
  
  /**
   * 
   * @param host
   * @param m
   * @param args 
   */
  void apply(PolyLineLayer host, PolyLineMarker m, DrawArgs args) {
    GraphicsContext g = ((Canvas) args.getLayerCanvas()).getGraphicsContext2D();
    double[][] doubleArray = new double[m.numPoints][2];
    for (int i = 0; i < m.numPoints; i++) {
      doubleArray[i] = new double[]{m.xArray[i], m.yArray[i]};
    }

    Projector projector = args.getCanvas().getProjector();
    SpatialRef sr = m.getSpatialRef();
    double[][] projected = projector.projectGeoToScreen(args.getScreenEnv(), sr, doubleArray);
    double[] xArray = new double[m.numPoints];
    double[] yArray = new double[m.numPoints];
    for (int i = 0; i < m.numPoints; i++) {
      xArray[i] = projected[i][0];
      yArray[i] = projected[i][1];
    }
    g.setFill(colorProperty.getValue());
    g.setStroke(colorProperty.getValue());
    g.setLineWidth(widthProperty.getValue());
    g.strokePolyline(xArray, yArray, m.numPoints);
  }

  
  /**
   * 
   * @param host
   * @param m
   * @param args 
   */
  void applySelected(PolyLineLayer host, PolyLineMarker m, DrawArgs args) {
    GraphicsContext g = ((Canvas) args.getLayerCanvas()).getGraphicsContext2D();
    double[][] doubleArray = new double[m.numPoints][2];
    for (int i = 0; i < m.numPoints; i++) {
      doubleArray[i] = new double[]{m.xArray[i], m.yArray[i]};
    }

    Projector projector = args.getCanvas().getProjector();
    SpatialRef sr = m.getSpatialRef();
    double[][] projected = projector.projectGeoToScreen(args.getScreenEnv(), sr, doubleArray);
    double[] xArray = new double[m.numPoints];
    double[] yArray = new double[m.numPoints];
    for (int i = 0; i < m.numPoints; i++) {
      xArray[i] = projected[i][0];
      yArray[i] = projected[i][1];
    }
    g.setFill(Color.web("00ddff"));
    g.setStroke(Color.web("00ddff"));
    g.setLineWidth(widthProperty.getValue());
    g.strokePolyline(xArray, yArray, m.numPoints);
  }
  
}
