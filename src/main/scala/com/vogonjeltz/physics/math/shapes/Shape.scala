package com.vogonjeltz.physics.math.shapes

import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.render.Frame

/**
  * An immutable shape in the Gamee system
 *
  * @param position Position of the shape as a vector
  * @tparam T The type of the shape (for use as a subtype)
  */
abstract class Shape[T <: Shape[T]](val position: Vect) {

  //TODO: Better method of dealing with subclass type

  /**
    * Translate the shape by a vector
    * @param v Vector to translate by
    * @return The new Shape
    */
  def translate(v: Vect): T

  /**
    * Is a point within the shape
    * @param v The vector to check
    * @return True if the vector is within the bounds of the shape else false
    */
  def contains(v: Vect): Boolean

  def frame = Frame(position)

  def render(): Unit

}
