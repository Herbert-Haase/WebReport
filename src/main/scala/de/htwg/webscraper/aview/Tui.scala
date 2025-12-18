package de.htwg.webscraper.aview

import _root_.de.htwg.webscraper.controller.{ControllerInterface}
import _root_.de.htwg.webscraper.util.Observer
import scala.io.StdIn.readLine

class Tui(controller: ControllerInterface) extends Observer {
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
    if (showLowerCase) r = new LowerCaseDecorator(r)
    if (showNumbers) r = new LineNumberDecorator(r)
    renderer = r
    update(false)
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
        println("\nExiting.")
    }
  }

  override def update(isFilterUpdate: Boolean): Unit = {
    if (state.isInstanceOf[InitialState] && controller.data.originalLines.nonEmpty) {
      changeState(new FilterState)
    }
    println(renderer.render(controller.data, 60))
    if (isFilterUpdate) {
      println(s">> Filter active. Matches: ${controller.data.displayLines.size}")
    }
  }
}