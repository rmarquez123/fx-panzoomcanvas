package com.rm.panzoomcanvas.app;

import com.rm.panzoomcanvas.Content;
import com.rm.panzoomcanvas.FxCanvas;
import com.rm.panzoomcanvas.Layer;
import com.rm.panzoomcanvas.components.CenterLayer;
import com.rm.panzoomcanvas.components.PositionBar;
import com.rm.panzoomcanvas.components.VirtualBoxLayer;
import com.rm.panzoomcanvas.core.FxPoint;
import com.rm.panzoomcanvas.core.GeometryProjection;
import com.rm.panzoomcanvas.core.Point;
import com.rm.panzoomcanvas.core.SpatialRef;
import com.rm.panzoomcanvas.impl.line.DefaultLineSymbology;
import com.rm.panzoomcanvas.impl.line.FixedLineLayerSource;
import com.rm.panzoomcanvas.impl.points.ArrayPointsSource;
import com.rm.panzoomcanvas.impl.points.PointShapeSymbology;
import com.rm.panzoomcanvas.layers.LayerTooltip;
import com.rm.panzoomcanvas.layers.line.LineLayer;
import com.rm.panzoomcanvas.layers.points.Offset.OffsetBuilder;
import com.rm.panzoomcanvas.layers.points.PointMarker;
import com.rm.panzoomcanvas.layers.points.PointsLabel;
import com.rm.panzoomcanvas.layers.points.PointsLayer;
import com.rm.panzoomcanvas.projections.MapCanvasSR;
import com.rm.panzoomcanvas.projections.Projector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The purpose of this main application is to test the basic and default features and
 * functions of panzoomcanvas api.
 *
 * @author rmarquez
 */
public class MainApp extends Application {

  /**
   * {@inheritDoc}
   * <p>
   * OVERRIDE: </p>
   */
  @Override
  public void start(Stage stage) throws Exception {
    FxCanvas mapCanvas = this.createMap();
    this.addLayers(mapCanvas);
    Scene scene = new Scene(mapCanvas.getParent());
    stage.setScene(scene);
    stage.setWidth(500);
    stage.setHeight(500);
    stage.show();
  }

  /**
   *
   * @param mapCanvas
   */
  private void addLayers(FxCanvas mapCanvas) {
    ObservableList<Layer> value = mapCanvas.getContent().getLayers().getValue();
    value.add(new VirtualBoxLayer());
    value.add(new CenterLayer("center"));
    PointsLayer pointsLayer = this.getPointsLayer();
    value.add(pointsLayer);
    LineLayer lineLayer = this.getLineLayer();
    value.add(lineLayer);
    
  }

  /**
   *
   * @param p1
   * @param p2
   * @return
   */
  private LineLayer getLineLayer() {
    final SpatialRef sr = new MapCanvasSR();
    FxPoint p1 = new FxPoint(0.0, 0.0, sr);
    FxPoint p2 = new FxPoint(120.0, 120.0, sr);
    FixedLineLayerSource<String> lineSource = new FixedLineLayerSource<>("line", p1, p2);
    DefaultLineSymbology symbology = new DefaultLineSymbology();
    symbology.strokeColorProperty().setValue(Color.BLACK);
    symbology.lineWidthProperty().setValue(2);
    LineLayer lineLayer = new LineLayer("line", symbology, lineSource);
    lineLayer.selectableProperty().setValue(Boolean.TRUE);
    lineLayer.hoverableProperty().setValue(Boolean.TRUE);
    lineLayer.setTooltip(new LayerTooltip.Builder().setHeightOffset(40));
    return lineLayer;
  }

  /**
   *
   * @return
   */
  private PointsLayer getPointsLayer() {
    FxPoint fxPoint = new FxPoint(120, 120, new MapCanvasSR());
    PointMarker<String> pointMarker = new PointMarker<>("Point 1", fxPoint);
    ArrayPointsSource singlePointSource = new ArrayPointsSource(pointMarker);

    PointShapeSymbology symbology = new PointShapeSymbology();
    symbology.fillColorProperty().setValue(Color.ROSYBROWN);
    symbology.strokeColorProperty().setValue(Color.BLACK);
    symbology.getSelected().fillColorProperty().setValue(Color.RED);
    
    PointsLayer pointsLayer = new PointsLayer("points", symbology, singlePointSource);
    pointsLayer.hoverableProperty().setValue(Boolean.TRUE);
    pointsLayer.selectableProperty().setValue(Boolean.TRUE);
    pointsLayer.setTooltip(new LayerTooltip.Builder()
      .setHeightOffset(54));
    
    pointsLayer.labelProperty().setValue(new PointsLabel.Builder<String>()
      .setOffset(new OffsetBuilder().north(10).east(10).build())
      .setText((PointMarker<String> marker)-> marker.getUserObject())
      .setBackgroundColor(Color.BLACK
      )
      .setForegroundColor(Color.WHITESMOKE)
      .build());
    
    return pointsLayer;
  }

  /**
   * Creates the map.
   *
   * @return
   */
  private FxCanvas createMap() {
    Content content = new Content();
    FxCanvas mapCanvas = new FxCanvas(content, new Projector(new MapCanvasSR(), new GeometryProjection() {
      MapCanvasSR sr = new MapCanvasSR();

      @Override
      public FxPoint project(FxPoint geomPoint, SpatialRef baseSpatialRef) {
        return geomPoint;
      }

      @Override
      public Point getMax() {
        return sr.getMax();
      }

      @Override
      public Point getMin() {
        return sr.getMax();
      }

    }));
    PositionBar positionBar = new PositionBar(mapCanvas);
    StackPane.setAlignment(positionBar, Pos.BOTTOM_RIGHT);
    StackPane root = new StackPane(mapCanvas, positionBar);
    root.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
      Platform.runLater(() -> {
        positionBar.toFront();
      });
    });
    StackPane.setAlignment(mapCanvas, Pos.CENTER);
    mapCanvas.widthProperty().bind(root.widthProperty());
    mapCanvas.heightProperty().bind(root.heightProperty());
    return mapCanvas;
  }

  /**
   * The main() method is ignored in correctly deployed JavaFX application. main() serves
   * only as fallback in case the application can not be launched through deployment
   * artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

}
