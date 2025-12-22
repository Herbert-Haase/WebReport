package de.htwg.webscraper.aview

import _root_.de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.model.fileio.FileIO
import _root_.de.htwg.webscraper.util.Observer
import scala.io.StdIn.readLine


class Tui(controller: ControllerInterface, val fileIO: FileIO) extends Observer {
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
    print("\u001b[H\u001b[2J") 
    System.out.flush()
    println(s"${Console.BOLD}${Console.CYAN} WEB SCRAPER v1.0 ${Console.RESET}")
    inputLoop()
  }

  def inputLoop(): Unit = {
    state.displayPrompt()
    Option(readLine()) match {
      case Some(input) =>
        state.handleInput(input, this, controller, fileIO)
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

  private def getTerminalWidth: Int = {
    import scala.sys.process._
    try {
      // Queries the terminal for its width; defaults to 80 if it fails
      "tput cols".!!.trim.toInt
    } catch {
      case _: Exception => 80 
    }
  }

  private def renderMetrics(): Unit = {
    val stats = f"${Console.BOLD}Chars:${Console.RESET} ${controller.data.characterCount}%-8s " +
                f"${Console.BOLD}Words:${Console.RESET} ${controller.data.wordCount}%-8s " +
                f"${Console.BOLD}Links:${Console.RESET} ${Console.BLUE}${controller.data.linkCount}${Console.RESET}"
    
    println(s"│ $stats │")
    println(s"│ Complexity: ${renderComplexityBar(controller.data.complexity)} │")
  }
  override def update(isFilterUpdate: Boolean): Unit = {
    val width = getTerminalWidth
    val d = controller.data
    
    // Clear screen for that "Application" feel
    print("\u001b[H\u001b[2J")
    
    // Render the box with dynamic width
    println(renderer.render(d, width))
    
    println(s"${Console.BOLD}[Metrics]${Console.RESET}")
    println(s" Images: ${d.imageCount} | Links: ${d.linkCount}")
    println(s" Complexity: ${renderComplexityBar(d.complexity)}")
    
    val visibleLibs = d.libraries.filter(l => famousLibs.exists(fl => l.toLowerCase.contains(fl)))
    println(s" Famous Libs: ${if (visibleLibs.isEmpty) "None" else visibleLibs.mkString(", ")}")
    
    println("─" * width) // Dynamic separator
  }

}