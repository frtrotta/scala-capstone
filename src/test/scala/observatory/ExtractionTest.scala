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
        (LocalDate.of(2015, 12, 25),Location(20.0,20.0),100.0)
    ))
  }

  test("locationYearlyAverageRecords") {
    val records = Extraction.locateTemperatures(2015, "/stations-test.csv.txt", "/2015-test.csv.txt")
    val r = Extraction.locationYearlyAverageRecords(records)

    assert(r.toList ===
      List(
        (Location(17.3,-5.23),0.0),
        (Location(20.0,20.0),100.0)
      )
    )
  }

  test("locateTemperatures on real data") {
    val start = System.currentTimeMillis()
    val  r = Extraction.locateTemperatures(2015, "/stations.csv", "/2015.csv")
    val stop = System.currentTimeMillis()
    println(s"Processing locateTemperatures on real data took ${stop - start} ms.\n")
  }

  test("locationYearlyAverageRecords on real data") {
    val start = System.currentTimeMillis()
    val records = Extraction.locateTemperatures(2015, "/stations.csv", "/2015.csv")
    val r = Extraction.locationYearlyAverageRecords(records)
    val stop = System.currentTimeMillis()
    println(s"Processing locationYearlyAverageRecords on real data took ${stop - start} ms.\n")
  }
}