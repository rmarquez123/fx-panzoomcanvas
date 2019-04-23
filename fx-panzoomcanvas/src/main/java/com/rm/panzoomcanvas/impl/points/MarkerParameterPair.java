package com.rm.panzoomcanvas.impl.points;

import com.rm.panzoomcanvas.layers.points.PointMarker;
import java.util.Objects;

/**
 *
 * @author Ricardo Marquez
 */
public class MarkerParameterPair {
  PointMarker<?> marker; 
  Object parameter;

  public MarkerParameterPair(PointMarker<?> markerk, Object parameter) {
    this.marker = markerk;
    this.parameter = parameter;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.marker);
    hash = 53 * hash + Objects.hashCode(this.parameter);
    return hash;
  }

  
  
  /**
   * 
   * @param obj
   * @return 
   */
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
    final MarkerParameterPair other = (MarkerParameterPair) obj;
    if (!Objects.equals(this.marker, other.marker)) {
      return false;
    }
    if (!Objects.equals(this.parameter, other.parameter)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "MarkerParameterPair{" + "markerk=" + marker + ", parameter=" + parameter + '}';
  }
  
  
}
