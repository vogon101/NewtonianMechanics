package com.vogonjeltz.physics.simulations

import com.vogonjeltz.physics.core.{Particle, ParticleType, Universe}
import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.output.PathTracker
import com.vogonjeltz.physics.render.Colour
import com.vogonjeltz.physics.utils.Log

import scala.io.StdIn

/**
  * Collection
  *
  * Created by fredd
  */
object Collection extends App {

  val simulations: Map[String, Simulation] = Map("earth" -> EarthOrbit(), "collision" -> Collision(), "orbit" -> OrbitSim(), "pool" -> Pool(), "test" -> Test(), "pool2" -> Pool2())

  val default: String = "pool2"

  var next: String = ""

  run(default)

  def run(input: String): Unit = {
    if (simulations.contains(input)) {
      Log.success(s"Running simulation $input")
      simulations(input).run()
    } else {
      Log.error(s"Cannot find simulation $input, defaulting to $default")
      simulations(default).run()
    }
    Log.info("Simulation Ended")
    //TODO: This would cause SO error if someone was crazy enough to keep running simulations, fix pls
    if (next != "") run(next)
  }

}

trait Simulation {

  def run(): Unit

}

case class Collision() extends Simulation {

  def run(): Unit = {


    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24 * 10, maxUPS = 100, resolution = 1)
    val pT1 = ParticleType(10, 20, Colour.GREEN)
    val pT2 = ParticleType(5, 10, Colour.RED)
    universe.addParticles(List(
      pT1(Vect(600, 300), Vect.ZERO)
      ,pT2(Vect(100, 300), Vect(1,0))
      //,pT2(Vect(600, 600), Vect(0, -0.6))
    ))

    universe.mainloop()

  }

}

case class OrbitSim() extends Simulation {

  override def run():Unit= {

    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24 * 10, maxUPS = 100, resolution = 1)
    val particleTypeOne = ParticleType(10000000000d, 10, Colour.GREEN, fixed=false)
    val particleTypeTwo = ParticleType(100000000000d, 20, Colour.YELLOW, fixed=true)
    universe.addParticles(List(
      particleTypeOne(Vect(300, 600), Vect(-0.1,0)),
      particleTypeTwo(Vect(300, 300), Vect(0,0)),
      particleTypeOne(Vect(300, 1000), Vect(-0.1, 0)),
      particleTypeOne(Vect(300, 1500), Vect(-0.07, 0))
    ))
    universe.mainloop()

  }

}

case class Pool() extends Simulation {

  override def run() = {

    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24 * 10, maxUPS = 100, resolution = 0.01)
    val red = ParticleType(1, 10, Colour.RED)
    val yellow = ParticleType(1, 10, Colour.YELLOW)
    val white = ParticleType(1, 10, Colour.WHITE)

    universe.addParticles(List(
      white(Vect(100, 350), Vect(20, -3)),
      yellow(Vect(300, 300)),
      red(Vect(320, 290)),
      red(Vect(320, 310)),
      red(Vect(340, 320)),
      white(Vect(340, 300)),
      yellow(Vect(340, 280)),
      yellow(Vect(360, 270)),
      red(Vect(360, 290)),
      yellow(Vect(360, 310)),
      yellow(Vect(360, 330)),
      yellow(Vect(380, 340)),
      red(Vect(380, 320)),
      red(Vect(380, 300)),
      yellow(Vect(380, 280)),
      red(Vect(380, 260))
    ))

    universe.mainloop()

  }

}

case class Pool2() extends Simulation {

  override def run() = {

    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24 * 10, maxUPS = 0, resolution = 0.01)
    val red = ParticleType(1, 0.1, Colour.RED)
    val yellow = ParticleType(1, 0.1, Colour.YELLOW)
    val white = ParticleType(1, 0.1, Colour.WHITE)


    universe.addParticles(List(
      white(Vect(0, -1), Vect(5, 1.3)),
      yellow(Vect(1, 0)),
      red(Vect(1.2, -0.1)),
      red(Vect(1.2, 0.1)),
      red(Vect(1.4, -0.2)),
      white(Vect(1.4, 0)),
      yellow(Vect(1.4, 0.2)),
      yellow(Vect(1.6, 0.3)),
      red(Vect(1.6, 0.1)),
      yellow(Vect(1.6, -0.1)),
      yellow(Vect(1.6, -0.3)),
      yellow(Vect(1.8, -0.4)),
      red(Vect(1.8, -0.2)),
      red(Vect(1.8, 0)),
      yellow(Vect(1.8, 0.2)),
      red(Vect(1.8, 0.4))
    ))

    universe.uXManager.runCommand("particle", List("0"))
    universe.uXManager.runCommand("scale", List("cm"))

    universe.mainloop()


  }

}

case class Test() extends Simulation {

  override def run() = {

    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24 * 10, maxUPS = 1, resolution = 1)
    val big = ParticleType(100000, 100, Colour.RED)
    val small = ParticleType(10, 10, Colour.YELLOW)
    universe.addParticles(List(
      big(Vect(500, 300)),
      small(Vect(0, 300), Vect(5, 0)),
      small(Vect(0, 321), Vect(5, 0)),
      small(Vect(0, 342), Vect(5, 0)),
      small(Vect(0, 363), Vect(5, 0)),
      small(Vect(0, 279), Vect(5, 0)),
      small(Vect(0, 258), Vect(5, 0)),
      small(Vect(0, 237), Vect(5, 0)),
      small(Vect(0, 216), Vect(5, 0)),
      small(Vect(0, 195), Vect(5, 0)),
      small(Vect(0, 384), Vect(5, 0)),
      small(Vect(0, 405), Vect(5, 0)),
      small(Vect(-30, 300), Vect(5, 0)),
      small(Vect(-30, 321), Vect(5, 0)),
      small(Vect(-30, 342), Vect(5, 0)),
      small(Vect(-30, 363), Vect(5, 0)),
      small(Vect(-30, 279), Vect(5, 0)),
      small(Vect(-30, 258), Vect(5, 0)),
      small(Vect(-30, 237), Vect(5, 0)),
      small(Vect(-30, 216), Vect(5, 0)),
      small(Vect(-30, 195), Vect(5, 0)),
      small(Vect(-30, 384), Vect(5, 0)),
      small(Vect(-30, 405), Vect(5, 0))
    ))

    universe.mainloop()

  }

}