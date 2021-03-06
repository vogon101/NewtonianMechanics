package com.vogonjeltz.physics.utils.config

/**
  * Created by Freddie on 11/12/2016.
  */
abstract class DisplaySettings {


  val height: Int = 640
  val width : Int = (height/9)*16

  val updateSpeed: Int = 60
  val fpsCap: Int = 300

  protected var _title = "SimpleGamee"

  def title = _title

  def setTitle(t: String) = _title = t

}
