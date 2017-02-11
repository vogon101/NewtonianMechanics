package com.vogonjeltz.physics.simulations

import com.vogonjeltz.physics.core.{Particle, ParticleType, Universe}
import com.vogonjeltz.physics.math.Vect
import com.vogonjeltz.physics.output.PathTracker
import com.vogonjeltz.physics.render.Colour

/**
  * EarthOrbit
  *
  * Created by fredd
  */
case class EarthOrbit() extends Simulation{

  def run (): Unit = {
    implicit val universe = new Universe(enableGraphics = false, runUntil = 365 * 24 * 10, maxUPS = -1, resolution = 360)
    val earthLike = ParticleType(5.972 * Math.pow(10, 24), 1000, Colour.GREEN, fixed = false)
    val sunLike = ParticleType(1.989 * Math.pow(10, 30), 10000, Colour.YELLOW, fixed = true)

    val earth: Particle = earthLike(Vect(149 * Math.pow(10, 9), 0), Vect(0, 30000))
    val sun: Particle = sunLike(Vect.ZERO)

    universe.addParticles(List(
      earth, sun
    ))

    val earthTracker = new PathTracker(earth)

    universe.registerPathTracker(earthTracker)
    universe.mainloop()
    earthTracker.writeToCsv("EarthOrbit.csv")
  }

}
