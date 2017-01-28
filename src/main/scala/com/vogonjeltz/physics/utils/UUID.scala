package com.vogonjeltz.physics.utils

/**
  * UUID
  *
  * Created by fredd
  */
object UUID {

  private var auto_inc: Int = 0

  def apply (): Int = {
    auto_inc += 1
    auto_inc
  }

}
