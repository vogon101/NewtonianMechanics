package com.vogonjeltz.physics.utils

import scala.collection.immutable.HashMap

/**
  * Logger for consistent console output
  * @param prefix String prefix for the output all output will be in the form "[prefix] output"
  * @param level Numerical level, controls when the output is printed to the screen. If Log.level is <= level then the
  *              output will be shown
  * @param colour Colour of the prefix
  */
class Logger(prefix: String, val level: Int = 4, colour: String = Console.WHITE) {

  /**
    * Log text to the screen
    * @param text Text to output
    */
  def log(text: String) =
    if (Log.enabled && level >= Log.level)
      println(
        s"[${if (Log.coloursEnabled) colour else ""}$prefix${if (Log.coloursEnabled) Console.WHITE else ""}] $text"
      )

  /**
    * Log text to screen
    * @param text Text to output
    */
  def apply(text:String) = log(text)

}

object Log {

  private var _enabled = true
  private var _coloursEnabled = true

  //Do not change this value here, this enables all logging by default
  private var _level = 0

  private var _loggers = HashMap[String, Logger]()

  /**
    * Create a Logger
    * @param prefix Prefix of the logger
    * @param level Level of the logger, controls when the output of the logger is shown
    * @param colour Colour of the prefix
    * @return
    */
  def apply(prefix: String, level: Int, colour: String = Console.WHITE):Logger =
    if (_loggers.get(prefix).isDefined) _loggers(prefix)
    else {
      _loggers = _loggers + (prefix -> new Logger(prefix, level, colour))
      _loggers(prefix)
    }

  val verbose = Log("verbose", 0)
  val info = Log("info", 1,  Console.BLUE)
  val warn = Log("warn", 2, Console.YELLOW)
  val success = Log("success", 2, Console.GREEN)
  val error = Log("error", 3, Console.RED)

  /**
    * Enable logging
    */
  def enable() = _enabled = true

  /**
    * Disable logging
    */
  def disable() = _enabled = false

  /**
    * Enable colours on the prefix of logger output
    */
  def enableColours() = _coloursEnabled = true

  /**
    * Disable colours
    */
  def disableColours() = _coloursEnabled = false

  def coloursEnabled = _coloursEnabled
  def enabled = _enabled

  def level = _level

  /**
    * Set the logging level. If the level of the logger is greater than or equal to Log.level then the output will be shown
    * @param l The new level
    */
  def setLevel(l : Int) = _level = l

}
