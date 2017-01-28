package com.vogonjeltz.physics.core

import javafx.beans.binding.ListBinding

import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.render.{GraphicsManager, Render}
import com.vogonjeltz.physics.utils.{DisplaySettings, Log}
import org.lwjgl.input.Keyboard

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.StdIn

/**
  * Universe
  *
  * Created by fredd
  */
class Universe {

  Log.setLevel(4)

  private val _particles: ArrayBuffer[Particle] = ArrayBuffer()
  def particles: List[Particle] = _particles.toList
  def addParticle(p: Particle): Unit = _particles.append(p)
  def addParticles(ps: List[Particle]): Unit = _particles.appendAll(ps)


  private var moveSpeed: Double = 10
  private var maxUPS = 100
  private var updateSync: Sync[Unit, Unit] = getNewUpdateSync
  private var tracker: Option[Particle] = None
  private var totalZoom:Double = 1

  val uxSync = new Sync[Unit, Unit](60, doUX _ )

  private var _nextTick: Tick = new Tick()
  def nextTick: Tick = _nextTick

  val graphicsManager: GraphicsManager = new GraphicsManager(new DisplaySettings {})

  private def getNewUpdateSync =
    new Sync[Unit, Unit](maxUPS, doTick _)


  def doTick(i: Option[Unit]): Unit = {

    val pairs = particles.combinations(2).toList
    val forceGroups = pairs.flatMap(T => T.head.interact(T(1))).groupBy(_.target)
    for(forceGroup <- forceGroups) {
      val target = forceGroup._1
      forceGroup._2.foreach(F => target.accept(F))
    }

    for (particle <- particles) {
      particle.runTick()
    }
  }

  def mainloop(): Unit = {
    graphicsManager.init()
    show()

    var running = true

    while (running) {
      running = !graphicsManager.render (() => {
        for (particle <- particles) {
          particle.render()
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
          Render.translateOffset(Vect(-moveSpeed,0))
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
          Render.translateOffset(Vect(moveSpeed,0))
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
          Render.translateOffset(Vect(0,-moveSpeed))
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
          Render.translateOffset(Vect(0,moveSpeed))
        }

      })
      updateSync.call()
      uxSync.call()

      //println("Tick Over")
    }

  }

  def doUX (): Unit = {
    graphicsManager.setTitle(s"FPS : ${graphicsManager.fps.toString} | UPS : ${updateSync.callsLastSecond} | Max UPS : $maxUPS")
    while(Keyboard.next()) {
      if (Keyboard.getEventKey == Keyboard.KEY_EQUALS && Keyboard.getEventKeyState) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
          Render.setZoom(0.1)
          moveSpeed *= 0.9
          totalZoom -= 0.1
        } else {
          maxUPS += 100
          updateSync = getNewUpdateSync
        }
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_MINUS && Keyboard.getEventKeyState) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
          if(Render.zoom.x > 0.1) {
            Render.setZoom(-0.1)
            moveSpeed *= 1.1
            totalZoom += 0.1
          }
        } else if (maxUPS > 99) {
          maxUPS -= 100
          updateSync = getNewUpdateSync
        }
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_M && Keyboard.getEventKeyState) {
        val x = -StdIn.readLine("New X > ").toDouble
        val y = -StdIn.readLine("New Y > ").toDouble
        Render.setOffset(Vect(x,y))
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_S && Keyboard.getEventKeyState) {
        show()
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_P && Keyboard.getEventKeyState) {
        val p = StdIn.readLine("Go to particle > ").toInt
        Render.setOffset((particles(p).position - Vect(500, 500)) * -1)
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_T && Keyboard.getEventKeyState) {
        if (tracker.isDefined) {
          tracker = None
          println("Cleared tracker")
        }
        else {
          val p = StdIn.readLine("Track particle > ").toInt
          tracker = Some(particles(p))
          Render.setOffset((particles(p).position - Vect(500* totalZoom, 500* totalZoom)) * -1)
          println("Tracking particle " + p)
        }
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_F && Keyboard.getEventKeyState) {
        Universe.toggleForces()
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_0 && Keyboard.getEventKeyState) {
        maxUPS = 0
        updateSync = getNewUpdateSync
      }
      else if (Keyboard.getEventKey == Keyboard.KEY_1 && Keyboard.getEventKeyState) {
        maxUPS = 1
        updateSync = getNewUpdateSync
      }
    }
    if (tracker.isDefined) {
      Render.setOffset((tracker.get.position - Vect(500 * totalZoom, 500* totalZoom)) * -1)
    }
  }

  def show(): Unit = {
    println("Particles")
    for (p <- particles.zipWithIndex) {
      println(s"${p._2} -> Position ${p._1.position} | Velocity ${p._1.velocity}")
    }
    println(Render.offset)
  }

}


object Universe {

  private var _enableForces: Boolean = true
  def enableForces:Boolean = _enableForces
  def toggleForces():Unit = _enableForces = !_enableForces

}

class Tick(initialForces: List[Force] = List()) {

  private val _forces: ListBuffer[Force] = initialForces.to[ListBuffer]
  def forces:List[Force] = _forces.toList

  def addForce(f: Force): Unit = _forces.append(f)

}