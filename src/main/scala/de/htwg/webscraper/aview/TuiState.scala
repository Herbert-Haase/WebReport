package de.htwg.webscraper.aview

import _root_.de.htwg.webscraper.controller.{ControllerInterface, Exporter}
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer
import scala.util.{Success, Failure}

trait TuiState {
  def handleInput(input: String, tui: Tui, controller: ControllerInterface, exporter: Exporter): Unit
  def displayPrompt(): Unit
}

class InitialState extends TuiState {
  override def displayPrompt(): Unit = {
    println("\n[Start] Enter 'file <path>', 'text', 'download <url>', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: ControllerInterface, exporter: Exporter): Unit = {
    input.split(" ").toList match {
      case "file" :: path :: Nil => 
        controller.loadFromFile(path)
        tui.changeState(new FilterState)
        
      case "download" :: url :: Nil =>
        println(s"Downloading from $url ...")
        controller.downloadFromUrl(url)
        tui.changeState(new FilterState)

      case "export" :: path :: Nil =>
        exporter.exportData(controller.data, path).fold(
          e => println(s"Error: ${e.getMessage}"),
          msg => println(msg)
        )

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
    println("\n[Filter] Enter word to filter, 'export <path>', 'undo', 'redo', 'reset', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: ControllerInterface, exporter: Exporter): Unit = {
    val parts = input.split(" ").toList
    
    parts match {
      case "undo" :: Nil => controller.undo()
      case "redo" :: Nil => controller.redo()
      case "numbers" :: Nil => tui.toggleLineNumbers()
      case "lower" :: Nil => tui.toggleLowerCase()
      case "reset" :: Nil => 
        controller.reset()
        tui.changeState(new InitialState)
      case "exit" :: Nil => System.exit(0)
      
      case "export" :: path :: Nil =>
        exporter.exportData(controller.data, path).fold(
          e => println(s"Error: ${e.getMessage}"),
          msg => println(msg)
        )
        
      case _ => 
        if (parts.length == 1 && parts.head.nonEmpty) {
          controller.filter(parts.head)
        } else if (input.nonEmpty) {
          println("Invalid command.")
        }
    }
  }
}