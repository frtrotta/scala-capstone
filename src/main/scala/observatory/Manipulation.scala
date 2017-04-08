package observatory

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  import Visualization.{predictTemperature, pixelIndexToLocation}

  def coordinatesToPixelIndex(lat: Int, lon: Int) = {
    (90 - lat) * 360 + (lon + 180)
  }

  val gridStep = 10

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Double)]): (Int, Int) => Double = {
    val grid = new Array[Double](180 * 360)
    for (i <- (0 until grid.length).par) {
      grid(i) = predictTemperature(temperatures, pixelIndexToLocation(i))
    }

    (lat: Int, lon: Int) => {
      grid(coordinatesToPixelIndex(lat, lon))
    }
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Double)]]): (Int, Int) => Double = {

    val m = scala.collection.mutable.Map[Location, List[Double]]() // TODO Seq or List?

    // ENHANCE TrieMap

    temperaturess.foreach {
      _.foreach {
        case (loc, temperature) => {
          m get loc match {
            case Some(temperatureList) => m(loc) = temperature :: temperatureList
            case None => m += (loc -> List(temperature))
          }
        }
      }
    }

    val averages = m.mapValues(l => l.reduce(_ + _) / l.size)
    makeGrid(averages)
  }

  /**
    * @param temperatures Known temperatures
    * @param normals      A grid containing the “normal” temperatures
    * @return A sequence of grids containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Double)], normals: (Int, Int) => Double): (Int, Int) => Double = {
    val current = makeGrid(temperatures)
    (lat: Int, lon: Int) => current(lat, lon) - normals(lat, lon)
  }


}

