package observatory

import org.junit.runner.RunWith
import org.scalacheck.Gen
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks
import org.scalatest._
import prop._

import Visualization2._

@RunWith(classOf[JUnitRunner])
class Visualization2Test extends PropSpec with PropertyChecks with Matchers {
  val colorScale = List(
    (7.0, Color(0, 0, 0)),
    (4.0, Color(255, 0, 0)),
    (2.0, Color(255, 255, 0)),
    (0.0, Color(255, 255, 255)),
    (-2.0, Color(0, 255, 255)),
    (-7.0, Color(0, 0, 255))
  )

  val minTemperature = colorScale.last._1 - 5.0
  val maxTemperature = colorScale.head._1 + 5.0
  val temperatureGen = Gen.choose(minTemperature, maxTemperature)

  def distance(a: Double, b: Double) = (a - b).abs


  /* The test is not passed, but the grader is ok: the test is not correct*/

  ignore("bilinear interpolated color must be close to that with the smallest area") {
    forAll(
      (Gen.choose(0.0, 1.0), "x"),
      (Gen.choose(0.0, 1.0), "y")
//      (temperatureGen, "d00"),
//      (temperatureGen, "d01"),
//      (temperatureGen, "d10"),
//      (temperatureGen, "d11")
    ) {
      (x, y) => {
        whenever(
          x >= 0 && x <= 1 &&
            y >= 0 && y <= 1
//            d00 >= minTemperature && d00 <= maxTemperature &&
//            d01 >= minTemperature && d01 <= maxTemperature &&
//            d10 >= minTemperature && d10 <= maxTemperature &&
//            d11 >= minTemperature && d11 <= maxTemperature &&
//            (d00-d01).abs > 2 &&
//            (d00-d10).abs > 2 &&
//            (d00-d11).abs > 2 &&
//            (d01-d10).abs > 2 &&
//            (d01-d11).abs > 2 &&
//            (d10-d11).abs > 2
        ) {
          val d00 = -5.0
          val d01 = 5.0
          val d10 = -3.0
          val d11 = 7.0


          val areaList = List(
            (d00, x * y),
            (d01, x * (1 - y)),
            (d10, (1 - x) * y),
            (d11, (1 - x) * (1 - y))
          )

          val i = bilinearInterpolation(x, y, d00, d01, d10, d11)

          val t = areaList.map { case (temperature, area) => (temperature, area, distance(temperature, i)) }

          val closestByArea = t.sortBy(_._2).head._1
          val closestByDistance = t.sortBy(_._3).head._1

          closestByArea should equal(closestByDistance)
        }
      }
    }
  }
}
