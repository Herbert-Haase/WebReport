package de.htwg.webscraper.controller

import de.htwg.webscraper.model.{Analyzer, WebClient}
import de.htwg.webscraper.util.{Command, Memento, Originator, UndoManager}
import _root_.de.htwg.webscraper.model.ProjectData
import de.htwg.webscraper.util.Observable
import scala.util.{Try, Success, Failure}
import scala.io.Source
import scala.util.Using
import scala.compiletime.uninitialized

trait ControllerInterface extends Observable {
  def data: ProjectData
  def loadFromFile(path: String): Unit
  def loadFromText(text: String): Unit
  def downloadFromUrl(url: String): Unit
  def filter(word: String): Unit
  def undo(): Unit
  def redo(): Unit
  def reset(): Unit
}

class Controller(
    val analyzer: Analyzer, 
    val client: WebClient,
) extends ControllerInterface with Originator {

  private var dataState: ProjectData = analyzer.process(List.empty) 
  private val undoManager = new UndoManager

  override def data: ProjectData = dataState

  override def createMemento(): Memento = Memento(dataState)
  
  override def restore(m: Memento): Unit = {
    dataState = m.state match {
      case d: ProjectData => d
    }
    notifyObservers()
  }

  // --- Commands ---
  class LoadCommand(path: Option[String], manualText: Option[String]) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      val lines = if (path.isDefined) {
        Using(Source.fromFile(path.get))(_.getLines().toList)
          .getOrElse(List(s"Error: Could not read file '${path.get}'"))
      } else {
        manualText.getOrElse("").split("\n").toList
      }
      dataState = analyzer.process(lines)
      notifyObservers()
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  class DownloadCommand(url: String) extends Command {
    var memento: Memento = uninitialized

    override def execute(): Unit = {
      memento = createMemento()
      
      client.download(url) match {
        case Success(content) =>
          dataState = analyzer.process(content.split("\n").toList)
        case Failure(e) =>
          dataState = analyzer.process(List(s"Error downloading from $url", e.getMessage))
      }
      notifyObservers()
    }

    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  class FilterCommand(word: String) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      
      val filteredLines = dataState.originalLines.filter(_.toLowerCase.contains(word.toLowerCase))
      
      dataState = analyzer.process(dataState.originalLines, filteredLines)
      
      notifyObservers(isFilterUpdate = true)
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  // --- API ---
  override def loadFromFile(path: String): Unit = undoManager.doStep(new LoadCommand(Some(path), None))
  override def loadFromText(text: String): Unit = undoManager.doStep(new LoadCommand(None, Some(text)))
  override def downloadFromUrl(url: String): Unit = undoManager.doStep(new DownloadCommand(url))
  override def filter(word: String): Unit = undoManager.doStep(new FilterCommand(word))
  override def undo(): Unit = undoManager.undoStep()
  override def redo(): Unit = undoManager.redoStep()
  
  override def reset(): Unit = {
    dataState = analyzer.process(List.empty)
    notifyObservers()
  }
}