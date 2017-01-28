package com.vogonjeltz.physics

import com.vogonjeltz.physics.core.{Particle, ParticleType, Universe}
import com.vogonjeltz.physics.math.{Deg, Rotation, Vect}
import com.vogonjeltz.physics.render.Colour

/**
  * MainTest
  *
  * Created by fredd
  */
object MainTest extends App {

  val a = Vect(0.001, 0.001)
  println(a.theta.toDeg)


  implicit val universe = new Universe
  /*val particleTypeOne = ParticleType(5.9 * Math.pow(10, 24), 1000, Colour.GREEN, fixed=false)
  val particleTypeTwo = ParticleType(1.9 * Math.pow(10,30), 10000, Colour.YELLOW, fixed=true)*/



  val particleTypeOne = ParticleType(10000000000d, 10, Colour.GREEN, fixed=false)
  val particleTypeTwo = ParticleType(100000000000d, 20, Colour.YELLOW, fixed=true)


  //Hello

/*
  universe.addParticles(List(
    particleTypeOne(Vect(0,0), Vect(0.03d,0)),
    particleTypeOne(Vect(700,600), Vect(-0.03d,0)),
    particleTypeOne(Vect(0,600), Vect(0,-0.03)),
    particleTypeOne(Vect(700,0), Vect(0,0.03)),
    particleTypeTwo(Vect(350, 300), Vect(0.03,-0.03))
  ))
*/

  universe.addParticles(List(
    particleTypeOne(Vect(300, 600), Vect(-0.1,0)),
    particleTypeTwo(Vect(300, 300), Vect(0,0)),
    particleTypeOne(Vect(300, 1000), Vect(-0.1, 0)),
    particleTypeOne(Vect(300, 1500), Vect(-0.07, 0))
  ))

  /*
  universe.addParticles(List(
    particleTypeTwo(Vect(0,0)),
    particleTypeOne(Vect(149 * Math.pow(10,6), 0), Vect(0, 30000))
  ))*/


  /*val pT1 = ParticleType(10, 20, Colour.GREEN)
  val pT2 = ParticleType(5, 10, Colour.RED)

  universe.addParticles(List(
    pT1(Vect(310,600), Vect(0,-2)),
    pT2(Vect(300, 0), Vect(0,2)),
    pT2(Vect(0, 300), Vect(2,0))
  ))*/
  println (universe.particles(0).position - universe.particles(1).position)

  /*
  universe.addParticles(List(
    particleTypeOne(Vect(0,0), Vect(0.03d,0)),
    particleTypeOne(Vect(600,600), Vect(-0.03d,0)),
    particleTypeOne(Vect(0,600), Vect(0,-0.03)),
    particleTypeOne(Vect(600,0), Vect(0,0.03)),
    particleTypeOne(Vect(300,0), Vect(0.03,0)),
    particleTypeOne(Vect(600,300), Vect(0,0.03)),
    particleTypeOne(Vect(300,600), Vect(-0.03,0)),
    particleTypeOne(Vect(0,300), Vect(0,-0.03))
    //particleTypeTwo(Vect(300, 300), Vect(0,-0.03))
  ))*/



  universe.mainloop()

}
