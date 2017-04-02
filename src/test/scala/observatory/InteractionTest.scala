package observatory

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
    println(s"Processing locationYearlyAverageRecords took ${stop - start} ms.\n")

    val location = Location(10.0, 0.0)
    if (temperatures.exists{case(l, _) => l == location}) assert(false, "Choose a location that is not contained")

    start = System.currentTimeMillis()
    val temp = predictTemperature(temperatures, location)
    stop = System.currentTimeMillis()
    println(s"Processing predictTemperature took ${stop - start} ms.\n")

    start = System.currentTimeMillis()
    interpolateColor(colors, temp)
    stop = System.currentTimeMillis()
    println(s"Processing interpolateColor took ${stop - start} ms.\n")
  }

  /*test("tile") {

    interpolateColor(colors, predictTemperature(temperatures, location))


    val year = 2015
    var start = System.currentTimeMillis()
    val temperatures = Extraction.locationYearlyAverageRecords(Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv"))
    var stop = System.currentTimeMillis()
    println(s"Processing locationYearlyAverageRecords took ${stop - start} ms.\n")

    val zoom = 0
    val x = 0
    val y = 0
    start = System.currentTimeMillis()
    tile(temperatures, colors, zoom, x, y).output(new java.io.File(s"target/temperatures/$year/$x-$y.png"))
    stop = System.currentTimeMillis()
    println(s"Processing tile took ${stop - start} ms.\n")
  }*/
}
