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
    // ENHANCE property based testing
    assert(greatCircleDistance(Location(0,0), Location(0,180)) === PI * r)
    assert(greatCircleDistance(Location(0,180), Location(0,0)) === greatCircleDistance(Location(0,0), Location(0,180)))
    assert(greatCircleDistance(Location(0,0), Location(180,0)) === PI * r)
    assert(greatCircleDistance(Location(0,0), Location(0,360)) === 0)
    assert(greatCircleDistance(Location(90,0), Location(-90,0)) === PI * r)
  }
  test("inverseDistanceWeight") {
    // ENHANCE property based testing

    val temperatures = List(
      (Location(0, 180), 10.0),
      (Location(0, 0), 30.0)
    )

    assert(inverseDistanceWeight(Location(0,90), temperatures) === 20)
  }
}
