package de.htwg.webreport.aview

import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.model.fileio.FileIOTrait
import de.htwg.webreport.util.Observer
import scala.io.StdIn.readLine

class Tui(sessionManager: SessionManagerTrait, val fileIO: FileIOTrait) extends Observer {
  sessionManager.add(this)
  private var running = true

  private[aview] var state: TuiState = new InitialState()
  private var renderer: Renderer = new SimpleReport()
  private var showNumbers = false
  private var showLowerCase = false

  def changeState(newState: TuiState): Unit = { this.state = newState }

  def stop(): Unit = { running = false }

  def toggleLineNumbers(): Unit = { showNumbers = !showNumbers; updateRenderer() }
  def toggleLowerCase(): Unit = { showLowerCase = !showLowerCase; updateRenderer() }

  private def updateRenderer(): Unit = {
    var r: Renderer = new SimpleReport()
    if (showNumbers) r = new LineNumberDecorator(r)
    renderer = r
    update(false)
  }

  def run(): Unit = {
    update(false) 
    inputLoop()
  }

  def inputLoop(): Unit = {
    if (running) {
      Option(readLine()) match {
        case Some(input) =>
          state.handleInput(input, this, sessionManager, fileIO)
          inputLoop()
        case None =>
          println("\nExiting.")
      }
    }
  }

  override def update(isFilterUpdate: Boolean): Unit = {
    val width = getTerminalWidth
    
    if (sessionManager.data.source != "empty" && state.isInstanceOf[InitialState]) {
      state = new FilterState()
    } 
    else if (sessionManager.data.source == "empty" && state.isInstanceOf[FilterState]) {
      state = new InitialState()
    }

    print("\u001b[H\u001b[2J")
    System.out.flush()
    
    println(renderer.render(sessionManager.data, width))
    
    state.displayPrompt()
  }

  private def getTerminalWidth: Int = {
    import scala.sys.process._
    try { "tput cols".!!.trim.toInt } catch { case _: Exception => 80 }
  }
}