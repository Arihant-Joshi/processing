/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2005-08 Ben Fry and Casey Reas

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

package processing.core;


/**
 * 4x4 matrix implementation.
 */
public final class PMatrix2D implements PConstants {

  public float m00, m01, m02;
  public float m10, m11, m12;


  public PMatrix2D() {
    reset();
  }


  public PMatrix2D(float m00, float m01, float m02, 
                   float m10, float m11, float m12) {
    set(m00, m01, m02,
        m10, m11, m12);
  }
  

  public void reset() {
    set(1, 0, 0, 
        0, 1, 0);
  }


  /**
   * Returns a copy of this PMatrix.
   */
  public PMatrix2D get() {
    PMatrix2D outgoing = new PMatrix2D();
    outgoing.set(this);
    return outgoing;
  }
  
  
  /**
   * Copies the matrix contents into a 6 entry float array.
   * If target is null (or not the correct size), a new array will be created.
   */
  public float[] get(float[] target) {
    if ((target == null) || (target.length != 6)) {
      target = new float[6];
    }
    target[0] = m00;
    target[1] = m01;
    target[2] = m02;
    
    target[3] = m10;
    target[4] = m11;
    target[5] = m12;

    return target;
  }
  

  public void set(PMatrix2D src) {
    set(src.m00, src.m01, src.m02,
        src.m10, src.m11, src.m12);
  }


  public void set(float[] source) {
    m00 = source[0];
    m01 = source[1];
    m02 = source[2];

    m10 = source[3];
    m11 = source[4];
    m12 = source[5];
  }
  
  
  public void set(float m00, float m01, float m02, 
                  float m10, float m11, float m12) {
    this.m00 = m00; this.m01 = m01; this.m02 = m02; 
    this.m10 = m10; this.m11 = m11; this.m12 = m12;
  }


  public void translate(float tx, float ty) {    
    m02 = tx*m00 + ty*m01 + m02;
    m12 = tx*m10 + ty*m11 + m12;
  }


  public void rotate(float angle) {
    // TODO fixme
  }


  public void rotateX(float angle) {
  }


  public void rotateY(float angle) {
  }


  public void rotateZ(float angle) {
  }


  public void rotate(float angle, float v0, float v1, float v2) {
  }


  public void scale(float s) {
    scale(s, s);
  }


  public void scale(float sx, float sy) {
    m00 *= sx;  m01 *= sy;  
    m10 *= sx;  m11 *= sy;  
  }


  public void scale(float x, float y, float z) {
  }


  /** 
   * Multiply this matrix by another.
   */
  public void apply(PMatrix2D source) {
    apply(source.m00, source.m01, source.m02, 
          source.m10, source.m11, source.m12);
  }


  public void apply(float n00, float n01, float n02, 
                    float n10, float n11, float n12) {
    float t0 = m00;
    float t1 = m01;
    m00  = n00 * t0 + n10 * t1;
    m01  = n01 * t0 + n11 * t1;
    m02 += n02 * t0 + n12 * t1;

    t0 = m10;
    t1 = m11;
    m10  = n00 * t0 + n10 * t1;
    m11  = n01 * t0 + n11 * t1;
    m12 += n02 * t0 + n12 * t1;    
  }


  /**
   * Apply another matrix to the left of this one.
   */
  public void preApply(PMatrix2D left) {
    preApply(left.m00, left.m01, left.m02,
             left.m10, left.m11, left.m12);
  }


  public void preApply(float n00, float n01, float n02,
                       float n10, float n11, float n12) {
    float t0 = m02;
    float t1 = m12;
    n02 += t0 * n00 + t1 * n01;
    n12 += t0 * n10 + t1 * n11;

    m02 = n02;
    m12 = n12;

    t0 = m00;
    t1 = m10;
    m00 = t0 * n00 + t1 * n01;
    m10 = t0 * n10 + t1 * n11;

    t0 = m01;
    t1 = m11;
    m01 = t0 * n00 + t1 * n01;
    m11 = t0 * n10 + t1 * n11;  }


  //////////////////////////////////////////////////////////////


  /** 
   * Multiply a four element vector against this matrix. 
   * If out is null or not length four, a new float array will be returned.
   * The values for vec and out can be the same (though that's less efficient). 
   */
  public float[] multiply(float vec[], float out[]) {
    if (out == null || out.length != 2) {
      out = new float[2];
    }
    
    if (vec == out) {
      float tx = m00*vec[0] + m01*vec[1] + m02;
      float ty = m10*vec[0] + m11*vec[1] + m12;

      out[0] = tx;
      out[1] = ty;

    } else {
      out[0] = m00*vec[0] + m01*vec[1] + m02;
      out[1] = m10*vec[0] + m11*vec[1] + m12;
    }
    
    return out;
  }


  /**
   * Transpose this matrix.
   */
  public void transpose() {
  }


  /**
   * Invert this matrix. Implementation stolen from OpenJDK.
   * @return true if successful
   */
  public boolean invert() {
    float determinant = determinant();
    if (Math.abs(determinant) <= Float.MIN_VALUE) {
      return false;
    }
    
    float t00 = m00; 
    float t01 = m01; 
    float t02 = m02;
    float t10 = m10; 
    float t11 = m11; 
    float t12 = m12;

    m00 =  t11 / determinant;
    m10 = -t10 / determinant;
    m01 = -t01 / determinant;
    m11 =  t00 / determinant;
    m02 = (t01 * t12 - t11 * t02) / determinant;
    m12 = (t10 * t02 - t00 * t12) / determinant;    
    
    return true;
  }
  
  
  /**
   * @return the determinant of the matrix
   */
  public float determinant() {
    return m00 * m11 - m01 * m10;
  }

  
  //////////////////////////////////////////////////////////////


  public void print() {
    int big = (int) abs(max(PApplet.max(abs(m00), abs(m01), abs(m02)),
                            PApplet.max(abs(m10), abs(m11), abs(m12))));

    int digits = 1;
    if (Float.isNaN(big) || Float.isInfinite(big)) {  // avoid infinite loop
      digits = 5;
    } else {
      while ((big /= 10) != 0) digits++;  // cheap log()
    }

    System.out.println(PApplet.nfs(m00, digits, 4) + " " +
                       PApplet.nfs(m01, digits, 4) + " " +
                       PApplet.nfs(m02, digits, 4));

    System.out.println(PApplet.nfs(m10, digits, 4) + " " +
                       PApplet.nfs(m11, digits, 4) + " " +
                       PApplet.nfs(m12, digits, 4));

    System.out.println();
  }


  //////////////////////////////////////////////////////////////


  private final float max(float a, float b) {
    return (a > b) ? a : b;
  }

  private final float abs(float a) {
    return (a < 0) ? -a : a;
  }

  private final float sin(float angle) {
    return (float)Math.sin(angle);
  }

  private final float cos(float angle) {
    return (float)Math.cos(angle);
  }
}
