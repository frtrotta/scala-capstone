package observatory

import java.nio.file.{Files, Paths}

import observatory.Extraction.{locateTemperatures, locationYearlyAverageRecords}
import observatory.Interaction.tile

/**
  * Created by francesco on 23/04/17.
  */
object ComputeAbsoluteTiles extends App {
  val absoluteColorScale = List(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  )

  val dir = "temperatures"

  val imgSize = 8

  def millisToHMS(start: Long, stop: Long) = {
    val delta = stop - start
    val h = (delta / 1000) / 3600
    val m = ((delta / 1000) - (h * 3600)) / 60
    val s = ((delta / 1000) - (h * 3600) - (m * 60))
    (h, m, s)
  }

  def createDir(dir: String, year: Int, zoom: Int) = {
    val p = Paths.get(s"target/$dir/$year/$zoom")
    if (Files.notExists(p)) {
      Files.createDirectories(p)
    }
  }

  for (year <- 1975 to 2015) {
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(records)

    for (zoom <- 0 to 3) {
      for (x <- 0 to (1 << zoom - 1)) {
        for (y <- 0 to (1 << zoom - 1)) {
          print(s"***\tProcessing tile $zoom,$x,$y for $dir (imgeSize = $imgSize)...")
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
