package com.vogonjeltz.physics.core

import com.vogonjeltz.physics.math.shapes.Circle
import com.vogonjeltz.physics.math.{Deg, Rotation, Vect}
import com.vogonjeltz.physics.render.{Colour, Frame, Render}
import org.lwjgl.opengl.GL11._

import Vect.VectHelper
/**
  * Particle
  *
  * Created by fredd
  */
class Particle(private var _rotation: Rotation, private var _position: Vect, val particleType: ParticleType, private var _velocity: Vect = Vect.ZERO)(implicit val universe: Universe) {

  val GRAVITATIONAL_CONSTANT: Double = 6.674 * Math.pow(10, -11)
  val MAX_PATH_LENGTH = 10000
  val REST_COEFF = 1

  val mass: Double = particleType.mass
  val radius: Double = particleType.radius

  val DELTA_TIME = 1

  def velocity = _velocity
  def momentum = velocity/mass

  def rotation: Rotation = _rotation
  def position: Vect = _position

  private var _tick = new Tick()
  private var _lastTick = _tick
  def tick: Tick = _tick
  def lastTick: Tick = _lastTick

  private var doPath = 0

  private var path: List[PathPoint] = List()

  def collide(that : Particle): List[Force] = {
    println("COLLIDE")
    val u1 = this.velocity
    val u2 = that.velocity
    val m1 = this.mass
    val m2 = that.mass

    val v1 = ( ( (u1 - u2) * (REST_COEFF * m1) ) + (u1 * m1) + (u2 * m2)) / (m1 + m2)
    val v2 = ( ( (u2 - u1) * (REST_COEFF * m2) ) + (u1 * m1) + (u2 * m2)) / (m1 + m2)

    println("Momentum difference " + (((v1 * m1) + (v2 * m2)) - ((u1 * m1) + (u2 * m2)) ).length/((u1 * m1) + (u2 * m2)).length )

    //val v2 = ( ( REST_COEFF * m1 * (u1.abs + u2.abs) ) - (u2 * m2) - (u1 * m1) ) / (m1 - m2)
    //val v1 = ((u1 * m1) + (u2 * m2) - (v2 * m2)) / m1

    if (v1.length > 2) {
      println()
    }

    val impulse1 = ((v1 * m1) - (u1 * m1)) / DELTA_TIME
    val impulse2 = ((v2 * m2) - (u2 * m2)) / DELTA_TIME

    val forces = List(
      Force(this, impulse1.length, (this.position - that.position).theta),
      Force(that, impulse2.length, (that.position - this.position).theta)
    )

    forces
  }

  def interact(that: Particle): List[Force] = {
    val distance = that.position.distance(position)
    val gravForce = (GRAVITATIONAL_CONSTANT * mass * that.mass) / Math.pow(distance, 2)

    val collisionForces:List[Force] = if (this.circle.intersect(that.circle)) collide(that) else List()

    List(
      Force(that, gravForce, (this.position - that.position).theta),
      Force(this, gravForce, (that.position - this.position).theta)
    ) ++ collisionForces
  }

  def accept(f: Force): Unit = tick.addForce(f)

  def render(): Unit = {
    Render.withContext(Frame()) {

      glBegin(GL_POINTS)
      for (point <- path) {
        val (r,g,b) = point.colour
        glColor3d(r,g,b)
        glVertex2d(point.position.x, point.position.y)
      }
      glEnd()
    }
    circle.render()
    if (particleType.fixed) return
    for (force <- lastTick.forces) force.render()
  }

  def runTick(): Unit = {
    //println(mass)
    if (particleType.fixed) return

    doPath += 1
    if ((doPath % 6 == 0 && path.nonEmpty && path.head.position.distance(position) > 1) || path.isEmpty)  {
      doPath = 0
      path ::= PathPoint(position, velocity.length)
      if (path.length > MAX_PATH_LENGTH) path = path.init
    }

    val netForce = tick.forces.map(_.toVect).foldLeft(Vect.ZERO)(_ + _)
    //println(netForce.distance(Vect.ZERO))
    //println(s"Force: $netForce")
    val acceleration = netForce/mass
    //println(acceleration)
    _velocity = velocity + (acceleration * DELTA_TIME)
    _position = position + (velocity * DELTA_TIME)

    _lastTick = tick
    _tick = new Tick()

  }

  def circle: Circle = new Circle(position, radius, particleType.colour, filled = false/*!particleType.fixed*/)

}

case class ParticleType(mass: Double, radius: Double, colour:Colour = Colour.WHITE, fixed: Boolean = false) {

  def apply(position: Vect, velocity: Vect = Vect.ZERO)(implicit universe: Universe): Particle = new Particle(Rotation.zero, position, this, velocity)
  
}

case class PathPoint(position: Vect, speed: Double) {

  private def cap(x: Double) = if (x > 1) 1d else x

  val colour: (Double, Double, Double) = {
    val g = 0d
    val r = cap(10d * Math.abs(speed))
    val b = cap(10d / Math.abs(speed))
    (r,g,b)
  }

}