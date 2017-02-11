package com.vogonjeltz.physics.output

import java.io.{File, PrintWriter}

import com.vogonjeltz.physics.core.Particle
import com.vogonjeltz.physics.math.Vect

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 05/02/2017.
  */
class PathTracker(val particle: Particle, val maxLength: Int = 0, val addEvery: Int = 3600) {

  private var counter = 0

  private val _path: ListBuffer[Vect] = ListBuffer()

  def path: List[Vect] = _path.toList

  def addPoint(v: Vect) =
    if ((maxLength == 0 || path.length < maxLength) && counter >= addEvery) {
      _path.append(v)
      counter = 0
    }
    else counter += 1

  def update(): Unit = addPoint(particle.position)

  def writeToCsv(filePath: String): Unit ={

    val pw = new PrintWriter(new File(filePath))
    val sb = new StringBuilder()

    for (i <- path) {
      sb.append(s"${i.x},${i.y}\n")
    }

    pw.write(sb.toString())
    pw.close()

  }

}
