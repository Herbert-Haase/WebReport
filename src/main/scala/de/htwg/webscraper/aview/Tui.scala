package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.{Controller, Observer}
import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}

class Tui(controller: Controller) extends Observer {
  controller.add(this)
  
  private var state: TuiState = new InitialState()
  private var renderer: Renderer = new SimpleReport() 
  
  // Decorator Toggles
  private var showNumbers = false
  private var showLowerCase = false

  def changeState(newState: TuiState): Unit = {
    this.state = newState
  }
  
  def toggleLineNumbers(): Unit = {
    showNumbers = !showNumbers
    updateRenderer()
  }

  def toggleLowerCase(): Unit = {
    showLowerCase = !showLowerCase
    updateRenderer()
  }

  private def updateRenderer(): Unit = {
    var r: Renderer = new SimpleReport()
    if (showLowerCase) {
      r = new LowerCaseDecorator(r)
    }
    if (showNumbers) {
      r = new LineNumberDecorator(r)
    }
    
    renderer = r
    update(false) // Trigger redraw
  }

  def run(): Unit = {
    println("Welcome to WebScraper")
    inputLoop()
  }

  def inputLoop(): Unit = {
    state.displayPrompt()
    Option(readLine()) match {
      case Some(input) => 
      state.handleInput(input, this, controller)
      inputLoop()
      case None => 
      println("\nEnd of input stream. Exiting.")
      System.exit(0)
    }
  }

  override def update(isFilterUpdate: Boolean): Unit = {

    // Auto-transition to FilterState if data is loaded from GUI

    if (state.isInstanceOf[InitialState] && controller.data.originalLines.nonEmpty) {

      changeState(new FilterState)

    }

    

    println(renderer.render(controller.data, 60))

    if (isFilterUpdate) {

      println(s">> Filter active. Matches: ${controller.data.displayLines.size}")

    }

  }
}