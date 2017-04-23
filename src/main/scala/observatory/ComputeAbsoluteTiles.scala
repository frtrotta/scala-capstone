package observatory



import observatory.Extraction.{locateTemperatures, locationYearlyAverageRecords}
import observatory.Interaction.tile

import Util._

object ComputeAbsoluteTiles extends App {


  val dir = "temperatures"

  val imgSize = 8

  for (year <- 1975 to 2015) {
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(records)

    for (zoom <- 0 to 3) {
      for (x <- 0 to (1 << zoom - 1)) {
        for (y <- 0 to (1 << zoom - 1)) {
          print(s"Processing tile year=$year, zoom=$zoom, x=$x, y=$y for $dir (imgSize = $imgSize)...")

          createDir(dir, year, zoom)
          val f = new java.io.File(s"target/$dir/$year/$zoom/$x-$y.png")
          val start = System.currentTimeMillis()
          tile(temperatures, absoluteColorScale, zoom, x, y, imgSize).output(f)
          val stop = System.currentTimeMillis()
          val (h, m, s) = millisToHMS(start, stop)
          println(s" COMPLETED. It took $h:$m:$s.")
        }
      }
    }
  }
}
