package de.htwg.webscraper.controller.impl1.controller

import com.google.inject.Inject
import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.model.analyzer.Analyzer
import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.model.webClient.WebClient
import de.htwg.webscraper.model.fileio.FileIO
import de.htwg.webscraper.util.{Command, Memento, Originator, UndoManager}
import scala.util.{Failure, Success, Try, Using}
import scala.io.Source
import scala.compiletime.uninitialized

class Controller @Inject() (
    val analyzer: Analyzer,
    val client: WebClient,
    val fileIO: FileIO
) extends ControllerInterface with Originator {

  override def storageMode: String = fileIO.mode

  private var dataState: ProjectData = analyzer.process(List.empty, Nil, "empty")
  private val undoManager = new UndoManager
  
  private var sessionHistory: List[ProjectData] = Nil

  override def data: ProjectData = dataState

  override def createMemento(): Memento = Memento(dataState)

  override def restore(m: Memento): Unit = {
    dataState = m.state match { case d: ProjectData => d }
    notifyObservers()
  }

  class SetStateCommand(newState: ProjectData) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      dataState = newState
      sessionHistory = sessionHistory :+ newState
      notifyObservers()
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  // --- API ---

  override def loadFromFile(path: String): Unit = {
    Try(fileIO.load(path)) match {
      case Success(history) if history.nonEmpty =>
        reset()
        history.foreach { state =>
          undoManager.doStep(new SetStateCommand(state))
        }
      case _ =>
        undoManager.doStep(new LoadCommand(Some(path), None))
    }
  }

  override def saveSession(path: String): Unit = {
    fileIO.save(sessionHistory, path)
  }

  override def downloadFromUrl(url: String): Unit = undoManager.doStep(new DownloadCommand(url))
  override def loadFromText(text: String): Unit = undoManager.doStep(new LoadCommand(None, Some(text)))
  override def filter(word: String): Unit = undoManager.doStep(new FilterCommand(word))
  
  override def undo(): Unit = undoManager.undoStep()
  override def redo(): Unit = undoManager.redoStep()

  override def reset(): Unit = {
    dataState = analyzer.process(List.empty, Nil, "empty")
    sessionHistory = Nil 
    notifyObservers()
  }

  // --- Existing Commands (Refined) ---

  class LoadCommand(path: Option[String], manualText: Option[String]) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      val lines = if (path.isDefined) {
        Using(Source.fromFile(path.get))(_.getLines().toList)
          .getOrElse(List(s"Error reading '${path.get}'"))
      } else {
        manualText.getOrElse("").split("\n").toList
      }
      val label = path.getOrElse("text-input")
      val newState = analyzer.process(lines, Nil, label)
      
      dataState = newState
      sessionHistory = sessionHistory :+ newState
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
          val newState = analyzer.process(content.split("\n").toList, Nil, url)
          dataState = newState
          sessionHistory = sessionHistory :+ newState
        case Failure(e) =>
          val newState = analyzer.process(List(s"Error: ${e.getMessage}"), Nil, url)
          dataState = newState
          sessionHistory = sessionHistory :+ newState
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
      dataState = analyzer.process(dataState.originalLines, filteredLines, dataState.source)
      notifyObservers(isFilterUpdate = true)
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }
}