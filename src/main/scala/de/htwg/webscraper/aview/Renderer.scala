package de.htwg.webscraper.aview

import de.htwg.webscraper.model.data.ProjectData
import scala.collection.mutable.ListBuffer

trait Renderer {
  def render(data: ProjectData, width: Int): String
}


abstract class ReportTemplate extends Renderer {

  final override def render(data: ProjectData, width: Int): String = {
    val b = new StringBuilder()
    val effectiveWidth = if (width > 20) width else 80
    
    b.append(buildHeader(effectiveWidth))
    b.append(buildBody(data, effectiveWidth))
    b.append(buildFooter(effectiveWidth))

    if (data.source != "empty") {
      b.append(buildDashboard(data, effectiveWidth))
    }
    b.toString()
  }

  protected def buildHeader(width: Int): String = "┌" + "─" * (width - 2) + "┐\n"
  protected def buildFooter(width: Int): String = "└" + "─" * (width - 2) + "┘\n"

  protected def buildBody(data: ProjectData, width: Int): String


  private def buildDashboard(data: ProjectData, width: Int): String = {
    val commonWords = data.mostCommonWords.take(5).map { case (w, c) => s"'$w'($c)" }.mkString(", ")
    
    val maxBar = 15
    val filled = Math.min((data.complexity / 6.0).toInt, maxBar)
    val bar = Console.RED + "█" * filled + Console.RESET + "░" * (maxBar - filled)
    val barColor = if (data.complexity < 20) Console.GREEN 
                else if (data.complexity < 50) Console.YELLOW 
                else Console.RED

    val statsRow = f" ${Console.BOLD}CHARS:${Console.RESET} ${data.characterCount}%-6s │ " +
                   f"${Console.BOLD}WORDS:${Console.RESET} ${data.wordCount}%-6s │ " +
                   f"${Console.BOLD}LINKS:${Console.RESET} ${data.linkCount}%-4s │ " +
                   f"${Console.BOLD}IMAGES:${Console.RESET} ${data.imageCount}"
    
    val famousLibs = Set(
    "react", "angular", "vue", "svelte", "jquery", "bootstrap", 
    "tailwind", "d3", "three", "lodash", "moment", "axios", "spring", "guice"
    )
    val visibleLibs = data.libraries.filter(l => famousLibs.exists(fl => l.toLowerCase.contains(fl)))

    val dashboard = new StringBuilder()
    dashboard.append(s"\n${Console.BOLD} DASHBOARD ${Console.RESET}\n")
    dashboard.append(s" $statsRow\n")
    dashboard.append(s" ${Console.BOLD} LIBS :${Console.RESET} ${if (visibleLibs.isEmpty) "None" else visibleLibs.mkString(", ")}\n")
    dashboard.append(s" ${barColor} COMPLEXITY:${Console.RESET} [$bar] (${data.complexity})\n")
    dashboard.append(s" ${Console.BOLD} TOP WORDS :${Console.RESET} $commonWords\n")
    dashboard.append("─" * width + "\n")
    dashboard.toString()
  }
}

class SimpleReport extends ReportTemplate {
  override protected def buildBody(data: ProjectData, width: Int): String = {
    if (data.displayLines.isEmpty || data.source == "empty") {
      return renderWelcome(width)
    }
    val contentWidth = width - 4
    
    val cleanLines = data.displayLines.flatMap { rawLine =>
      rawLine.replace("\t", "    ").split("\n")
    }
    
    val wrapped = wrapLines(cleanLines, contentWidth)

    wrapped.map { line =>
      val flatLine = line.replaceAll("[\n\r]", "")
      val paddedLine = flatLine.padTo(contentWidth, ' ')
      val coloredLine = colorizeHtml(paddedLine)
      s"│ $coloredLine │\n"
    }.mkString
  }

  private def renderWelcome(width: Int): String = {
    val contentWidth = width - 4
    val msg = List(
      "",
      s"${Console.BOLD}${Console.CYAN}Welcome to WebScraper${Console.RESET}",
      "",
      "Type 'download <url>' to fetch a website,",
      "or 'load <path>' to open a file.",
      "",
      s"${Console.YELLOW}Use the GUI for mouse interactions.${Console.RESET}",
      ""
    )
    
    msg.map { text =>
      val rawLen = text.replaceAll("\u001B\\[[;\\d]*m", "").length // calculate length without colors
      val padding = (contentWidth - rawLen) / 2
      val line = " " * padding + text + " " * (contentWidth - padding - rawLen)
      s"│ $line │\n"
    }.mkString
  }

  private def colorizeHtml(text: String): String = {
    val tagColor = Console.YELLOW
    val attrColor = Console.MAGENTA
    val reset = Console.RESET

    text
      .replaceAll("(<[^>]+>)", s"$tagColor$$1$reset")
      .replaceAll("(\\w+)=(['\"])", s"$attrColor$$1$reset=$$2") 
  }

  private def wrapLines(lines: List[String], width: Int): List[String] = {
    val wrappedLines = ListBuffer[String]()
    
    for (line <- lines) {
      val words = line.split(" ")
      var currentLine = new StringBuilder()

      for (word <- words) {
        if (word.length > width) {
          if (currentLine.nonEmpty) {
            wrappedLines += currentLine.toString()
            currentLine.clear()
          }
          word.grouped(width).foreach { chunk =>
             if (chunk.length == width) wrappedLines += chunk
             else currentLine.append(chunk)
          }
        }
        else if (currentLine.length + (if (currentLine.nonEmpty) 1 else 0) + word.length > width) {
          wrappedLines += currentLine.toString()
          currentLine.clear()
          currentLine.append(word)
        }
        else {
          if (currentLine.nonEmpty) currentLine.append(" ")
          currentLine.append(word)
        }
      }
      if (currentLine.nonEmpty) wrappedLines += currentLine.toString()
    }
    wrappedLines.toList
  }
}

abstract class RendererDecorator(decorated: Renderer) extends Renderer {

  override def render(data: ProjectData, width: Int): String = decorated.render(data, width)
}

class LineNumberDecorator(decorated: Renderer) extends RendererDecorator(decorated) {
  override def render(data: ProjectData, width: Int): String = {
    val rawOutput = decorated.render(data, width)
    val lines = rawOutput.split("\n")
    var counter = 1
    lines.map { line =>
      if (line.startsWith("│")) { 
        val l = f"${counter}%2d. $line"
        counter += 1
        l
      } else line
    }.mkString("\n")
  }
}