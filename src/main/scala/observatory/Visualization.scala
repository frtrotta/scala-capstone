package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import java.lang.Math.{PI, acos, cos, pow, sin}

import scala.annotation.tailrec

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /* https://en.wikipedia.org/wiki/Great-circle_distance
    R = 6371km (does it really care?)
   */
  val r = 1

  def greatCircleDistance(a: Location, b: Location): Double = {
    def rad(x: Double): Double = {
      x * PI / 180
    }

    def fi1 = rad(a.lat)

    def lambda1 = rad(a.lon)

    def fi2 = rad(b.lat)

    def lambda2 = rad(b.lon)

    // ENHANCE http://http.developer.nvidia.com/Cg/acos.html
    r * (acos(sin(fi1) * sin(fi2) + cos(fi1) * cos(fi2) * cos(lambda1 - lambda2))) // cosine is even
  }

  // https://en.wikipedia.org/wiki/Inverse_distance_weighting
  def inverseDistanceWeight(x: Location, temperatures: Iterable[(Location, Double)]): Double = {
    def m(t: (Location, Double)): (Double, Double) = {
      val (loc, temperature) = t
      val w = 1 / pow(greatCircleDistance(x, loc), 2)
      (w * temperature, w)
    }

    val (num, den) = temperatures.par.map(m).reduce((a, b) => (a._1+b._1, a._2+b._2))
    num / den
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    temperatures.find(_._1 == location) match {
      case Some((_, t)) => t
      case None => {
        inverseDistanceWeight(location, temperatures)
      }
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {

    val colorScale = points.toList.sortBy{case (t, _) => t}

    /* When a requirement is set of the ordering of points, it happens to fail both for ascending (less fails)
        and descending (more frequently).
     */

    if (value <= colorScale.head._1) colorScale.head._2
    else {
      // ENHANCE Binary search
      @tailrec
      def interpolateColorHelper(previous: (Double, Color), rest: Iterable[(Double, Color)]): Color = {
        rest match {
          case Nil => previous._2
          case next :: rs =>
            if (value > next._1) interpolateColorHelper(next, rs)
            else {
              val weight = (value - previous._1) / (next._1 - previous._1)
              ((next._2 - previous._2) * weight) + previous._2
            }
        }
      }

      interpolateColorHelper(colorScale.head, colorScale.tail)
    }

  }

  def pixelPositionToPixelIndex(x: Int, y: Int): Int = {
    y * 360 + x
  }

  def pixelIndexToLocation(i: Int): Location = {
    def x = i % 360

    def y = i / 360

    val lat = 90.0 - y
    val lon = x - 180.0
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    val image = new Array[Pixel](360 * 180)

    for(i <- (0 until (360*180)).par) {
        val color = interpolateColor(colors, predictTemperature(temperatures, pixelIndexToLocation(i)))
        image(i) = Pixel(color.red, color.green, color.blue, 255)
      }

    Image(360, 180, image)
  }

}

