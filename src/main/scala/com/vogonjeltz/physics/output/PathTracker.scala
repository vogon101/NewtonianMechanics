package com.vogonjeltz.physics.output

import java.io.{File, PrintWriter}

import com.vogonjeltz.physics.core.Particle
import com.vogonjeltz.physics.math.Vect

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 05/02/2017.
  */
class PathTracker(val particles: List[Particle], val maxLength: Int = 0, val addEvery: Int = 3600) {

  private var counter= 0

  private val _path: List[ListBuffer[Vect]] = for (p <- particles) yield ListBuffer[Vect]()

  def paths: List[List[Vect]] = _path.map(_.toList)



  def update(): Unit = {
    val doAdd = (maxLength == 0 || paths.head.length < maxLength) && counter >= addEvery
    for ((p, i) <- particles.zipWithIndex) {
      if (doAdd) {
        _path(i).append(p.position)
      }
    }
    if(doAdd) counter = 0
    else counter += 1
  }


  def writeToCsv(filePath: String): Unit ={

    val pw = new PrintWriter(new File(filePath))
    val sb = new StringBuilder()

    val rows = ListBuffer[ListBuffer[Vect]]()
    for (path <- paths) {
      for ((point, index) <- path.zipWithIndex) {
        if (!rows.isDefinedAt(index)) {
          rows.append(ListBuffer())
        }
        rows(index).append(point)
      }
    }

    println(rows(0).length)

    for (row <- rows) {
      for (vect <- row) {
        val x = vect.x
        val y = vect.y
        sb.append(s"$x,$y,")
      }
      sb.append("\n")
    }

    pw.write(sb.toString())
    pw.close()

  }

}
