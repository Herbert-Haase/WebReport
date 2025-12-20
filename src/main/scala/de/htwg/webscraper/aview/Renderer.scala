package de.htwg.webscraper.aview

import de.htwg.webscraper.model.data.ProjectData
import scala.collection.mutable.ListBuffer

trait Renderer {
  def render(data: ProjectData, width: Int): String
}

abstract class ReportTemplate extends Renderer {
  final override def render(data: ProjectData, width: Int): String = {
    val b = new StringBuilder()
    b.append(buildHeader(width))
    b.append(buildBody(data, width))
    b.append(buildFooter(width))
    b.append(buildStats(data))
    b.toString()
  }

  protected def buildHeader(width: Int): String = "+" + "-" * width + "+\n"
  protected def buildFooter(width: Int): String = "+" + "-" * width + "+\n"

  protected def buildBody(data: ProjectData, width: Int): String

  protected def buildStats(data: ProjectData): String = {
    val commonWords = data.mostCommonWords.map { case (w, c) => s"'$w'($c)" }.mkString(", ")
    s"\n[Stats] Chars: ${data.characterCount} | Words: ${data.wordCount}\n[Top Words] $commonWords\n"
  }
}

class SimpleReport extends ReportTemplate {
  override protected def buildBody(data: ProjectData, width: Int): String = {
    // FIX: Use wrapLines logic instead of just truncating
    val wrapped = wrapLines(data.displayLines, width)

    wrapped.map { line =>
      "|" + line.padTo(width, ' ') + "|\n"
    }.mkString
  }

  // Restored Word Wrapping Logic
  private def wrapLines(lines: List[String], width: Int): List[String] = {
    val wrappedLines = ListBuffer[String]()

    for (line <- lines) {
      if (line.isEmpty) {
        wrappedLines += ""
      } else {
        val words = line.split(" ")
        var currentLine = new StringBuilder()

        for (word <- words) {
          if (word.length > width) {
             if (currentLine.nonEmpty) {
               wrappedLines += currentLine.toString()
               currentLine.clear()
             }
             // Chunk huge words
             word.grouped(width).foreach(chunk => wrappedLines += chunk)
          }
          else if (currentLine.length + 1 + word.length > width) {
            wrappedLines += currentLine.toString()
            currentLine.clear()
            currentLine.append(word)
          }
          else {
            if (currentLine.nonEmpty) currentLine.append(" ")
            currentLine.append(word)
          }
        }
        if (currentLine.nonEmpty) {
          wrappedLines += currentLine.toString()
        }
      }
    }
    wrappedLines.toList
  }
}

// Decorators
abstract class RendererDecorator(decorated: Renderer) extends Renderer {
  override def render(data: ProjectData, width: Int): String = decorated.render(data, width)
}

class LineNumberDecorator(decorated: Renderer) extends RendererDecorator(decorated) {
  override def render(data: ProjectData, width: Int): String = {
    val rawOutput = decorated.render(data, width)
    val lines = rawOutput.split("\n")

    var counter = 1
    lines.map { line =>
      if (line.startsWith("|")) {
        val l = s"$counter. $line"
        counter += 1
        l
      } else {
        line
      }
    }.mkString("\n")
  }
}

class LowerCaseDecorator(decorated: Renderer) extends RendererDecorator(decorated) {
  override def render(data: ProjectData, width: Int): String = {
    val rawOutput = decorated.render(data, width)
    rawOutput.split("\n").map { line =>
      if (line.startsWith("|")) line.toLowerCase
      else line
    }.mkString("\n")
  }
}