package observatory

import org.scalameter.api._

class ExtractionBenchmark extends Bench.LocalTime {
  import Extraction._

  performance of "Extraction" in {
    measure method "pippo" in {
      val year = 2015
      locationYearlyAverageRecords(Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv"))
    }
  }
}
