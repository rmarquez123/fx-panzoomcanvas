package com.rm.panzoomcanvas.tools;

import com.rm.panzoomcanvas.core.FxPoint;

/**
 *
 * @author Ricardo Marquez
 */
public class PointSelectEvent {

  private final FxPoint fxPoint;

  /**
   *
   * @param source
   * @param target
   * @param eventType
   */
  public PointSelectEvent(FxPoint fxPoint) {
    this.fxPoint = fxPoint;
  }

  /**
   *
   * @return
   */
  public FxPoint fxPoint() {
    return fxPoint;
  }

  @Override
  public String toString() {
    return "{" + "fxPoint=" + fxPoint + '}';
  }
  
  

  /**
   *
   */
  public static interface Listener {
    /**
     * 
     * @param evt 
     */
    void handle(PointSelectEvent evt);
  }
}
