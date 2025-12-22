package de.htwg.webscraper.aview

import _root_.de.htwg.webscraper.controller.ControllerInterface
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer
import scala.util.{Success, Failure}
import de.htwg.webscraper.model.fileio.FileIO

trait TuiState {
  def handleInput(input: String, tui: Tui, controller: ControllerInterface, fileIO: FileIO): Unit
  def displayPrompt(): Unit
}

class InitialState extends TuiState {
  override def displayPrompt(): Unit = {
    val help = s"${Console.WHITE}(download <url> | load <path> | text | exit)${Console.RESET}"
    val prompt = s"${Console.CYAN}${Console.BOLD} scraper>${Console.RESET} "
    
    println(s"\n$help")
    print(prompt)
  }

  override def handleInput(input: String, tui: Tui, controller: ControllerInterface, fileIO: FileIO): Unit = {
    val parts = input.trim.split("\\s+", 2)
    val command = parts(0).toLowerCase
    val argument = if (parts.length > 1) parts(1) else ""

    command match {
      case "download" => 
        tui.changeState(new FilterState)
        controller.downloadFromUrl(argument)
        
      case "import" | "load" => 
        if (argument.nonEmpty) {
          tui.changeState(new FilterState)
          controller.loadFromFile(argument)
        } else {
          println("Usage: import <filepath>")
          tui.update(false) 
        }
      case "text" =>
        println("Enter text. Type '.' on a new line to finish:")
        val buffer = ListBuffer[String]()
        var lineOpt = Option(readLine())
        while (lineOpt.isDefined && lineOpt.get != ".") {
          buffer += lineOpt.get
          lineOpt = Option(readLine())
        }
        tui.changeState(new FilterState)
        controller.loadFromText(buffer.mkString("\n"))
        
      case "exit" | "quit" => 
        println("Bye!")
        System.exit(0)
        
      case _ => 
        println(s"Invalid command: $command")
        tui.update(false)
    }
  }
}

class FilterState extends TuiState {
  override def displayPrompt(): Unit = {
    println("\n[Filter] Enter word to filter, 'export <path>', 'ln', 'undo', 'redo', 'reset', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: ControllerInterface, FileIO: FileIO): Unit = {
    val parts = input.split(" ").toList

    parts match {
      case "undo" :: Nil => controller.undo()
      case "redo" :: Nil => controller.redo()
      case "ln" :: Nil => tui.toggleLineNumbers()
      case "reset" :: Nil =>
        controller.reset()
        tui.changeState(new InitialState)
      case "exit" :: Nil => System.exit(0)
      case "save" :: Nil => 
        println("Enter filename:")
        val path = readLine()
        controller.saveSession(path)
      case "load" :: Nil =>
        println("Enter filename:")
        val path = readLine()
        controller.loadFromFile(path)


      case _ =>
        if (parts.length == 1 && parts.head.nonEmpty) {
          controller.filter(parts.head)
        } else if (input.nonEmpty) {
          println("Invalid command.")
        }
    }
  }
}