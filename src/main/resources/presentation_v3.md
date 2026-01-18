---
marp: true
theme: default
paginate: true
style: |
  section {
    background-image: url("./bg-image.jpg");
    background-size: cover;
    background-position: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  .panel {
    background: rgba(255, 255, 255, 0.95);
    border:  1px solid #d1d5da;
    border-radius: 15px;
    padding: 25px 40px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.15);
    width: 85%;
    color: #24292e;
  }
  .panel h1 { margin-top: 0; color: #0366d6; border-bottom: 2px solid #0366d6; }
  .panel h2 { color: #22863a; margin-top: 0; margin-bottom: 15px; }
  .panel h3 { 
    color: #0366d6; 
    border-bottom: 1px solid #eaecef; 
    margin-bottom: 15px; 
    font-size: 1.2em;
    padding-bottom: 8px;
  }
  .panel ul { margin-top: 10px; }
  .panel li { margin-bottom: 8px; font-size: 0.9em; }
  .panel a { color: #0366d6; text-decoration: none; font-weight: bold; }
  .panel table { width: 100%; border-collapse:  collapse; margin:  10px 0; }
  .panel th { padding: 10px 12px; text-align: left; border-bottom: 2px solid #0366d6; background: rgba(3, 102, 214, 0.1); }
  .panel td { padding: 10px 12px; border-bottom: 1px solid #eaecef; }
  .panel code { background: #f6f8fa; padding: 2px 6px; border-radius: 3px; }
  
  .ascii {
    font-family: 'Fira Code', monospace;
    white-space: pre;
    font-size: 12px;
    line-height:  1.1;
    color: #0366d6;
    margin-bottom: 20px;
  }

  /* Timeline */
  .timeline { border-left: 3px solid #0366d6; margin-left: 20px; padding-left: 20px; }
  .timeline-item { margin-bottom: 15px; }
  .timeline-date { font-weight: bold; color: #0366d6; }
  .timeline-desc { color: #24292e; }

  /* Bar chart */
  .bar-row { display: flex; align-items: center; margin:  10px 0; gap: 10px; }
  .bar-label { width: 170px; font-weight: bold; font-size: 0.9em; flex-shrink: 0; }
  .bar-label-larger { width: 230px; font-weight: bold; font-size: 0.9em; flex-shrink: 0; }
  .bar-track { width: 300px; height: 20px; background: #6c757d; border-radius: 10px; overflow: hidden; }
  .bar-fill { display: block; height: 100%; border-radius: 10px; }
  .bar-value { font-weight: bold; font-size: 0.9em; }

  /* Color classes for bars */
  .bar-blue { background: #0366d6; }
  .bar-green { background: #28a745; }
  .bar-yellow { background: #f9a825; }
  .bar-purple { background: #6f42c1; }
  .bar-red { background: #d73a49; }
  .bar-orange { background:  #fd7e14; }
  
  /* Width classes */
  .w-100 { width: 100%; }
  .w-95 { width: 95%; }
  .w-90 { width: 90%; }
  .w-85 { width: 85%; }
  .w-80 { width: 80%; }
  .w-75 { width: 75%; }
  .w-70 { width: 70%; }
  .w-65 { width: 65%; }
  .w-60 { width: 60%; }
  .w-55 { width: 55%; }
  .w-50 { width: 50%; }
  .w-45 { width: 45%; }
  .w-40 { width: 40%; }
  .w-35 { width: 35%; }
  .w-30 { width: 30%; }
  .w-25 { width: 25%; }
  .w-20 { width: 20%; }
  .w-15 { width: 15%; }
  .w-10 { width: 10%; }
  .w-05 { width: 5%; }

  /* File tree */
  .tree { font-family: 'Fira Code', monospace; font-size: 0.75em; line-height: 1.8; }
  .tree-folder { font-weight: bold; }
  .tree-folder-blue { color: #0366d6; }
  .tree-folder-yellow { color: #f9a825; }
  .tree-folder-green { color: #28a745; }
  .tree-folder-purple { color: #6f42c1; }
  .tree-bar { display: inline-block; width: 120px; height: 12px; background: #6c757d; border-radius: 6px; margin-left: 10px; vertical-align: middle; overflow: hidden; }
  .tree-bar-fill { height: 100%; border-radius: 6px; display: block; }

  /* Architecture boxes */
  .arch-layer { border: 2px solid; border-radius: 10px; padding: 15px; margin:  10px 0; text-align: center; }
  .arch-view { background: rgba(3, 102, 214, 0.1); border-color: #0366d6; }
  .arch-controller { background: rgba(249, 168, 37, 0.1); border-color: #f9a825; }
  .arch-model { background: rgba(40, 167, 69, 0.1); border-color: #28a745; }
  .arch-box { display: inline-block; background: white; border: 1px solid currentColor; border-radius: 5px; padding: 8px 15px; margin: 5px; font-size: 0.85em; }
  .arch-box-blue { border-color: #0366d6; }
  .arch-box-yellow { border-color: #f9a825; }
  .arch-box-green { border-color: #28a745; }
  .arrow-down { text-align: center; font-size: 24px; color: #666; }
  .color-blue { color: #0366d6; }
  .color-yellow { color: #f9a825; }
  .color-green { color: #28a745; }
  .color-gray { color: #586069; }

  /* Pipeline */
  .pipeline { display: flex; align-items: center; justify-content: center; gap: 8px; flex-wrap: wrap; margin: 15px 0; }
  .pipeline-step { background: #f6f8fa; border: 2px solid #d1d5da; border-radius: 8px; padding: 10px 15px; text-align: center; font-size: 0.8em; }
  .pipeline-active { background: #28a745; color: white; border-color: #28a745; }
  .pipeline-done { background: #0366d6; color: white; border-color: #0366d6; }
  .pipeline-purple { background: #6f42c1; color: white; border-color: #6f42c1; }
  .pipeline-arrow { font-size: 20px; color: #666; }

  /* Git log */
  .git-log { font-family: 'Fira Code', monospace; font-size: 0.85em; }
  .git-line { margin: 8px 0; padding-left: 20px; border-left: 3px solid #28a745; }
  .git-date { color: #0366d6; font-weight: bold; display: inline-block; width: 100px; }

  /* Legend */
  .legend { display: flex; flex-wrap: wrap; gap: 15px; justify-content: center; margin-top: 15px; padding-top: 15px; border-top: 1px solid #eaecef; }
  .legend-item { display: flex; align-items: center; font-size: 0.85em; }
  .legend-color { width: 16px; height: 16px; border-radius: 3px; margin-right: 8px; display: inline-block; }

  /* Version badges */
  .badge { padding: 2px 6px; border-radius: 3px; font-size: 0.85em; }
  .badge-gray { background: #e1e4e8; }
  .badge-green { background: #dcffe4; }
  .badge-purple { background: #f5f0ff; }
---

<div class="panel">
  <div class="ascii">
██╗    ██╗███████╗██████╗ ██████╗ ███████╗██████╗  ██████╗ ██████╗ ████████╗
██║    ██║██╔════╝██╔══██╗██╔══██╗██╔════╝██╔══██╗██╔═══██╗██╔══██╗╚══██╔══╝
██║ █╗ ██║█████╗  ██████╔╝██████╔╝█████╗  ██████╔╝██║   ██║██████╔╝   ██║   
██║███╗██║██╔══╝  ██╔══██╗██╔══██╗██╔══╝  ██╔═══╝ ██║   ██║██╔══██╗   ██║   
╚███╔███╔╝███████╗██████╔╝██║  ██║███████╗██║     ╚██████╔╝██║  ██║   ██║   
 ╚══╝╚══╝ ╚══════╝╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝      ╚═════╝ ╚═╝  ╚═╝   ╚═╝
  </div>
  <p>A blazingly fast analyzer for web content. </p>
</div>

---

<div class="panel">
  <h2>Project Overview</h2>
  <p><strong>WebReport</strong> is a web content analysis tool built for Software Engineering education at HTWG. </p>
  
  <h3>Key Features</h3>
  <ul>
    <li><strong>Web Content Download</strong> - Fetch and analyze websites</li>
    <li><strong>Text Analysis</strong> - Character/word count, top words, complexity scoring</li>
    <li><strong>Content Filtering</strong> - Filter content by keywords</li>
    <li><strong>Undo/Redo</strong> - Full command history support</li>
    <li><strong>Session Persistence</strong> - Save/load sessions in XML or JSON</li>
    <li><strong>Dual Interface</strong> - TUI (Terminal) + GUI (ScalaFX)</li>
  </ul>
</div>

---

<div class="panel">
  <h2>Development Timeline</h2>
  
  <div class="timeline">
    <div class="timeline-item"><span class="timeline-date">Oct 2025</span> <span class="timeline-desc">Project Initialization — Repository created</span></div>
    <div class="timeline-item"><span class="timeline-date">Nov 2025</span> <span class="timeline-desc">Core Development — MVC Architecture setup</span></div>
    <div class="timeline-item"><span class="timeline-date">Dec 2025</span> <span class="timeline-desc">Feature Implementation — TUI & GUI, Tests, Scoverage</span></div>
    <div class="timeline-item"><span class="timeline-date">Jan 2026</span> <span class="timeline-desc">Final Sprint — Docker, Coverage optimization, GUI improvements</span></div>
  </div>
  
  <h3>Activity Intensity</h3>
  <div class="bar-row">
    <span class="bar-label">Oct 2025</span>
    <span class="bar-track"><span class="bar-fill bar-green w-10"></span></span>
  </div>
  <div class="bar-row">
    <span class="bar-label">Nov 2025</span>
    <span class="bar-track"><span class="bar-fill bar-green w-20"></span></span>
  </div>
  <div class="bar-row">
    <span class="bar-label">Dec 2025</span>
    <span class="bar-track"><span class="bar-fill bar-green w-50"></span></span>
  </div>
  <div class="bar-row">
    <span class="bar-label">Jan 2026</span>
    <span class="bar-track"><span class="bar-fill bar-green w-100"></span></span>
  </div>
</div>

---

<div class="panel">
  <h2>Commit Activity Distribution</h2>
  <p><em>Based on 105 commits (Oct 2025 - Jan 2026)</em></p>
  
  <div class="bar-row">
    <span class="bar-label-larger">Feature Dev</span>
    <span class="bar-track"><span class="bar-fill bar-blue w-65"></span></span>
    <span class="bar-value">33%</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Testing</span>
    <span class="bar-track"><span class="bar-fill bar-green w-40"></span></span>
    <span class="bar-value">21%</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Documentation</span>
    <span class="bar-track"><span class="bar-fill bar-yellow w-35"></span></span>
    <span class="bar-value">17%</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">DevOps/CI</span>
    <span class="bar-track"><span class="bar-fill bar-purple w-25"></span></span>
    <span class="bar-value">11%</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Refactoring</span>
    <span class="bar-track"><span class="bar-fill bar-red w-20"></span></span>
    <span class="bar-value">10%</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Bug Fixes</span>
    <span class="bar-track"><span class="bar-fill bar-orange w-10"></span></span>
    <span class="bar-value">5%</span>
  </div>

  <div class="legend">
    <div class="legend-item"><span class="legend-color bar-blue"></span>Feature Development (35)</div>
    <div class="legend-item"><span class="legend-color bar-green"></span>Testing & Coverage (22)</div>
    <div class="legend-item"><span class="legend-color bar-yellow"></span>Documentation (18)</div>
    <div class="legend-item"><span class="legend-color bar-purple"></span>DevOps & CI/CD (12)</div>
    <div class="legend-item"><span class="legend-color bar-red"></span>Refactoring (10)</div>
    <div class="legend-item"><span class="legend-color bar-orange"></span>Bug Fixes (5)</div>
  </div>
</div>

---

<div class="panel">
  <h2>Technology Stack</h2>
  
  <table>
    <tr><th>Component</th><th>Technology</th><th>Version</th></tr>
    <tr><td>Language</td><td>Scala 3</td><td>3.4.2</td></tr>
    <tr><td>Build Tool</td><td>SBT</td><td>latest</td></tr>
    <tr><td>GUI Framework</td><td>ScalaFX</td><td>21.0.0-R32</td></tr>
    <tr><td>DI Framework</td><td>Google Guice</td><td>7.0.0</td></tr>
    <tr><td>Testing</td><td>ScalaTest</td><td>3.2.19</td></tr>
    <tr><td>Coverage</td><td>Scoverage + Coveralls</td><td>2.1.0</td></tr>
  </table>
</div>

---

<div class="panel">
  <h2>Codebase Structure</h2>
  
  <div class="tree">
    <div><span class="tree-folder tree-folder-blue">src/main/scala/de/htwg/webreport/</span></div>
    <div>├── <span class="tree-folder tree-folder-blue">aview/</span> <span class="tree-bar"><span class="tree-bar-fill bar-blue w-80"></span></span> ~350 LOC</div>
    <div>│   ├── Gui.scala</div>
    <div>│   ├── Tui.scala</div>
    <div>│   ├── TuiState.scala</div>
    <div>│   └── Renderer.scala</div>
    <div>├── <span class="tree-folder tree-folder-yellow">controller/</span> <span class="tree-bar"><span class="tree-bar-fill bar-yellow w-35"></span></span> ~150 LOC</div>
    <div>│   └── sessionManager/</div>
    <div>├── <span class="tree-folder tree-folder-green">model/</span> <span class="tree-bar"><span class="tree-bar-fill bar-green w-55"></span></span> ~250 LOC</div>
    <div>│   ├── data/</div>
    <div>│   ├── analyzer/</div>
    <div>│   ├── webClient/</div>
    <div>│   └── fileio/</div>
    <div>└── <span class="tree-folder tree-folder-purple">util/</span> <span class="tree-bar"><span class="tree-bar-fill bar-purple w-15"></span></span> ~60 LOC</div>
    <div>    ├── Command.scala</div>
    <div>    └── Observer.scala</div>
  </div>
</div>

---

<div class="panel">
  <h2>Test Suite Overview</h2>
  
  <table>
    <tr><th>Test File</th><th>Purpose</th></tr>
    <tr><td>WebReportSpec.scala</td><td>Integration tests</td></tr>
    <tr><td>IntegrationSpec.scala</td><td>Full system tests</td></tr>
    <tr><td>TuiSpec.scala</td><td>Terminal UI tests</td></tr>
    <tr><td>TuiStateSpec.scala</td><td>State pattern tests</td></tr>
    <tr><td>GuiSpec.scala</td><td>GUI component tests</td></tr>
    <tr><td>RendererSpec.scala</td><td>Output rendering tests</td></tr>
    <tr><td>DataSpec.scala</td><td>Data model tests</td></tr>
    <tr><td>ObserverSpec.scala</td><td>Observer pattern tests</td></tr>
  </table>
  
  <p class="color-gray" style="font-style: italic; margin-top: 15px;">Exclusions: Main 87%, Tui 94%, TuiState 97%, Renderer 98%, Gui 64%</p>
</div>

---

<div class="panel">
  <h2>Architecture Overview</h2>
  
  <div class="arch-layer arch-view">
    <strong class="color-blue">VIEW LAYER</strong><br>
    <span class="arch-box arch-box-blue">TUI (Terminal)</span>
    <span class="arch-box arch-box-blue">GUI (ScalaFX)</span>
    <br><em class="color-gray" style="font-size: 0.8em;">← Observer P. | Template P. | Decorater P. | State P.</em>
  </div>
  
  <div class="arch-layer arch-controller">
    <strong class="color-yellow">CONTROLLER LAYER</strong><br>
    <span class="arch-box arch-box-yellow">SessionManager</span>
    <br><em class="color-gray" style="font-size: 0.8em;">← Command Pattern | Memento Pattern</em>
  </div>
  
  <div class="arch-layer arch-model">
    <strong class="color-green">MODEL LAYER</strong><br>
    <span class="arch-box arch-box-green">Analyzer</span>
    <span class="arch-box arch-box-green">WebClient</span>
    <span class="arch-box arch-box-green">FileIO (XML/JSON)</span>
    <br><em class="color-gray" style="font-size: 0.8em;">← Strategy Pattern</em>
  </div>
</div>

---

<div class="panel">
  <table>
    <tr><th>Pattern</th><th>Purpose</th><th>Location</th></tr>
    <tr><td><strong>Observer</strong></td><td>UI Synchronization</td><td>util/Observer.scala</td></tr>
    <tr><td><strong>Command</strong></td><td>Operation Encapsulation</td><td>util/Command.scala</td></tr>
    <tr><td><strong>Memento</strong></td><td>Undo/Redo Support</td><td>util/Command.scala</td></tr>
    <tr><td><strong>State</strong></td><td>TUI Mode Switching</td><td>aview/TuiState.scala</td></tr>
    <tr><td><strong>Template Method</strong></td><td>Report Structure</td><td>aview/Renderer.scala</td></tr>
    <tr><td><strong>Decorator</strong></td><td>Feature Extensions</td><td>aview/Renderer.scala</td></tr>
    <tr><td><strong>Strategy</strong></td><td>Interchangeable Serialization</td><td>model/fileio/</td></tr>
    <tr><td><strong>DI (Guice)</strong></td><td>Loose Coupling</td><td>WebReportModule.scala</td></tr>
  </table>
</div>

---

<div class="panel">
  <h2>Component Complexity</h2>
  
  <div class="bar-row">
    <span class="bar-label-larger">SessionManager</span>
    <span class="bar-track"><span class="bar-fill bar-red w-95"></span></span>
    <span class="bar-value">●●●●●</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Renderer</span>
    <span class="bar-track"><span class="bar-fill bar-yellow w-75"></span></span>
    <span class="bar-value">●●●●○</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Tui</span>
    <span class="bar-track"><span class="bar-fill bar-yellow w-60"></span></span>
    <span class="bar-value">●●●○○</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Gui</span>
    <span class="bar-track"><span class="bar-fill bar-green w-55"></span></span>
    <span class="bar-value">●●●○○</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">Data</span>
    <span class="bar-track"><span class="bar-fill bar-green w-40"></span></span>
    <span class="bar-value">●●○○○</span>
  </div>
  <div class="bar-row">
    <span class="bar-label-larger">FileIO</span>
    <span class="bar-track"><span class="bar-fill bar-green w-30"></span></span>
    <span class="bar-value">●●○○○</span>
  </div>
  
  <p style="text-align: center; font-size: 0.85em;" class="color-gray">Low ◄─────────────────────────────► High</p>
</div>


---

<div class="panel">
  <h2>Dependencies</h2>
  
  <div class="tree">
    <div><span class="tree-folder tree-folder-blue">WebReport 1.0.0</span></div>
    <div>├── org.scala-lang » scala3-library <span class="badge badge-gray">3.4.2</span></div>
    <div>├── org.scalafx » scalafx <span class="badge badge-gray">21.0.0-R32</span></div>
    <div>│   └── org.openjfx » javafx-* <span class="badge badge-gray">21.0.2</span></div>
    <div>├── com.google.inject » guice <span class="badge badge-gray">7.0.0</span></div>
    <div>├── org.scala-lang.modules » scala-xml <span class="badge badge-gray">2.2.0</span></div>
    <div>├── com.typesafe.play » play-json <span class="badge badge-gray">2.10.4</span></div>
    <div>└── <span class="tree-folder tree-folder-green">[test]</span></div>
    <div>    ├── org.scalatest » scalatest <span class="badge badge-green">3.2.19</span></div>
    <div>    └── org.scalactic » scalactic <span class="badge badge-green">3.2.14</span></div>
    <div style="margin-top: 10px;"><span class="tree-folder tree-folder-purple">Plugins</span></div>
    <div>├── org.scoverage » sbt-scoverage <span class="badge badge-purple">2.1.0</span></div>
    <div>└── org.scoverage » sbt-coveralls <span class="badge badge-purple">1.3.15</span></div>
  </div>
</div>


---

<div class="panel">
  <h2>CI/CD Pipeline</h2>
  
  <h3>GitHub Actions Workflow</h3>
  <div class="pipeline">
    <div class="pipeline-step pipeline-active"> Push<br>Trigger</div>
    <span class="pipeline-arrow">→</span>
    <div class="pipeline-step">Build<br>SBT</div>
    <span class="pipeline-arrow">→</span>
    <div class="pipeline-step">Test<br>ScalaTest</div>
    <span class="pipeline-arrow">→</span>
    <div class="pipeline-step">Coverage<br>Scoverage</div>
    <span class="pipeline-arrow">→</span>
    <div class="pipeline-step pipeline-done">Coveralls<br>Report</div>
  </div>
  
  <h3>Docker Build</h3>
  <div class="pipeline">
    <div class="pipeline-step">Dockerfile</div>
    <span class="pipeline-arrow">→</span>
    <div class="pipeline-step">Build<br>OpenJDK</div>
    <span class="pipeline-arrow">→</span>
    <div class="pipeline-step pipeline-purple">Image<br>Ready</div>
  </div>
</div>

---

<div class="panel">
  <h2>Deployment Options</h2>
  
  <table>
    <tr><th>Method</th><th>Command</th><th>Description</th></tr>
    <tr><td><strong>Local Development</strong></td><td><code>sbt run</code></td><td>Launches TUI + GUI simultaneously</td></tr>
    <tr><td><strong>Docker Container</strong></td><td><code>./docker-run.sh</code></td><td>X11 forwarding for GUI support</td></tr>
    <!-- <tr><td><strong>Packaged JAR</strong></td><td><code>sbt package</code></td><td>Standalone executable</td></tr> -->
    <tr><td><strong>Test Mode</strong></td><td><code>sbt coverage test</code></td><td>Run tests with coverage report</td></tr>
  </table>
</div>

---

<div class="panel">
  <h2>Key Takeaways</h2>
  
  <ul style="line-height: 1.8;">
    <li>Clean MVC Architecture with clear layer separation</li>
    <li>8 Design Patterns applied in practice</li>
    <li>Modern Scala 3 with trait-based interfaces</li>
    <li>Dual Interface (TUI + GUI) via Observer pattern</li>
    <li>Full Undo/Redo via Command + Memento patterns</li>
    <li>Extensible serialization with Strategy pattern</li>
    <li>Comprehensive test suite with Scoverage</li>
    <li>Docker support for containerized deployment</li>
    <li>CI/CD pipeline with GitHub Actions</li>
  </ul>
</div>

---

<div class="panel">
  <h2>Project Resources</h2>

  <ul style="line-height: 2;">
    <li>
      <strong>GitHub Repository</strong><br>
      <a href="https://github.com/Herbert-Haase/WebReport">github.com/Herbert-Haase/WebReport</a>
    </li>
    <li>
      <strong>License</strong><br>
      MIT License — Free for educational and commercial use. 
    </li>
    <li>
      <strong>Documentation</strong><br>
      Check the <code>README.md</code> for build instructions and Scoverage reports.
    </li>
  </ul>

  <br>
  
  <div style="text-align: center; font-style: italic;" class="color-gray">
    Thank you for your attention!  Questions? 
  </div>
</div>