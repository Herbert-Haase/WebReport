package de.htwg.webscraper.util

import de.htwg.webscraper.model.data.ProjectData

// --- Memento Pattern ---
case class Memento(state: ProjectData)

trait Originator {
  def createMemento(): Memento
  def restore(m: Memento): Unit
}

// --- Command Pattern ---
trait Command {
  def execute(): Unit
  def undo(): Unit
  def redo(): Unit
}

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    undoStack = command :: undoStack
    command.execute()
    redoStack = Nil // New action clears redo history
  }

  def undoStep(): Unit = {
    undoStack match {
      case Nil =>
      case head :: stack =>
        head.undo()
        undoStack = stack
        redoStack = head :: redoStack
    }
  }

  def redoStep(): Unit = {
    redoStack match {
      case Nil =>
      case head :: stack =>
        head.redo()
        redoStack = stack
        undoStack = head :: undoStack
    }
  }
}