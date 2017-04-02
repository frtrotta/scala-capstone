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
      x * (2 * PI) / 360
    }

    def fi1 = rad(a.lat)

    def lambda1 = rad(a.lon)

    def fi2 = rad(b.lat)

    def lambda2 = rad(b.lon)

    r * (acos(sin(fi1) * sin(fi2) + cos(fi1) * cos(fi2) * cos(lambda1 - lambda2))) // cosine is even
  }

  // https://en.wikipedia.org/wiki/Inverse_distance_weighting
  def inverseDistanceWeight(x: Location, temperatures: Iterable[(Location, Double)]): Double = {
    /* ENHANCE Would ParIterable produce any benefit?
    * The signature of predictTemperature cannot be changed, therefore each call
    * would require the conversion of a Iterable to a ParIterable.
    * */
    def reduce(a: (Double, Double), e: (Location, Double)): (Double, Double) = {
      val w = 1 / pow(greatCircleDistance(x, e._1), 2)
      (a._1 + w * e._2, a._2 + w)
    }

    val (num, den) = temperatures.foldLeft((0.0, 0.0))(reduce)
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

    require(points.head._1 < points.tail.head._1) // ascending ordering

    if (value <= points.head._1) points.head._2
    else {
      // ENHANCE Binary search
      @tailrec
      def interpolateColorHelper(previous: (Double, Color), rest: Iterable[(Double, Color)]): Color = {
        rest match {
          case Nil => previous._2
          case r :: rs =>
            if (value > r._1) interpolateColorHelper(r, rs)
            else {
              val deltaV = (value - previous._1) / (r._1 - previous._1)
              ((r._2 - previous._2) * deltaV) + previous._2
            }
        }
      }

      interpolateColorHelper(points.head, points.tail)
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
    var i = 0

    // ENHANCE Data parallel
    for (x <- 0 to 359) {
      for (y <- 0 to 179) {
        i = pixelPositionToPixelIndex(x, y)

        def color = interpolateColor(colors, predictTemperature(temperatures, pixelIndexToLocation(i)))

        image(i) = Pixel(color.red, color.green, color.blue, 255)
      }
    }

    Image(360, 180, image)
  }

}

