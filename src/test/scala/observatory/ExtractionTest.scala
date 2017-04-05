package observatory

import java.time.LocalDate

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {
  import Extraction._

  test("locateTemperatures") {
    import java.time.LocalDate
    val r = locateTemperatures(2015, "/stations-test.csv.txt", "/2015-test.csv.txt")
    assert(r.toList ===
      List(
        (LocalDate.of(2015, 12, 25), Location(17.3, -5.23), 0.0),
        (LocalDate.of(2015, 12, 25), Location(20.0, 20.0), 100.0)
      ))
  }

  test("locationYearlyAverageRecords") {
    val records = locateTemperatures(2015, "/stations-test.csv.txt", "/2015-test.csv.txt")
    val r = locationYearlyAverageRecords(records)

    assert(r.toList ===
      List(
        (Location(17.3, -5.23), 0.0),
        (Location(20.0, 20.0), 100.0)
      )
    )
  }

  object SingleTempRecordsOrdering extends Ordering[(LocalDate, Location, Double)] {
    def compare(a: (LocalDate, Location, Double), b: (LocalDate, Location, Double)) = {
      if (a._1.isBefore(b._1)) -1
      else if (a._1.isAfter(b._1)) 1
      else if (a._2.lat < b._2.lat) -1
      else if (a._2.lat > b._2.lat) 1
      else (b._2.lon - a._2.lon).toInt
    }
  }

  test("locateTemperatures on real data") {
    val year = 2015
    val r = locateTemperatures(year, "/stations.csv", s"/$year.csv")

    val temp = r.toList.sorted(SingleTempRecordsOrdering).take(4)
    assert(temp === List(
      (LocalDate.of(2015, 1, 1), Location(-90.0, 0.0), -8.666666666666668),
      (LocalDate.of(2015, 1, 1), Location(-89.0, 89.667), -22.11111111111111),
      (LocalDate.of(2015, 1, 1), Location(-89.0, -1.017), -22.27777777777778),
      (LocalDate.of(2015, 1, 1), Location(-84.6, -115.817), -7.388888888888889)
    )
    )

  }

  object LocatioAverageTempRecordsOrdering extends Ordering[(Location, Double)] {
    def compare(a: (Location, Double), b: (Location, Double)) = {
      if (a._2 < b._2) -1
      else if (a._2 > b._2) 1
      else if (a._1.lat < b._1.lat) -1
      else if (a._1.lat > b._1.lat) 1
      else (b._1.lon - a._1.lon).toInt
    }
  }

  test("locationYearlyAverageRecords on real data") {
    val year = 2015
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val r = locationYearlyAverageRecords(records)
    val temp = r.toList.sorted(LocatioAverageTempRecordsOrdering).take(4)
    assert(temp === List(
      (Location(40.839, 8.405), -17.959999999999997),
      (Location(-89.0, -1.017), -17.511904761904763),
      (Location(38.767, -104.3), -17.25757575757576),
      (Location(15.233, -12.167), -17.0)
    )
    )
  }
}