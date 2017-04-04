package observatory

import java.io.File
import java.nio.file.{Files, Paths}

import scala.math._

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.math.{pow}


import scala.collection.concurrent.TrieMap
import Interaction._
import observatory.Visualization.{interpolateColor, predictTemperature}

@RunWith(classOf[JUnitRunner])
class InteractionTestSpecific extends FunSuite {
  val colorScale = List(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  ).reverse

 test("interpolateColor at Location(-27.05912578437406,-180.0) for 2015") {
    /* Incorrect computed color at Location(-27.05912578437406,-180.0): Color(153,0,102).
    Expected to be closer to Color(0,0,255) than Color(255,0,0)

    Incorrect computed color at Location(-27.05912578437406,-180.0): Color(153,0,102).
     Expected to be closer to Color(0,0,255) than Color(255,0,0)

     */
   val year = 2015
    val temperatures =
      Extraction.locationYearlyAverageRecords(
        Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv"))
    val location = Location(-27.05912578437406, -180)
    val temp = predictTemperature(temperatures, location)
    val color = interpolateColor(colorScale, temp)
    println(color)
  }
}
