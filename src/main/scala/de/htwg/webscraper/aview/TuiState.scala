package de.htwg.webscraper.aview

import _root_.de.htwg.webscraper.controller.ControllerInterface
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer

trait TuiState {
  def handleInput(input: String, tui: Tui, controller: ControllerInterface): Unit
  def displayPrompt(): Unit
}

class InitialState extends TuiState {
  override def displayPrompt(): Unit = {
    println("\n[Start] Enter 'file <path>', 'text', 'download <url>', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: ControllerInterface): Unit = {
    input.split(" ").toList match {
      case "file" :: path :: Nil => 
        controller.loadFromFile(path)
        tui.changeState(new FilterState)
        
      case "download" :: url :: Nil =>
        println(s"Downloading from $url ...")
        controller.downloadFromUrl(url)
        tui.changeState(new FilterState)

      case "text" :: Nil =>
        println("Enter text. Type '.' on a new line to finish:")
        val buffer = ListBuffer[String]()
        var lineOpt = Option(readLine())
        while (lineOpt.isDefined && lineOpt.get != ".") {
          buffer += lineOpt.get
          lineOpt = Option(readLine())
        }
        controller.loadFromText(buffer.mkString("\n"))
        tui.changeState(new FilterState)
        
      case "exit" :: Nil => System.exit(0)
      case _ => println("Invalid command.")
    }
  }
}

class FilterState extends TuiState {
  override def displayPrompt(): Unit = {
    println("\n[Filter] Enter word to filter, 'undo', 'redo', 'reset', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: ControllerInterface): Unit = {
    input.toLowerCase match {
      case "undo" => controller.undo()
      case "redo" => controller.redo()
      case "numbers" => tui.toggleLineNumbers()
      case "lower" => tui.toggleLowerCase()
      case "reset" => 
        controller.reset()
        tui.changeState(new InitialState)
      case "exit" => System.exit(0)
      case "" => // Ignore empty
      case word => controller.filter(word)
    }
  }
}