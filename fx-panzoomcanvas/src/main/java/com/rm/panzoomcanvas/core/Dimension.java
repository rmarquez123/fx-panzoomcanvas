package com.rm.panzoomcanvas.core;

import org.locationtech.jts.geom.Envelope;
import java.util.Objects;

/**
 *
 * @author Ricardo Marquez
 */
public class Dimension {

  private final int width;
  private final int height;
  private final Envelope env;
  private static final Dimension EMPTY = new Dimension(0, 0, new Envelope());

  /**
   *
   * @param width
   * @param height
   * @param env
   */
  public Dimension(int width, int height, Envelope env) {
    if (env == null) {
      throw new NullPointerException("Envelope cannot be null");
    }
    this.width = width;
    this.height = height;
    this.env = env;
  }

  /**
   *
   * @param dimension
   * @return
   */
  public static boolean isEmpty(Dimension dimension) {
    return EMPTY == dimension;
  }

  /**
   *
   * @return
   */
  public static Dimension Empty() {
    return EMPTY;
  }

  /**
   * 
   * @return 
   */
  public int getHeight() {
    return height;
  }
  
  /**
   * 
   * @return 
   */
  public int getWidth() {
    return width;
  }

  /**
   *
   * @return
   */
  public Envelope getExtent() {
    return env;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 67 * hash + this.width;
    hash = 67 * hash + this.height;
    hash = 67 * hash + Objects.hashCode(this.env);
    return hash;
  }

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
    final Dimension other = (Dimension) obj;
    if (this.width != other.width) {
      return false;
    }
    if (this.height != other.height) {
      return false;
    }
    if (!Objects.equals(this.env, other.env)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Dimension{" + "width=" + width + ", height=" + height + ", env=" + env + '}';
  }

}
