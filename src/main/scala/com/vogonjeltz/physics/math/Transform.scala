package com.vogonjeltz.physics.math

import com.vogonjeltz.physics.math.shapes.Shape

/**
  * Created by Freddie on 14/12/2016.
  */
class Transform[T <: Shape[T]](protected var _shape: T) {

  protected var _position: Vect = shape.position

  def position = _position

  def shape = _shape

  def shape_m () = shape

  def translate(v: Vect) = _shape = shape.translate(v)

}