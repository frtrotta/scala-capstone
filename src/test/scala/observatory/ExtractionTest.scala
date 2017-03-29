package observatory

import java.time.LocalDate

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("locateTemperatures") {
    import java.time.LocalDate
    val  r = Extraction.locateTemperatures(2015, "/stations-test.csv.txt", "/2015-test.csv.txt")
    assert( r.toList ===
      List(
        (LocalDate.of(2015, 12, 25),Location(17.3,-5.23),0.0),
        (LocalDate.of(2015, 12, 25),Location(20.0,20.0),100)
    ))
  }

  test("locationYearlyAverageRecords") {
    val records = Extraction.locateTemperatures(2015, "/stations-test.csv.txt", "/2015-test.csv.txt")
    val r = Extraction.locationYearlyAverageRecords(records)

    assert(r.toList ===
      List(
        (Location(17.3,-5.23),0.0),
        (Location(20.0,20.0),100)
      )
    )
  }
}