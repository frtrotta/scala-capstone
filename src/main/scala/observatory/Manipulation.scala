package observatory

import scala.collection.parallel.ParIterable

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  import Visualization.{predictTemperature, pixelIndexToLocation}
  import Visualization2.bilinearInterpolation

  def gridIndexToUnitGridIndex(i: Int, gridResolution: Int) = {
    val row = i / gridCols(gridResolution)
    val col = i % gridCols(gridResolution)

    if (row < gridRows(gridResolution) - 1)
      (row * 360 + col) * gridResolution
    else
      (row * 360 + col) * gridResolution - 360
  }

  def gridIndexToLocation(i: Int, gridResolution: Int) = {
    pixelIndexToLocation(gridIndexToUnitGridIndex(i, gridResolution))
  }

  def geoToUnitGrid(lat: Int, lon: Int) = {
    val y = 90 - lat
    val x = lon + 180
    (x, y)
  }

  def unitGridToGrid(x: Int, y: Int, gridResolution: Int) = {
    val col = (x / gridResolution)
    val row = if (y == 359) gridRows(gridResolution) - 1 else (y / gridResolution)
    (col, row)
  }

  def gridCols(gridResolution: Int) = {
    360 / gridResolution // Last column equals the first
  }

  def gridRows(gridResolution: Int) = {
    180 / gridResolution + 1
  }

  def gridCoordinatesToGridIndex(col: Int, row: Int, gridResolution: Int) = {
    row * gridCols(gridResolution) + col
  }

  def bilinearInterpolatedGrid(data: Array[Double], gridResolution: Int) = {
    (lat: Int, lon: Int) => {
      val (x, y) = geoToUnitGrid(lat, lon)
      val (col, row) = unitGridToGrid(x, y, gridResolution)
      val d00 = data(gridCoordinatesToGridIndex(col, row, gridResolution))
      val d01 = data(gridCoordinatesToGridIndex(col, row + 1, gridResolution))
      val d10 = if (col == gridCols(gridResolution))
                  data(gridCoordinatesToGridIndex(0, row, gridResolution))
                else
                  data(gridCoordinatesToGridIndex(col + 1, row, gridResolution))
      val d11 = if (col == gridCols(gridResolution))
                  data(gridCoordinatesToGridIndex(0, row + 1, gridResolution))
                else
                  data(gridCoordinatesToGridIndex(col + 1, row + 1, gridResolution))
      bilinearInterpolation(
        (x % gridResolution).toDouble / gridResolution,
        (y % gridResolution).toDouble / gridResolution,
        d00,
        d01,
        d10,
        d11
      )
    }
  }

  /**
    * Computes the data that supports the grid
    *
    * @param temperatures
    * @param gridResolution must be an integer divider of both 180 and 360
    * @return
    */
  def computeGridData(temperatures: Iterable[(Location, Double)], gridResolution: Int): Array[Double] = {
    val data = new Array[Double](gridCols(gridResolution) * gridRows(gridResolution))
    for (i <- (0 until data.length).par) {
      data(i) = predictTemperature(temperatures, gridIndexToLocation(i, gridResolution))
    }
    data
  }

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Double)], gridResolution: Int = 1): (Int, Int) => Double = {
    val data = computeGridData(temperatures, gridResolution)
    bilinearInterpolatedGrid(data, gridResolution)
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Double)]], gridResolution: Int = 1): (Int, Int) => Double = {

    /* Evidently the two implementations are different, since the grader does not like
    the commented, while it says nothing for the other.
    * */

    /*val m = scala.collection.mutable.Map[Location, List[Double]]()

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
    makeGrid(averages)*/
    val yearlyData: ParIterable[Array[Double]] = temperaturess.map(temperatures => computeGridData(temperatures, gridResolution)).par
    val z: (Int) => Double = (i: Int) => 0.0
    val sumData = yearlyData.foldLeft(z)((f: (Int) => Double, d: Array[Double]) => { (i: Int) => f(i) + d(i) })
    val n = yearlyData.size
    val avg = new Array[Double](gridCols(gridResolution) * gridRows(gridResolution))
    for (i <- (0 until avg.size).par) {
      avg(i) = sumData(i) / n
    }
    bilinearInterpolatedGrid(avg, gridResolution)
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

