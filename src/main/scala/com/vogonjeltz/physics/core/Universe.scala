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


  var moveSpeed: Double = 10
  var maxUPS = 100
  val updateSync: Sync[Unit, Unit] = new Sync[Unit, Unit](maxUPS, doTick _)
  private var _tracker: Option[Particle] = None
  var totalZoom:Double = 1

  val uxSync = new Sync[Unit, Unit](60, doUX _ )

  private var _nextTick: Tick = new Tick()
  def nextTick: Tick = _nextTick

  val graphicsManager: GraphicsManager = new GraphicsManager(this, new DisplaySettings {})

  def command = uXManager.command

  def tracker:String = if (_tracker.isDefined) particles.indexOf(_tracker.get).toString else ""

  val commands: List[Command] = List(
    Command("track", 1, (params: List[String]) => {
      try{
        val num = params.head.toInt
        _tracker = Some(particles(num))
        CommandSuccess(s"Tracking particle $num")
      } catch {
        case e: NumberFormatException => CommandFailure("Invalid number " + params.head)
        case e: IndexOutOfBoundsException => CommandFailure("No particle of index " + params.head)
      }
    }),
    Command("cleartrack", 0, (params: List[String]) => {
      _tracker = None
      CommandSuccess("Cleared tracker")
    }),
    Command("show", 0, (params: List[String]) => {
      show()
      CommandSuccess("Listed particles on console")
    }),
    Command("forces", 0,(params: List[String]) => {
      Universe.toggleForces()
      CommandSuccess("Toggled forces")
    }),
    Command("col", 1,(params: List[String]) => {
      if (params.head == "on") {
        Universe.setAllowForceOverride(true)
        CommandSuccess("Turned collision force draw override on")
      }
      else if (params.head == "off") {
        Universe.setAllowForceOverride(false)
        CommandSuccess("Turned collision force draw override off")
      }
      else
        CommandFailure("Invalid option ${params.head} (use on/off)")

    }),
    Command("particle", 1,(params: List[String]) => {
      try{
        val num = params.head.toInt
        Render.setOffset((particles(num).position - Vect(500 * totalZoom, 500* totalZoom)) * -1)
        CommandSuccess("Moved view to particle")
      } catch {
        case e: NumberFormatException => CommandFailure("Invalid number " + params.head)
        case e: IndexOutOfBoundsException => CommandFailure("No particle of index " + params.head)
      }
    }),
    Command("pause", 0,(params: List[String]) => {
      if (maxUPS == 0)
         maxUPS = 100
      else
        maxUPS = 0
      updateSync.setRate(maxUPS)
      CommandSuccess(s"Set max UPS to $maxUPS")
    }),
    Command("slow", 0,(params: List[String]) => {
      maxUPS = 10
      updateSync.setRate(maxUPS)
      CommandSuccess(s"Set max UPS to $maxUPS")
    }),
    Command("1", 0,(params: List[String]) => {
      maxUPS = 1
      updateSync.setRate(maxUPS)
      CommandSuccess(s"Set max UPS to $maxUPS")
    }),
    Command("speed", 1, (params: List[String]) => {
      try{
        val num = params.head.toInt
        maxUPS = num
        updateSync.setRate(maxUPS)
        CommandSuccess(s"Set max UPS to $maxUPS")
      } catch {
        case e: NumberFormatException => CommandFailure("Invalid number " + params.head)
      }
    })
  )

  val uXManager = new UXManager(commands, this)

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
          Render.translateOffset(Vect(0,moveSpeed))
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
          Render.translateOffset(Vect(0,-moveSpeed))
        }

      })
      updateSync.call()
      uxSync.call()
    }

  }


  def doUX (): Unit = {
    graphicsManager.setTitle(s"FPS : ${graphicsManager.fps.toString} | UPS : ${updateSync.callsLastSecond} | Max UPS : $maxUPS | Timing Modifier : ${updateSync.timingModifier}")
    uXManager.update()
    if (_tracker.isDefined) {
      Render.setOffset((_tracker.get.position - Vect(500 * totalZoom, 500* totalZoom)) * -1)
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

  private var _drawForces: Boolean = true
  private var _allowForceOverride: Boolean = true
  def drawForces:Boolean = _drawForces
  def toggleForces():Unit = _drawForces = !_drawForces
  def setAllowForceOverride(b: Boolean) = _allowForceOverride = b
  def allowForceOverride = _allowForceOverride

}

class Tick(initialForces: List[Force] = List()) {

  private val _forces: ListBuffer[Force] = initialForces.to[ListBuffer]
  def forces:List[Force] = _forces.toList

  def addForce(f: Force): Unit = _forces.append(f)

}