package observatory

import observatory.Visualization.interpolateColor
import org.junit.runner.RunWith
import org.scalatest._
import prop._
import org.scalacheck.Gen
import org.scalatest.junit.JUnitRunner

import scala.math.{pow, _}

@RunWith(classOf[JUnitRunner])
class InterpolateColorsTest extends PropSpec with PropertyChecks with Matchers {

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

  def temperatureDistance(a: Double, b: Double): Double = {
    (a - b).abs
  }

  def colorDistance(a: Color, b: Color): Double = {
    val delta = a - b
    sqrt(pow(delta.red, 2) + pow(delta.green, 2) + pow(delta.blue, 2))
  }

  property("interpolated color should be close to that of the closest temperature") {
    forAll((Gen.choose(-100.0, 100.0), "temperature")) {
      (temperature: Double) => {
        val color = interpolateColor(colorScale, temperature)
        val (closestTempByTemp, closestColorByTemp) = colorScale.sortBy { case (t, _) => temperatureDistance(temperature, t) }.head
        val (closestTempByColor, closestColorByColor) = colorScale.sortBy { case (_, c) => colorDistance(color, c) }.head
        closestTempByColor should equal (closestTempByTemp)
      }
    }
  }

/*
[info] - interpolated color should be close to that of the closest temperature *** FAILED ***
[info]   TestFailedException was thrown during property evaluation.
[info]     Message: -50.0 did not equal -27.0
[info]     Location: (InterpolateColorsProperty.scala:41)
[info]     Occurred when passed generated values (
[info]       temperature = -38.45449941243753
[info]     )
*/


  property("interpolated color for temperatures in the reference should equal the reference color") {
    forAll ((Gen.oneOf(colorScale), "color reference")) {
      case (temperature, refColor) => {
        val color = interpolateColor(colorScale, temperature)
        color should equal (refColor)
      }
    }
  }
}
