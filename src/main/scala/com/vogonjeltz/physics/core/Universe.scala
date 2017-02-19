package com.vogonjeltz.physics.core

import java.text.CollationElementIterator
import javafx.beans.binding.ListBinding

import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.output.PathTracker
import com.vogonjeltz.physics.render.{GraphicsManager, Render}
import com.vogonjeltz.physics.simulations.Collection
import com.vogonjeltz.physics.utils.{DisplaySettings, Log}
import org.lwjgl.input.Keyboard

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.StdIn

/**
  * Universe
  *
  * Created by fredd
  */
class Universe(val enableGraphics: Boolean = true, val runUntil: Int = 0, var maxUPS:Int = 100, val resolution: Double = 1) {

  Log.setLevel(1)

  private val _particles: ArrayBuffer[Particle] = ArrayBuffer()
  def particles: List[Particle] = _particles.toList
  def addParticle(p: Particle): Unit = _particles.append(p)
  def addParticles(ps: List[Particle]): Unit = _particles.appendAll(ps)

  private var _pauseOnNextCollision = false
  def pauseOnNextCollision: Boolean = _pauseOnNextCollision


  var moveSpeed: Double = 10
  //var maxUPS = 100
  val updateSync: Sync[Unit, Unit] = new Sync[Unit, Unit](maxUPS, doTick _)
  private var _tracker: Option[Particle] = None
  var totalZoom:Double = 1

  val uxSync = new Sync[Unit, Unit](60, doUX _ )

  private var _tickNum: Int = 0
  def tickNum: Int = _tickNum

  private var _nextTick: Tick = new Tick()
  def nextTick: Tick = _nextTick

  val graphicsManager: GraphicsManager = new GraphicsManager(this, new DisplaySettings {
    override val enabled: Boolean = enableGraphics
  })

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
        CommandFailure(s"Invalid option ${params.head} (use on/off)")

    }),
    Command("particle", 1,(params: List[String]) => {
      try{
        val num = params.head.toInt
        Render.setOffset((particles(num).position * totalZoom - Vect(graphicsManager.displaySettings.width / 2, graphicsManager.displaySettings.height / 2)) * -1)
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
    }),
    Command("noCap", 0, (params: List[String]) => {
      maxUPS = -1
      updateSync.setRate(maxUPS)
      CommandSuccess(s"Set max UPS to -1")
    }),
    Command("pauseCollision", 0, (params: List[String]) => {
      _pauseOnNextCollision = !pauseOnNextCollision
      CommandSuccess(s"Set pauseOnNextCollision to $pauseOnNextCollision")
    }),
    Command("zoom", 1, (params: List[String]) => {
      try {
        val zoom = params.head.toDouble
        Render.setZoom(zoom)
        totalZoom = zoom
        CommandSuccess("Changed zoom to " + zoom)
      } catch {
        case e: NumberFormatException => CommandFailure("Invalid number " + params.head)
      }
    }),
    Command("scale", 1, (params: List[String]) => {
      val scale = params.head
      scale match {
        case "mm" => {Render.setZoom(1000); totalZoom=1000; CommandSuccess("Set scale to milimetres")}
        case "cm" => {Render.setZoom(100); totalZoom=100; CommandSuccess("Set scale to centimetres")}
        case "m" => {Render.setZoom(1); totalZoom=1; CommandSuccess("Set scale to metres")}
        case "km" => {Render.setZoom(0.001); totalZoom=0.001; CommandSuccess("Set scale to kilometres")}
        case _ => CommandFailure("Unknown scale mode: " + scale)
      }
    }),
    Command("exit", 0, (params: List[String]) => {
      System.exit(0)
      CommandSuccess("Exiting...")
    }),
    Command("run", 1, (params: List[String]) => {
      running = false
      Collection.next = params.head
      CommandSuccess("Changing simulation")
    }),
    Command("simulations", 0, (params: List[String]) => {
      println("Available simulations")
      Collection.simulations.keys.foreach(S => println(s"* $S"))
      CommandSuccess("Printed available simulations to console")
    }),
    Command("restart", 0, (params: List[String]) => {
      running = false
      if(Collection.next == "") Collection.next = "--restart--"
      CommandSuccess("Restarting simulation")
    })
  )

  val uXManager = new UXManager(commands, this)

  private val _pathTrackers: ListBuffer[PathTracker] = ListBuffer()
  def pathTrackers:List[PathTracker] = _pathTrackers.toList

  def registerPathTracker(tracker: PathTracker) = _pathTrackers.append(tracker)

  def doTick(i: Option[Unit]): Unit = {
    _tickNum += 1
    if (runUntil != 0 && tickNum % 1000 == 0) {
      println(s"Ticks Remaining ${runUntil - tickNum}")
    }

    for (tracker <- pathTrackers) {
      tracker.update()
    }

    val pairs = particles.combinations(2).toList
    val interactions = pairs.map(T => T.head.interact(T(1)))
    val forceGroups:Map[Particle, List[Force]] = interactions.flatMap(_._1).groupBy(_.target)
    for(forceGroup <- forceGroups) {
      val target = forceGroup._1
      forceGroup._2.foreach((F: Force) => target.accept(F))
    }

    val didCollide = interactions.exists(_._2)

    if (didCollide && pauseOnNextCollision) {
      maxUPS = 0
      updateSync.setRate(maxUPS)
    }

    for (particle <- particles) {
      particle.runTick()
    }

  }

  private var running = true

  def mainloop(): Unit = {
    if (enableGraphics)
      graphicsManager.init()
    show()
    Keyboard.create()

    while (running && (runUntil == 0 || tickNum < runUntil)) {

      val closeRequested = enableGraphics && graphicsManager.render (() => {
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

      if (closeRequested){
        running = false
        Collection.next = ""
      }

      updateSync.call()
      if (enableGraphics)
        uxSync.call()
    }

    graphicsManager.cleanUp()

  }


  def doUX (): Unit = {
    val timeMultiplier = maxUPS * resolution
    graphicsManager.setTitle(s"FPS : ${graphicsManager.fps.toString} | UPS : ${updateSync.callsLastSecond} | Max UPS : $maxUPS | Timing Modifier : ${updateSync.timingModifier} | Time Multiplier : $timeMultiplier | Zoom : $totalZoom")
    uXManager.update()
    if (_tracker.isDefined) {
      Render.setOffset((_tracker.get.position * totalZoom - Vect(graphicsManager.displaySettings.width / 2, graphicsManager.displaySettings.height / 2)) * -1)
      //Render.setOffset((_tracker.get.position - Vect(500 / totalZoom, 500 / totalZoom)) * -1)
    }
  }

  def show(): Unit = {
    var totalMomentum: Vect = Vect.ZERO
    var totalKE: Double = 0

    println("Particles")
    for (p <- particles.zipWithIndex) {
      println(s"${p._2} -> Position ${p._1.position} | Velocity ${p._1.velocity} (${p._1.velocity.length})")
      totalMomentum += p._1.momentum
      totalKE += 0.5 * p._1.velocity.squared * p._1.mass
    }

    println(s"Total Momentum: ${totalMomentum.length}")
    println(s"Total KE: $totalKE")

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