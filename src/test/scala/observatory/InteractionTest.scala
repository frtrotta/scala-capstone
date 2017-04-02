package observatory


import java.io.File
import java.nio.file.{Files, Paths}

import scala.math._

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap
import Interaction._
import observatory.Visualization.{interpolateColor, predictTemperature}

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  val timing = new StringBuffer

  def timed[T](label: String, code: => T): T = {
    val start = System.currentTimeMillis()
    val result = code
    val stop = System.currentTimeMillis()
    timing.append(s"Processing $label took ${stop - start} ms.\n")
    result
  }

  val colors = List(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  ).reverse

  test("interpolateColor") {

    val year = 2015
    var start = System.currentTimeMillis()
    val temperatures = Extraction.locationYearlyAverageRecords(Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv"))
    var stop = System.currentTimeMillis()
    println(s"***\tProcessing locationYearlyAverageRecords took ${stop - start} ms.")

    val location = Location(10.0, 0.0)
    if (temperatures.exists { case (l, _) => l == location }) assert(false, "Choose a location that is not contained")

    start = System.currentTimeMillis()
    val temp = predictTemperature(temperatures, location)
    stop = System.currentTimeMillis()
    println(s"***\tProcessing predictTemperature took ${stop - start} ms.")

    start = System.currentTimeMillis()
    interpolateColor(colors, temp)
    stop = System.currentTimeMillis()
    println(s"***\tProcessing interpolateColor took ${stop - start} ms.")
  }

  test("tile") {
    val year = 2015
    val temperatures = Extraction.locationYearlyAverageRecords(Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv"))

    generateTiles(2015 to 2015, 0 to 3, temperatures, 8)
  }

  private def generateTiles(yearList: Seq[Int], zoomList: Seq[Int], temperatures: Iterable[(Location, Double)], imgSize: Int) = {
    for (year <- yearList) {
      for (zoom <- zoomList) {
        for (x <- 0 to (pow(2, zoom).toInt - 1)) {
          for (y <- 0 to (pow(2, zoom).toInt - 1)) {
            val p = Paths.get(s"target/temperatures/$year/$zoom")
            if (Files.notExists(p)) {
              Files.createDirectories(p)
            }
            val start = System.currentTimeMillis()
            tile(temperatures, colors, zoom, x, y, imgSize).output(new java.io.File(s"target/temperatures/$year/$zoom/$x-$y.png"))
            val stop = System.currentTimeMillis()
            val delta = stop - start
            val h = (delta / 1000) / 3600
            val m = ((delta / 1000) - (h * 3600)) / 60
            val s = ((delta / 1000) - (h * 3660) - (m * 60))
            println(s"***\tProcessing tile $zoom,$x,$y took $delta ms ($h:$m:$s).")
          }
        }
      }
    }
  }
}
