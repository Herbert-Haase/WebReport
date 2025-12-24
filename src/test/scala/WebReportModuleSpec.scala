package de.htwg.webreport

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.Guice
import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.model.fileio.FileIOTrait

class WebScraperModuleSpec extends AnyWordSpec with Matchers {
  "The WebScraperModule" should {
    "configure the injector correctly" in {
      val injector = Guice.createInjector(new WebScraperModule)
      
      val sessionManager = injector.getInstance(classOf[SessionManagerTrait])
      sessionManager should not be null
      
      val fileIO = injector.getInstance(classOf[FileIOTrait])
      fileIO should not be null
    }
  }
}