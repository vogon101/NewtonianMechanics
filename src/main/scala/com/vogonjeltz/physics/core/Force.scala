package com.vogonjeltz.physics.core

import com.vogonjeltz.physics.math.{Rotation, Vect}
import com.vogonjeltz.physics.render.{Frame, Render}
import org.lwjgl.opengl.GL11._

/**
  * Force
  *
  * Created by fredd
  */
case class Force (target: Particle, magnitude: Double, direction: Rotation){

  def toVect: Vect = Vect.fromAMF(direction, magnitude)

  def render():Unit = {
    if (!Universe.enableForces) return
    Render.withContext(Frame(target.position)) {
      glBegin(GL_LINE_STRIP)
      val (r,g,b) = target.particleType.colour.tuple
      glColor3d(r,g,b)
      glLineWidth(2f)
      glVertex2d(0,0)
      glVertex2d(toVect.x/Math.log10(target.mass), toVect.y/Math.log10(target.mass))
      glEnd()
    }

  }

}
