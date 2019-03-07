package com.rm.panzoomcanvas.layers.points;

import com.rm.panzoomcanvas.core.Point;
import com.rm.panzoomcanvas.core.ScreenPoint;
import com.rm.panzoomcanvas.layers.DrawArgs;
import java.util.function.Function;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author Ricardo Marquez
 */
public class PointsLabel<T> {

  private final Offset offset;
  private final Function<PointMarker<T>, String> textFunction;
  private final Color foregroundColor;
  private final Color backgroundColor;

  public PointsLabel(Offset offset, Function<PointMarker<T>, String> text, Color foregroundColor, Color backgroundColor) {
    this.offset = offset;
    this.textFunction = text;
    this.foregroundColor = foregroundColor;
    this.backgroundColor = backgroundColor;
  }
  
  /**
   *
   * @param layer
   * @param marker
   * @param args
   * @param screenPoint
   */
  public void apply(PointsLayer<T> layer, PointMarker<T> marker, DrawArgs args, ScreenPoint screenPoint){
    String text = this.textFunction.apply(marker); 
    GraphicsContext g = ((Canvas) args.getLayerCanvas()).getGraphicsContext2D();
    double x1 = screenPoint.getX();
    double y1 = screenPoint.getY();
    Point p = this.offset.getPosition(x1, y1);
    g.setFill(this.backgroundColor);
    Text t = new Text(text);
    t.setFont(g.getFont());
    double width = t.getBoundsInLocal().getWidth() + 4;
    double height = t.getBoundsInLocal().getHeight() + 6;
    g.fillRect(p.getX() - 2, p.getY() - height + 6, width, height);
    g.setFill(this.foregroundColor);
    g.fillText(text, p.getX(), p.getY());
    
    
  }
  
  /**
   * 
   * @param <T> 
   */
  public static class Builder<T> {

    private Offset offset;
    private Function<PointMarker<T>, String> text;
    private Color foregroundColor;
    private Color backgroundColor;

    public Builder<T> setOffset(Offset offset) {
      this.offset = offset;
      return this;
    }

    public Builder<T> setText(Function<PointMarker<T>, String> text) {
      this.text = text;
      return this;
    }

    public Builder<T> setForegroundColor(Color foregroundColor) {
      this.foregroundColor = foregroundColor;
      return this;
    }

    public Builder<T> setBackgroundColor(Color backgroundColor) {
      this.backgroundColor = backgroundColor;
      return this;
    }

    /**
     *
     * @return
     */
    public PointsLabel<T> build() {
      return new PointsLabel<>(offset, text, foregroundColor, backgroundColor);
    }

  }

}
