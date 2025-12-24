package de.htwg.webreport.util

import de.htwg.webreport.model.data.DataTrait

// --- Memento Pattern ---
case class Memento(state: DataTrait)

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