package observatory

import org.scalameter.api._
import org.scalameter.picklers.noPickler._

class ExtractionBenchmark extends Bench.ForkedTime {
  import Extraction._

  val year = Gen.single("year")(2015)

  val temps = (for {
    y <- year
    lt <- Gen.single("locateTemperatures")(locateTemperatures(y, "/stations.csv", s"/$y.csv"))
  } yield lt).cached


  performance of "Extraction" in {
    measure method "locateTemperatures" in {
      using(year) in {
        y =>
          locateTemperatures(y, "/stations.csv", s"/$y.csv")
      }
    }

    measure method "locationYearlyAverageRecords" in {
      using(temps) config(
        exec.independentSamples -> 1
      ) in {
        t =>
          locationYearlyAverageRecords(t)
      }
    }
  }
}
