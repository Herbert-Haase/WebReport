package de.htwg.webscraper.aview

import _root_.de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.controller.exporter.Exporter
import _root_.de.htwg.webscraper.util.Observer
import scala.io.StdIn.readLine


class Tui(controller: ControllerInterface, val exporter: Exporter) extends Observer {
  controller.add(this)

  private val famousLibs = Set(
    "react", "angular", "vue", "svelte", "jquery", "bootstrap", 
    "tailwind", "d3", "three", "lodash", "moment", "axios", "spring", "guice"
  )

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
        state.handleInput(input, this, controller, exporter)
        inputLoop()
      case None =>
        println("\nExiting.")
    }
  }

  private def renderComplexityBar(score: Int): String = {
    // Normalizing score 0-100 for display. 
    // Low (Green) < 20, Med (Yellow) < 50, High (Red) > 50
    val maxBar = 20
    val filled = Math.min((score / 10.0).toInt, maxBar)
    val bar = "=" * filled + ">" + " " * (maxBar - filled)
    
    val color = if (score < 20) Console.GREEN 
                else if (score < 50) Console.YELLOW 
                else Console.RED
    
    s"[$color$bar${Console.RESET}] ($score)"
  }

    override def update(isFilterUpdate: Boolean): Unit = {
    val d = controller.data
    println("-" * 60)
    println(s"[Metrics]")
    println(s" Chars: ${d.characterCount} | Words: ${d.wordCount} | Lines: ${d.lineCount}")
    println(s" Images: ${d.imageCount} | Links: ${d.linkCount}")
    
    println(s" Complexity: ${renderComplexityBar(d.complexity)}")
    
    val visibleLibs = d.libraries.filter(l => famousLibs.exists(fl => l.toLowerCase.contains(fl)))
    println(s" Famous Libs: ${if (visibleLibs.isEmpty) "None detected" else visibleLibs.mkString(", ")}")
    
    println("-" * 60)
    println(renderer.render(controller.data, 60))
    if (isFilterUpdate) {
      println(s">> Filter active. Matches: ${controller.data.displayLines.size}")
    }
  }
}