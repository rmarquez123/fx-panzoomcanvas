package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.layers.points.PointMarker;
import common.colormap.ColorModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 *
 * @author Ricardo Marquez
 */
public final class ColorValueSymbology<T> implements ValueMarkerSymbology<T> {

  private final Property<ColorModel> colorModelProperty = new SimpleObjectProperty<>();

  private final Property<Evaluator<T>> evaluatorProperty = new SimpleObjectProperty<>();

  public ColorValueSymbology(ColorModel model, Evaluator<T> evaluator) {
    this.setColorModel(model);
    this.evaluatorProperty.setValue(evaluator);
  }

  /**
   *
   * @param colorModel
   */
  public void setColorModel(ColorModel colorModel) {
    this.colorModelProperty.setValue(colorModel);
  }

  /**
   *
   * @return
   */
  public ColorModel getColorModel() {
    return colorModelProperty.getValue();
  }

  /**
   *
   * @return
   */
  public Property<ColorModel> colorModelProperty() {
    return this.colorModelProperty;
  }

  /**
   *
   * @param colorModel
   */
  public void setEvaluator(Evaluator<T> colorModel) {
    this.evaluatorProperty.setValue(colorModel);
  }

  /**
   *
   * @return
   */
  public Evaluator<T> getEvaluator() {
    return this.evaluatorProperty.getValue();
  }

  /**
   *
   * @return
   */
  public Property<Evaluator<T>> evaluatorProperty() {
    return this.evaluatorProperty;
  }

  /**
   *
   * @param t
   * @return
   */
  @Override
  public PointShapeSymbology apply(Pair<PointMarker<?>, T> t) {
    PointShapeSymbology symbology1 = new PointShapeSymbology();
    if (this.getEvaluator() != null && this.getColorModel() != null) {
      Double value = this.getEvaluator().apply(t);
      Color color = this.getColorModel().getColor(value);
      symbology1.fillColorProperty().setValue(color);
      symbology1.strokeColorProperty().setValue(color);
    } 
    return symbology1;

  }
}
