package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest._
import prop._
import org.scalacheck.Gen
import scala.math.{pow, sqrt}

@RunWith(classOf[JUnitRunner])
class ManipulationSpecification extends PropSpec with PropertyChecks with Matchers {

  import Manipulation._

  property("Coordinates (lat, lon) converted to index must be inbound") {
    forAll(
      (Gen.choose(-89, 90), "Latitude"),
      (Gen.choose(-180, 179), "Longitude")
    ) {
      (lat: Int, lon: Int) =>
        whenever (lat >= -89 && lat <= 90 && lon >= -180 && lon <= 179)
      {
        val i = coordinatesToPixelIndex(lat, lon)
        i should be >= 0
        i should be < 360 * 180
      }
    }
  }

  property("pixelIndexToLocation must be the inverse of coordinatesToPixelIndex") {
    forAll(
      (Gen.choose(-89, 90), "Latitude"),
      (Gen.choose(-180, 179), "Longitude")
    ) {
      (lat: Int, lon: Int) =>
        whenever (lat >= -89 && lat <= 90 && lon >= -180 && lon <= 179)
        {
          val l = Visualization.pixelIndexToLocation(coordinatesToPixelIndex(lat, lon))
          l.lat should equal (lat)
          l.lon should equal (lon)
        }
    }
  }

  val gridGen =
    for {
      y <- Gen.const(2015)
    } yield {
      val temperatures =
        Extraction.locationYearlyAverageRecords(
        Extraction.locateTemperatures(
          y,
          "/stations-test.csv.txt",
          s"/$y-test.csv.txt")
      )
      (temperatures, makeGrid(temperatures))
    }

  def distance(a: Location, b: Location): Double = {
    val delta = a-b
    sqrt(pow(delta.lat, 2) + pow(delta.lon, 2))
  }

  def distance(a: Double, b: Double): Double = {
    (a-b).abs
  }

  property("Temperature of a grid point must be the closest to the temperature of the closest reference point") {
    forAll(
      (Gen.choose(-89, 90), "Latitude"),
      (Gen.choose(-180, 179), "Longitude"),
      (gridGen, "Grid")
    ) {
      (lat: Int, lon: Int, t: (Iterable[(Location, Double)], (Int, Int) => Double)) =>
        whenever (lat >= -89 && lat <= 90 && lon >= -180 && lon <= 179) {
        val (temperatures, grid) = t

        val temperature = grid(lat, lon)

        val closestLocationByLocation =
          temperatures
          .map{case (l, _) => (l, distance(l, Location(lat, lon)))}
        .toList
        .sortBy(_._2)
        .head
        ._1

        val closestLocationByTemperature =
          temperatures
            .map{case (l, t) => (l, distance(t, temperature))}
        .toList
        .sortBy(_._2)
        .head
        ._1

        closestLocationByLocation should equal (closestLocationByTemperature)
      }
    }
  }

}