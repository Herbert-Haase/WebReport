## WebScraper
[![Coverage Status](https://coveralls.io/repos/github/Herbert-Haase/WebScraper/badge.svg?branch=main)](https://coveralls.io/github/Herbert-Haase/WebScraper?branch=main)

WebScraper is a hybrid application built in Scala, demonstrating fundamental Software Engineering design patterns such as **Model-View-Controller (MVC)**, **Command/Memento** for undo/redo functionality, **Observer** for view synchronization, and **Decorator/Template Method** for flexible reporting (TUI). It allows users to load text from local files, manually input, or download content from a specified URL, and then analyze and filter the data.

The application provides both a fully featured **Graphical User Interface (GUI)** using ScalaFX and a responsive **Terminal User Interface (TUI)**.

---

### Features

* **Data Input:** Load content from file path (`file <path>`), manual text input (`text`), or remote URL (`download <url>`).
* **GUI/TUI Synchronization:** All loaded and filtered data is immediately reflected in both the GUI and TUI via the **Observer Pattern**.
* **History & Commands:** Full **Undo/Redo** functionality implemented using the **Command** and **Memento** patterns.
* **Data Analysis:** Automatically calculates character count, word count, and displays the top 5 most common words.
* **Filtering:** Filter loaded content based on a keyword.
* **Flexible Reporting (TUI):** Use the **Decorator Pattern** to add features to the report, such as line numbers and forced lower-casing.
* **HTML Support:** Automatically renders content using a built-in `WebView` if the downloaded data is detected as HTML.

---

### Architecture and Design Patterns

The entire application is structured around clean design principles:

| Component | Design Pattern | Responsibility |
| :--- | :--- | :--- |
| **`Controller`** | **Command, Memento, Observer** | The central hub. Receives commands from the views (TUI/GUI), executes them via `UndoManager`, and notifies all attached views of data changes. |
| **`Tui` / `Gui`** | **Observer** | The views. They subscribe to the `Controller` and render the current state of the `Data` model whenever a change occurs. |
| **`Data` (Model)** | – | Holds the original content, the currently filtered/displayed content, and all calculated statistics (e.g., word count). |
| **`UndoManager`** | **Command, Memento** | Manages the command history (`undoStack`, `redoStack`). Commands encapsulate actions (`LoadCommand`, `FilterCommand`, `DownloadCommand`) and their rollback logic.  |
| **`Renderer`** | **Template Method, Decorator** | In the TUI, `SimpleReport` uses the **Template Method** to define the overall structure (Header, Body, Footer). `LineNumberDecorator` and `LowerCaseDecorator` use the **Decorator Pattern** to dynamically enhance the output.  |

---

### Getting Started (SBT)

This project requires **Scala 3** and is built using **SBT (Scala Build Tool)**.

#### Prerequisites

* Java Development Kit (JDK) 21+
* SBT (Scala Build Tool)

#### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Herbert-Haase/WebScraper.git
    cd WebScraper
    ```

2.  **Start the application:**
    The application runs with both the GUI and TUI simultaneously. The TUI requires explicit input connection (`connectInput in run := true` in `build.sbt`) to ensure it remains responsive while the GUI is open.

    ```bash
    sbt run
    ```

3.  **Use the GUI:** The main window will appear. Use the top toolbar to load data via **Open** (file), **URL** (download), or use the **Filter** box.
4.  **Use the TUI:** In your terminal, you can interact with the TUI command line while the GUI is running.

    ```
    Welcome to WebScraper

    [Start] Enter 'file <path>', 'text', 'download <url>', or 'exit':
    > download [https://example.com](https://example.com)
    ```

#### Running Tests

To execute the unit tests for the core logic, including the Command/Memento architecture and the new `DownloadCommand`:

```bash
sbt test
