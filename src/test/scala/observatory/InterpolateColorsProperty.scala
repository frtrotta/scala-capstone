package observatory

import observatory.Visualization.interpolateColor
import org.junit.runner.RunWith
import org.scalatest._
import prop._
import org.scalacheck.Gen
import org.scalatest.junit.JUnitRunner

import scala.math.{pow, _}

@RunWith(classOf[JUnitRunner])
class InterpolateColorsProperty extends PropSpec with PropertyChecks with Matchers {

  val colors = List(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  )

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
        val color = interpolateColor(colors, temperature)
        val (closestTempByTemp, closestColorByTemp) = colors.sortBy { case (t, _) => temperatureDistance(temperature, t) }.head
        val (closestTempByColor, closestColorByColor) = colors.sortBy { case (_, c) => colorDistance(color, c) }.head
        closestTempByColor should equal (closestTempByTemp)
      }
    }
  }

  property("interpolated color for temperatures in the reference should equal the reference color") {
    forAll ((Gen.oneOf(colors), "color reference")) {
      case (temperature, refColor) => {
        val color = interpolateColor(colors, temperature)
        color should equal (refColor)
      }
    }
  }
}
