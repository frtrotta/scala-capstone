package observatory



import observatory.Extraction.{locateTemperatures, locationYearlyAverageRecords}
import observatory.Interaction.tile

import Util._

object ComputeAbsoluteTiles extends App {


  val dir = "temperatures"

  val imgSize = 8

  val years = 1975 to 2015
  val zooms = 0 to 3
  def positions(zoom: Int) = 0 to (1 << zoom - 1)

  val effort = years.length + zooms.foldLeft(0)((a: Int, z: Int) => (a + positions(z).length * positions(z).length))
  var accomplished = 0

  for (year <- years) {
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(records)

    for (zoom <- zooms) {
      for (x <- positions(zoom)) {
        for (y <- positions(zoom)) {
          print(s"Processing tile year=$year, zoom=$zoom, x=$x, y=$y for $dir (imgSize = $imgSize)...")

          createDir(dir, year, zoom)
          val f = new java.io.File(s"target/$dir/$year/$zoom/$x-$y.png")
          val start = System.currentTimeMillis()
          tile(temperatures, absoluteColorScale, zoom, x, y, imgSize).output(f)
          val stop = System.currentTimeMillis()
          val (h, m, s) = millisToHMS(start, stop)
          accomplished += 1
          val p = accomplished * 100.0 / effort
          printCompletedWithTimeAndPercentage(h, m, s, p)
        }
      }
    }
  }
}
