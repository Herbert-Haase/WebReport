package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.model.fileio.FileIO
import de.htwg.webscraper.util.Observer
import scala.io.StdIn.readLine

class Tui(controller: ControllerInterface, val fileIO: FileIO) extends Observer {
  controller.add(this)

  private var state: TuiState = new InitialState()
  private var renderer: Renderer = new SimpleReport()
  private var showNumbers = false
  private var showLowerCase = false

  def changeState(newState: TuiState): Unit = { this.state = newState }

  def toggleLineNumbers(): Unit = { showNumbers = !showNumbers; updateRenderer() }
  def toggleLowerCase(): Unit = { showLowerCase = !showLowerCase; updateRenderer() }

  private def updateRenderer(): Unit = {
    var r: Renderer = new SimpleReport()
    if (showNumbers) r = new LineNumberDecorator(r)
    renderer = r
    update(false)
  }

  def run(): Unit = {
    // Initial Render to show the Welcome Box
    update(false) 
    inputLoop()
  }

  def inputLoop(): Unit = {
    // REMOVED state.displayPrompt() from here to avoid double prompts
    Option(readLine()) match {
      case Some(input) =>
        state.handleInput(input, this, controller, fileIO)
        inputLoop()
      case None =>
        println("\nExiting.")
    }
  }

  override def update(isFilterUpdate: Boolean): Unit = {
    val width = getTerminalWidth
    
    // --- REACTIVE STATE SYNC ---
    // If data exists but we are in InitialState, auto-switch to FilterState.
    // This allows GUI actions to unlock TUI commands automatically.
    if (controller.data.source != "empty" && state.isInstanceOf[InitialState]) {
      state = new FilterState()
    } 
    // If data was reset (is empty) but we are in FilterState, auto-switch back.
    else if (controller.data.source == "empty" && state.isInstanceOf[FilterState]) {
      state = new InitialState()
    }
    // ---------------------------

    print("\u001b[H\u001b[2J") // Clear Screen
    System.out.flush()
    
    println(renderer.render(controller.data, width))
    
    // The prompt is now part of the update cycle
    state.displayPrompt()
  }

  private def getTerminalWidth: Int = {
    import scala.sys.process._
    try { "tput cols".!!.trim.toInt } catch { case _: Exception => 80 }
  }
}