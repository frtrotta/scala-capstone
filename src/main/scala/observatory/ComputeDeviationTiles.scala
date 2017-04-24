package observatory

import observatory.Extraction.{locateTemperatures, locationYearlyAverageRecords}
import Manipulation.{average, makeGrid, deviation}
import Visualization2.visualizeGrid
import observatory.Util._

/**
  * Created by francesco on 23/04/17.
  */
object ComputeDeviationTiles extends App {
  val gridResolution = 10
  val dir = "deviations"
  val imgSize = 8

  def computeTemperaturess() = {
    print("Computing yearly average records for years 1975 to 1995... ")
    val start = System.currentTimeMillis()
    val temperaturess = (1975 to 1995).map(
      year => {
        val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
        locationYearlyAverageRecords(records)
      }
    )
    val stop = System.currentTimeMillis()
    val (h, m, s) = millisToHMS(start, stop)
    println(s" COMPLETED. It took $h:$m:$s.")
    temperaturess
  }

  def computeTemperature(year: Int) = {
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    locationYearlyAverageRecords(records)
  }

  def computeAverage() = {
    val temperaturess = computeTemperaturess()

    print("Computing average grid for the same years... ")
    val start = System.currentTimeMillis()
    val avg = average(temperaturess, gridResolution)
    val stop = System.currentTimeMillis()
    val (h, m, s) = millisToHMS(start, stop)
    println(s" COMPLETED. It took $h:$m:$s.")
    avg
  }

  def computeGrid(year: Int, gridResolution: Int) = {
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(records)
    makeGrid(temperatures, gridResolution)
  }

  val normals = computeAverage()

  val years = 1996 to 2015
  val zooms = 0 to 3
  def positions(zoom: Int) = 0 to (1 << zoom - 1)

  val effort = years.length + zooms.foldLeft(0)((a: Int, z: Int) => (a + positions(z).length * positions(z).length))
  var accomplished = 0

  for (year <- years) {
    val temperatures = computeTemperature(year)

    for (zoom <- zooms) {
      for (x <- positions(zoom)) {
        for (y <- positions(zoom)) {
          print(s"Processing tile year=$year, zoom=$zoom, x=$x, y=$y for $dir (imgSize = $imgSize)...")
          createDir(dir, year, zoom)
          val f = new java.io.File(s"target/$dir/$year/$zoom/$x-$y.png")
          val start = System.currentTimeMillis()
          val devGrid = deviation(temperatures, normals)
          visualizeGrid(devGrid, deviationColorScale, zoom, x, y, imgSize).output(f)
          val stop = System.currentTimeMillis()
          val (h, m, s) = millisToHMS(start, stop)
          accomplished += 1
          val p = accomplished * 100.0 / effort
          println(f" COMPLETED. It took $h:$m:$s. $p%.2f%% of total work done.")
        }
      }
    }
  }

}
