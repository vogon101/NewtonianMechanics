package com.vogonjeltz.physics.math.shapes

import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.render.{Colour, Frame, Render}
import org.lwjgl.opengl.GL11._

/**
  *
  * @param _centre The centre of the circle
  * @param radius The radius of the circle
  */
class Circle (private val _centre: Vect, val radius: Double, colour: Colour = Colour.WHITE, val filled: Boolean = false) extends Shape[Circle](_centre){

  override def translate(v: Vect) = new Circle(position + v, radius)

  override def frame: Frame = Frame(position, colour)

  override def contains(v: Vect) =
    math.abs(v.distance(position)) <= radius

  def intersect(that: Circle):Boolean =
    Math.abs(that.position.distance(this.position)) <= radius + that.radius


  override def render() = {
    if (filled)
      Render.withContext(frame) {
        var x2, y2:Double = 0
        glBegin(GL_TRIANGLE_FAN)
        for (angle <- 0d to (2d * Math.PI) by 0.01) {
          x2 = Math.sin(angle)*radius
          y2 = Math.cos(angle)*radius
          glVertex2d(x2,y2)
        }
        glEnd()
      }
    else
      Render.withContext(frame) {
        glBegin(GL_LINE_LOOP)
        for(i <- 0 to 360){
          val deginrad = i * Math.PI / 180
          glVertex2d(Math.cos(deginrad) * radius, Math.sin(deginrad) * radius)
        }
        glEnd()
      }
  }

}
