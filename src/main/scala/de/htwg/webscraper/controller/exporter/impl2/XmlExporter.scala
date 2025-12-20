package de.htwg.webscraper.controller.exporter.impl2

import de.htwg.webscraper.controller.exporter.Exporter
import de.htwg.webscraper.model.data.ProjectData
import java.io.{File, BufferedWriter, FileWriter}
import scala.util.Try

class XmlExporter extends Exporter {
  override def exportData(data: ProjectData, filePath: String): Try[String] = Try {
    val libsXml = data.libraries.map(lib => s"    <lib>$lib</lib>").mkString("\n")
    val wordsXml = data.mostCommonWords.map { case (w, c) => s"    <word count=\"$c\">$w</word>" }.mkString("\n")
    
    val imagesXml = data.images.map(img => s"    <image>$img</image>").mkString("\n")
    val linksXml = data.links.map(lnk => s"    <link>$lnk</link>").mkString("\n")

    val xmlContent = s"""<analysis>
  <meta>
    <chars>${data.characterCount}</chars>
    <lines>${data.lineCount}</lines>
    <totalWords>${data.wordCount}</totalWords>
    <complexity>${data.complexity}</complexity>
    <images count="${data.imageCount}">
$imagesXml
    </images>
    <links count="${data.linkCount}">
$linksXml
    </links>
  </meta>
  <anatomy>
    <libraries>
$libsXml
    </libraries>
  </anatomy>
  <content>
    <topWords>
$wordsXml
    </topWords>
  </content>
</analysis>"""

    val file = new File(filePath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(xmlContent)
    bw.close()
    s"Successfully exported XML to $filePath"
  }
}