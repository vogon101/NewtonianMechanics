package com.vogonjeltz.physics.math.shapes

import com.vogonjeltz.physics.math.Vect

/**
  * A rectangle (Shape)
 *
  * @param _centre The centre of the rectangle (the position)
  * @param width Width of the rectangle (x direction)
  * @param height Height of the rectangle (y direction)
  */
case class Box(private val _centre: Vect, width: Double, height: Double) extends Shape[Box](_centre) {

  /**
    * @param x x-ordinate of the centre
    * @param y y-ordinate of the centre
    * @param width Width of the rectangle (x-direction)
    * @param height Height of the rectangle (y-direction)
    */
  def this(x: Double, y: Double, width: Double, height: Double) = {
    this(Vect(x, y), width, height)
  }

  /**
    * Defines a box by using two corners
    * @param bottomLeft Bottom left corner of the rectangle
    * @param topRight Top right corner of the rectangle
    */
  def this(bottomLeft: Vect, topRight: Vect) = {
    this((bottomLeft + topRight) / 2, (topRight-bottomLeft).x, (topRight - bottomLeft).y)
  }

  /**
    * @return The bottom left hand corner of the rectangle
    */
  def bottomLeft = Vect(position.x - width/2, position.y - height/2)

  /**
    * @return The top right hand corner of the rectangle
    */
  def topRight = Vect(position.x + width/2, position.y + height/2)

  /**
    * @return The rectangle as a rendering reference frame (Frame(position))
    */
  //def frame = Frame(position)

  override def translate(v: Vect) = Box(position + v, width, height)

  override def contains(v: Vect) = ??? //bottomLeft.x > v.x && topRight.x < v.x && bottomLeft.y

  override def render() = ???

}
