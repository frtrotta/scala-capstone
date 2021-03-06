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
    val normalYears = 1975 to 1995
    val temperaturess = new Array[Iterable[(Location, Double)]](normalYears.length)
    for (year <- normalYears) {
      val i = year - normalYears.head
      val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
      temperaturess(i) = locationYearlyAverageRecords(records)
      print(s"$year ")
    }
    val stop = System.currentTimeMillis()
    printCompletedWithTime(start, stop)
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
    printCompletedWithTime(start, stop)
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

  val totalEffort = years.length * zooms.foldLeft(0)((a: Int, z: Int) => (a + positions(z).length * positions(z).length))
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
          accomplished += 1
          printCompletedWithTimeAndPercentage(start, stop, accomplished, totalEffort)
        }
      }
    }
  }

}
