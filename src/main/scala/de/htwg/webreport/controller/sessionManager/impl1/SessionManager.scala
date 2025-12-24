package de.htwg.webreport.controller.sessionManager.impl1

import com.google.inject.Inject
import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.model.analyzer.AnalyzerTrait
import de.htwg.webreport.model.data.DataTrait
import de.htwg.webreport.model.webClient.WebClientTrait
import de.htwg.webreport.model.fileio.FileIOTrait
import de.htwg.webreport.util.{Command, Memento, Originator, UndoManager}
import scala.util.{Failure, Success, Try, Using}
import scala.io.Source
import scala.compiletime.uninitialized

class SessionManager @Inject() (
    val analyzer: AnalyzerTrait,
    val client: WebClientTrait,
    val fileIO: FileIOTrait
) extends SessionManagerTrait with Originator {

  override def storageMode: String = fileIO.mode

  private var dataState: DataTrait = analyzer.process(List.empty, None, "empty")
  private val undoManager = new UndoManager
  
  private var sessionHistory: List[DataTrait] = Nil

  override def data: DataTrait = dataState

  override def createMemento(): Memento = Memento(dataState)

  override def restore(m: Memento): Unit = {
    dataState = m.state match { case d: DataTrait => d }
    notifyObservers()
  }

  class SetStateCommand(newState: DataTrait) extends Command {
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
    dataState = analyzer.process(List.empty, None, "empty")
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
      val newState = analyzer.process(lines, None, label)
      
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
          val newState = analyzer.process(content.split("\n").toList, None, url)
          dataState = newState
          sessionHistory = sessionHistory :+ newState
        case Failure(e) =>
          val newState = analyzer.process(List(s"Error: ${e.getMessage}"), None, url)
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
      dataState = analyzer.process(dataState.originalLines, Some(filteredLines), dataState.source)
      notifyObservers(isFilterUpdate = true)
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }
}