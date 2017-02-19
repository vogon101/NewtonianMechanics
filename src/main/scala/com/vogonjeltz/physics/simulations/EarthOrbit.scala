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
    implicit val universe = new Universe(enableGraphics = true, runUntil = 365 * 24, maxUPS = -1, resolution = 3600)
    val earthLike = ParticleType(5.9724 * Math.pow(10, 24), 1000, Colour.GREEN, fixed = false)
    val sunLike = ParticleType(1.989 * Math.pow(10, 30), 10000, Colour.YELLOW, fixed = true)
    val moonLike = ParticleType(0.07346 * Math.pow(10,24), 700, Colour.WHITE)

    val earth: Particle = earthLike(Vect(149 * Math.pow(10, 9), 0), Vect(0, 30000))
    val sun: Particle = sunLike(Vect.ZERO)
    val moon: Particle = moonLike(Vect(385000000, 0) + earth.position, Vect(0, 1022) + earth.velocity)

    //val earth: Particle = earthLike(Vect(0 ,0), Vect(0, 0))
    //val moon: Particle = moonLike(Vect(385000000, 0), Vect(0, 1022))

    universe.addParticles(List(
      earth, moon, sun
    ))
    println(earth.position)

    val earthTracker = new PathTracker(List(earth, moon), addEvery = 23)
    //val moonTracker = new PathTracker(moon, addEvery = 23)

    universe.registerPathTracker(earthTracker)
    universe.mainloop()
    println (earthTracker.paths.length)
    earthTracker.writeToCsv("EarthOrbit.csv")
    Collection.next = "--restart--"
  }

}
