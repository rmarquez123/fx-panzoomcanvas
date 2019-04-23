package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.layers.points.PointMarker;
import java.util.function.Function;
import javafx.util.Pair;

/**
 *
 * @author Ricardo Marquez
 */
public interface Evaluator<T> extends Function<Pair<PointMarker<?>, T>, Double>{
  
}
