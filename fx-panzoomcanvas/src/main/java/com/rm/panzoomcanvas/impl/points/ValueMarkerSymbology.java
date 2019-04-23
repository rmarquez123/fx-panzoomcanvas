package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.layers.points.PointMarker;
import java.util.function.Function;
import javafx.util.Pair;

/**
 *
 * @author Ricardo Marquez
 */
@FunctionalInterface
public interface ValueMarkerSymbology<T> extends Function<Pair<PointMarker<?>, T>, PointShapeSymbology> {

  
}
