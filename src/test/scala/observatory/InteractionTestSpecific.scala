package observatory

import java.io.File
import java.nio.file.{Files, Paths}

import scala.math._

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalactic._
import Tolerance._



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

 /*
 This test does not work, because of wrong information in
 http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Zoom_levels

 Indeed, while the width in degrees shrinks linearly, this is not true for height.

 test("tileLocation") {


    implicit val locationEq =
      new Equality[Location] {
        def areEqual(a: Location, b: Any): Boolean =
          b match {
            case p: Location => (a.lat === p.lat +- 0.1 && a.lon === p.lon +- 0.1)
            case _ => false
          }
      }

    val base = Location(-170.1022, 360.0)
    for (zoom <- 0 to 3) {
      val p = 1<<zoom
      val u = (p - 1).toInt / 2
      val SE = tileLocation(zoom, u+1, u+1)
      val NW = tileLocation(zoom, u, u)

      println(s"SE: $SE")
      println(s"NW: $NW")
      val actual = SE - NW
      val expected = base / pow(2, zoom)

      assert(actual === expected, s"zoom: $zoom, tile $u-$u")
    }
  }*/
}
