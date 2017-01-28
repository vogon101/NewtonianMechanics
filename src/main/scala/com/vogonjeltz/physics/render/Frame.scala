package com.vogonjeltz.physics.render

import com.vogonjeltz.physics.math.{Rotation, Vect}

/**
  * Created by Freddie on 11/12/2016.
  */
final case class Frame(private val _position: Vect = null, private val _colour: Colour = null, private val _rotation: Rotation = null) {

  val position = Option(_position)

  val rotation = Option(_rotation)

  val colour = Option(_colour)

  def wrap (actions: => Unit) = Render.withContext(this)(actions)

  def apply(c: Colour)  = Frame(_position, c, _rotation)
  def apply(p: Vect)    = Frame(p, _colour, _rotation)
  def apply(r: Rotation)= Frame(_position, _colour, r)

}
