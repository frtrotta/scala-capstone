package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import java.lang.Math.PI

import Visualization._

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {


  test("greatCircleDistance") {
    assert(greatCircleDistance(Location(0, 0), Location(0, 180)) === PI)
    assert(greatCircleDistance(Location(0, 180), Location(0, 0)) === greatCircleDistance(Location(0, 0), Location(0, 180)))
    assert(greatCircleDistance(Location(0, 0), Location(180, 0)) === PI)
    assert(greatCircleDistance(Location(0, 0), Location(0, 360)) === 0)
    assert(greatCircleDistance(Location(90, 0), Location(-90, 0)) === PI)
  }


  test("inverseDistanceWeight") {
    val temperatures = List(
      (Location(0, 180), 10.0),
      (Location(0, 0), 30.0)
    )
    assert(inverseDistanceWeight(Location(0, 90), temperatures) === 20.0)
  }

  test("predictTemperature") {
    val temperatures = List(
      (Location(0, 180), 10.0),
      (Location(0, 0), 30.0)
    )
    assert(predictTemperature(temperatures, Location(0, 180)) === 10.0)
    assert(predictTemperature(temperatures, Location(0, 0)) === 30.0)
    assert(predictTemperature(temperatures, Location(0, 90)) === 20.0)
  }

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

  test("interpolateColor") {
    assert(interpolateColor(colorScale, 70.0) === Color(255, 255, 255))
    assert(interpolateColor(colorScale, -90.0) === Color(0, 0, 0))
    assert(interpolateColor(colorScale, 12.0) === Color(255, 255, 0))
    assert(interpolateColor(colorScale, 6.0) === Color((255.0 / 2).round.toInt, 255, (255.0 / 2).round.toInt))
  }

  test("pixelIndexToLocation") {
    assert(pixelIndexToLocation(pixelPositionToPixelIndex(0, 0)) == Location(90, -180))
    assert(pixelIndexToLocation(pixelPositionToPixelIndex(359, 0)) == Location(90, 179))
    assert(pixelIndexToLocation(pixelPositionToPixelIndex(0, 179)) == Location(-89, -180))
    assert(pixelIndexToLocation(pixelPositionToPixelIndex(359, 179)) == Location(-89, 179))
  }

  test("visualize") {
    val temperatures = List(
      (Location(90.0, -180.0), -20.0),
      (Location(90.0, 179.0), -20.0),
      (Location(-89.0, -180.0), -20.0),
      (Location(-89.0, 179.0), -40.0),
      (Location(0.0, -90), 70.0),
      (Location(0.0, 90), -50.0)
    )
    val f = new java.io.File("target/some-image.png")
    visualize(temperatures, colorScale).output(f)
  }
}
