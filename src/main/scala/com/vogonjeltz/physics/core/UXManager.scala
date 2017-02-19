package com.vogonjeltz.physics.core

import com.vogonjeltz.physics.render.Render
import org.lwjgl.input.Keyboard

/**
  * Created by Freddie on 29/01/2017.
  */
class UXManager(val commands: List[Command], val universe: Universe) {

  private var _command = ""
  def command = _command
  def commandObj: Option[Command] = {
    if (command == "") None
    else {
      commands.find(_.name == command)
    }
  }

  private var _lastRes: Option[CommandResult] = None
  def lastResult: String = _lastRes match {
    case None => ""
    case Some(CommandSuccess(text)) => "Success: " + text
    case Some(CommandFailure(text)) => "Fail: "  + text
  }

  private var _params: List[String] = List()
  def params = _params

  val inputBuilder: StringBuilder = new StringBuilder

  def update(): Unit = {
    doKeyboard()
  }

  def onEnterKey(): Option[CommandResult] = {
    if (command == "") {
      val inputs = inputBuilder.toString().split(" ")
      _command = inputs.head
      _params = inputs.tail.toList
    }
    else {
      _params = inputBuilder.toString() :: params
    }
    if (commandObj.isEmpty){
      val fail = Some(CommandFailure("Invalid command '" + command + "'"))
      _command = ""
      return fail
    }

    val actualCommand = commandObj.get

    if (_params.length == actualCommand.paramLength) {
      val res = actualCommand.f(params)
      _params = List()
      _command = ""
      Some(res)
    } else {
      None
    }

  }

  def runCommand(commandString: String, params: List[String]): CommandResult = {

    val commandOption = commands.find(_.name == commandString)
    if (commandOption.isEmpty)
      return CommandFailure(s"Invalid command '$commandString'")

    val command = commandOption.get

    if (params.length != command.paramLength)
      return CommandFailure(s"Wrong number of params for $commandString")

    val res = command.f(params)
    _lastRes = Some(res)
    res

  }

  def doKeyboard(): Unit ={
    while(Keyboard.next()) {
      if (Keyboard.getEventKeyState) {
        if (Keyboard.getEventKey == Keyboard.KEY_EQUALS && Keyboard.getEventKeyState) {
          if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
              Render.changeZoom(1)
              universe.totalZoom += 1
            }
            else {
              Render.setZoom(1.1 * universe.totalZoom)
              //universe.moveSpeed *= 0.9
              universe.totalZoom = Render.zoom.x
            }
          } else {
            universe.maxUPS += 100
            universe.updateSync.setRate(universe.maxUPS)
          }
        }
        else if (Keyboard.getEventKey == Keyboard.KEY_MINUS && Keyboard.getEventKeyState) {
          if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if(Render.zoom.x > 0.1) {
              if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                Render.changeZoom(-1)
                universe.totalZoom -= 1
              } else {
                Render.setZoom(0.9 * universe.totalZoom)
                //universe.moveSpeed *= 0.9
                universe.totalZoom = Render.zoom.x
              }
            }
          } else if (universe.maxUPS > 99) {
            universe.maxUPS -= 100
            universe.updateSync.setRate(universe.maxUPS)
          }
        }

        if (Keyboard.getEventCharacter.isLetterOrDigit || Keyboard.getEventCharacter == ' ' || Keyboard.getEventCharacter == '.') {
          inputBuilder.append(Keyboard.getEventCharacter)
        }
        else if (Keyboard.getEventKey == Keyboard.KEY_BACK) {
          if (inputBuilder.nonEmpty)
            inputBuilder.length -= 1
        }
        else if (Keyboard.getEventKey == Keyboard.KEY_RETURN) {
          _lastRes = onEnterKey()
          inputBuilder.clear()
        }
      }
    }
  }

}

sealed class CommandResult

case class CommandSuccess(text: String) extends CommandResult

case class CommandFailure(text: String) extends CommandResult

case class Command(name: String, paramLength: Int, f: (List[String]) => CommandResult) {

}