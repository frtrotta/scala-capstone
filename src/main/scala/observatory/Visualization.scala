package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import java.lang.Math.{acos, sin, cos, PI}

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
      x  * (2*PI) / 360
    }

    def fi1 = rad(a.lat)
    def lambda1 = rad(a.lon)
    def fi2 = rad(b.lat)
    def lambda2 = rad(b.lon)

    r * (acos( sin(fi1) * sin(fi2) + cos(fi1) * cos(fi2) * cos(lambda1-lambda2))) // cosine is even
  }

  // https://en.wikipedia.org/wiki/Inverse_distance_weighting
  def u(x: Location, temperatures: Iterable[(Location, Double)]): Double = {
    def r(a: (Double, Double), e: (Location, Double)): (Double, Double) = {
      val w = 1 / greatCircleDistance(x, e._1)
      (w * e._2, w)
    }
    val (num, den) = temperatures.foldLeft((0.0, 0.0))(r)
    num / den
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    temperatures.find(_._1 == location) match {
      case Some((_, t)) => t
      case None => {
        u(location, temperatures)
      }
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    ???
  }

}

