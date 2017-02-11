package com.vogonjeltz.physics.simulations

import com.vogonjeltz.physics.core.{Particle, ParticleType, Universe}
import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.output.PathTracker
import com.vogonjeltz.physics.render.Colour

import scala.io.StdIn

/**
  * Collection
  *
  * Created by fredd
  */
object Collection extends App {

  val simulations: Map[String, Simulation] = Map("earth" -> EarthOrbit(), "collision" -> Collision(), "orbit" -> OrbitSim(), "pool" -> Pool(), "test" -> Test())

  val default: String = "pool"

  val input = StdIn.readLine(">>>")
  if (simulations.contains(input)) {
    simulations(input).run()
  } else {
    simulations(default).run()
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

    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24 * 10, maxUPS = 1, resolution = 1)
    val red = ParticleType(1, 10, Colour.RED)
    val yellow = ParticleType(1, 10, Colour.YELLOW)
    val white = ParticleType(1, 10, Colour.WHITE)

    universe.addParticles(List(
      white(Vect(100, 300), Vect(6, 0)),
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

    implicit class DoubleE(d: Double) {

      def e: Double = d/100

    }

    universe.addParticles(List(
      white(Vect(100 e, 300 e), Vect(1, 0)),
      yellow(Vect(300 e, 300 e)),
      red(Vect(320 e, 290 e)),
      red(Vect(320 e, 310 e)),
      red(Vect(340 e, 320 e)),
      white(Vect(340 e, 300 e)),
      yellow(Vect(340 e, 280 e)),
      yellow(Vect(360 e, 270 e)),
      red(Vect(360 e, 290 e)),
      yellow(Vect(360 e, 310 e)),
      yellow(Vect(360 e, 330 e)),
      yellow(Vect(380 e, 340 e)),
      red(Vect(380 e, 320 e)),
      red(Vect(380 e, 300 e)),
      yellow(Vect(380 e, 280 e)),
      red(Vect(380 e, 260 e))
    ))

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
      small(Vect(0, 300), Vect(2, 0))
    ))

    universe.mainloop()

  }

}