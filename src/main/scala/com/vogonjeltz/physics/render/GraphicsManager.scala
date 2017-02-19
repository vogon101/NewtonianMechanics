package com.vogonjeltz.physics.render

import java.awt.Font

import com.vogonjeltz.physics.core.{Sync, Universe}
import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.utils.{DisplaySettings, Log}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}

/**
  * Created by Freddie on 11/12/2016.
  */
class GraphicsManager(val universe: Universe, val displaySettings: DisplaySettings) {

  val frameSync = new Sync(displaySettings.fpsCap, (f : Option[() => Unit]) => doRender(f.get))

  def init(): Unit = {
    // Initialize OpenGL

    Display.setDisplayMode( new DisplayMode( displaySettings.width, displaySettings.height ) )
    Display.create()
    Display.setTitle( displaySettings.title )
    /*
    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
    glViewport( 0, 0, displaySettings.width, displaySettings.height )

    glMatrixMode( GL_PROJECTION )
    glLoadIdentity()
    glOrtho( 0, Display.getWidth.toDouble, 0, Display.getHeight.toDouble, 1, -1 )

    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()*/
    glPushAttrib(GL_ALL_ATTRIB_BITS)
    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
    glViewport( 0, 0, displaySettings.width.toInt, displaySettings.height.toInt )

    glMatrixMode( GL_PROJECTION )
    glLoadIdentity()
    glOrtho( 0, Display.getWidth.toDouble, Display.getHeight.toDouble, 0, 1, -1 )

    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
  }

  def setTitle(t: String) = Display.setTitle(t)

  def clearScreen(): Unit ={
    glClearColor( 0f, 0f, 0f, 1.0f )
    glClear( GL_COLOR_BUFFER_BIT )
  }

  def render(f:() => Unit) = {
    frameSync.call(Some(f)).getOrElse(false)
  }

  def doRender(f: () => Unit): Boolean = {

    clearScreen()

    Log.verbose("Starting Render")

    //glScaled(Render.zoom.x, Render.zoom.y, 1)
    //Render.clearZoom()

    f()

    renderHUD()

    Display.update()

    Log.verbose("Screen rendered")

    if ( Display.isCloseRequested ) {
      Log.warn("Close requested")
      true
    } else false

  }

  def renderHUD(): Unit = {

    val forceRenderString = "Render forces " + Universe.drawForces + (if (Universe.allowForceOverride) " (Collisions override: On)" else "")

    Render.textContext(){
      val strings = List(
        "Newtonian Mechanics Simulation",
        s"Number of bodies: ${universe.particles.length}",
        s"Current input: ${universe.uXManager.inputBuilder.toString}",
        s"Last command result: ${universe.uXManager.lastResult}",
        forceRenderString,
        s"Render offset ${Render.offset}",
        s"Time resolution: ${universe.resolution} | Time multiplier: ${universe.resolution * universe.maxUPS}",
        if (universe.command != "") s"Current command: ${universe.command}" else "",
        if (universe.tracker != "") s"Current tracker: ${universe.tracker}" else "",
        if (universe.pauseOnNextCollision) s"Will pause on next collision" else ""
      ).filter(_ != "")
      Render.drawText(strings, new Font("Arial", Font.PLAIN, 14), frame = Frame(_position = Vect(10,10)))
    }

  }

  def cleanUp(): Unit = {
    glPopAttrib()
    Render.clearFontCache()
    Render.setZoom(1)
    Render.setOffset(Vect.ZERO)
    Display.destroy()
  }


  def fps = frameSync.callsLastSecond

}
