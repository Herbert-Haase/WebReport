package de.htwg.webscraper.model.fileio.impl

import de.htwg.webscraper.model.fileio.FileIO
import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.model.data.impl1.Data
import scala.xml.{NodeSeq, PrettyPrinter, XML}
import java.io.{File, PrintWriter}

class XmlFileIO extends FileIO {
  override val mode: String = "XML"

  override def save(dataList: List[ProjectData], filePath: String): Unit = {
    val xml = <session>
      {dataList.map(dataToXml)}
    </session>

    val printer = new PrettyPrinter(120, 2)
    val pw = new PrintWriter(new File(filePath))
    pw.write(printer.format(xml))
    pw.close()
  }

  override def load(filePath: String): List[ProjectData] = {
    val file = XML.loadFile(filePath)
    (file \ "analysis").map(xmlToData).toList
  }

  private def dataToXml(data: ProjectData) = {
    <analysis source={data.source}>
      <meta>
        <chars>{data.characterCount}</chars>
        <words>{data.wordCount}</words>
        <complexity>{data.complexity}</complexity>
      </meta>
      <lists>
        <topWords>{data.mostCommonWords.map(w => <w count={w._2.toString}>{w._1}</w>)}</topWords>
        <libraries>{data.libraries.map(l => <lib>{l}</lib>)}</libraries>
        <images>{data.images.map(i => <img>{i}</img>)}</images>
        <links>{data.links.map(l => <lnk>{l}</lnk>)}</links>
      </lists>
      <originalContent>
        {data.originalLines.map(l => <line>{l}</line>)}
      </originalContent>
    </analysis>
  }

  private def xmlToData(node: scala.xml.Node): ProjectData = {
    val source = (node \ "@source").text
    val chars = (node \ "meta" \ "chars").text.toInt
    val wordCount = (node \ "meta" \ "words").text.toInt
    val complexity = (node \ "meta" \ "complexity").text.toInt
    
    val topWords = (node \ "lists" \ "topWords" \ "w").map(n => 
      (n.text, (n \ "@count").text.toInt)
    ).toList
    
    val libs = (node \ "lists" \ "libraries" \ "lib").map(_.text).toList
    val images = (node \ "lists" \ "images" \ "img").map(_.text).toList
    val links = (node \ "lists" \ "links" \ "lnk").map(_.text).toList
    
    val originalLines = (node \ "originalContent" \ "line").map(_.text).toList

    Data.restore(source, originalLines, originalLines, chars, wordCount, topWords, libs, complexity, images, links)
  }
}