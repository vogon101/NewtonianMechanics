package com.vogonjeltz.physics.render

import com.vogonjeltz.physics.math.Vect
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._

/**
  * Created by Freddie on 11/12/2016.
  */
object Render {

  private var _offset: Vect = Vect.ZERO
  private var _zoom: Vect = Vect(1,1)

  //TODO: Should the frame know about colour
  def withContext( frame: Frame )( actions: => Unit ): Unit = {

    glPushMatrix()

    val colourBuffer = BufferUtils.createFloatBuffer( 16 )
    glGetFloat( GL_CURRENT_COLOR, colourBuffer )

    for (p <- frame.position) glTranslated(p.x, p.y, 0)
    for (r <- frame.rotation) glRotated(r.toDeg, 0, 0, 1)
    for (c <- frame.colour)   glColor3d(c.r, c.g, c.b)

    glTranslated(_offset.x, _offset.y, 0)

    actions

    glPopMatrix()
    glColor3d( colourBuffer.get( 0 ).toDouble, colourBuffer.get( 1 ).toDouble, colourBuffer.get( 2 ).toDouble )

  }

  def translateOffset(v: Vect):Unit = _offset += v
  def setOffset(v:Vect) = _offset = v
  def offset = _offset

  def setZoom(s: Double): Unit = _zoom += Vect(s,s)
  def zoom = _zoom
  def clearZoom(): Unit = _zoom = Vect(1,1)



}
