package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("Extraction") {
    Extraction.locateTemperatures(2015, "/stations.csv", "/2015.csv").foreach(println)
  }
}