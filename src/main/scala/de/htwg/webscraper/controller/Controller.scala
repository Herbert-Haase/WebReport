package de.htwg.webscraper.controller

import de.htwg.webscraper.model.Data
import de.htwg.webscraper.util.{Command, Memento, Originator, UndoManager}
import scala.io.Source
import scala.util.{Using, Try, Success, Failure}
import scala.compiletime.uninitialized

class Controller extends Observable with Originator {
  var data: Data = Data(List.empty)
  private val undoManager = new UndoManager

  // --- Memento Impl ---
  override def createMemento(): Memento = Memento(data)
  override def restore(m: Memento): Unit = {
    data = m.state
    notifyObservers()
  }

  // --- Commands ---
  class LoadCommand(path: Option[String], manualText: Option[String]) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento() // Save state before change

      val inputTry: Try[List[String]] =
        path.map(p => Using(Source.fromFile(p))(_.getLines().toList))
          .getOrElse(
            manualText.filter(_.nonEmpty)
              .map(t => Success(t.split("\n").toList))
              .getOrElse(Failure(new IllegalArgumentException("No input provided")))
          )

      inputTry match {
        case Success(lines) =>
          data = Data(lines)
        case Failure(e) =>
          data = Data(List(s"Error: ${e.getMessage}"))
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
      val filteredLines = Option(word)
        .filter(_.nonEmpty)
        .map(w => data.originalLines.filter(_.toLowerCase.contains(w.toLowerCase)))
        .getOrElse(data.originalLines)

      data = Data.fromFiltered(data.originalLines, filteredLines)
      notifyObservers(isFilterUpdate = true)
    }

    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  // --- API ---
  def loadFromFile(path: String): Try[Unit] =
    Try(undoManager.doStep(new LoadCommand(Some(path), None)))

  def loadFromText(text: String): Try[Unit] =
    Try(undoManager.doStep(new LoadCommand(None, Some(text))))

  def filter(word: String): Unit = undoManager.doStep(new FilterCommand(word))
  def undo(): Unit = undoManager.undoStep()
  def redo(): Unit = undoManager.redoStep()
  def reset(): Unit = {
      data = Data(List.empty)
      notifyObservers()
  }
}