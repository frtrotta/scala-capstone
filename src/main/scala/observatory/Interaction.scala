package observatory

import com.sksamuel.scrimage.{Image, Pixel}

import observatory.Visualization.{interpolateColor, predictTemperature}
import scala.math._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val p = (1<<zoom)
    val lon = x.toDouble / p * 360.0 - 180.0
    val lat = atan(sinh(Pi * (1 - 2 * y.toDouble / p))) * 180.0/Pi
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int, imgSize: Int = 256): Image = {
    val image = new Array[Pixel](imgSize*imgSize)
    val NWcorner = tileLocation(zoom, x, y)
    val SEcorner = tileLocation(zoom, x+1, y+1)
    val f = (SEcorner - NWcorner) / imgSize
    val p = 1<<zoom

    for (i <- (0 until imgSize*imgSize).par) {
      val yPixel = i / imgSize
      val xPixel = i % imgSize

      val lon = (xPixel.toDouble / imgSize + x) * 360 / p - 180.0
      val lat = atan(sinh(Pi * (1 - 2 * (yPixel.toDouble / imgSize + y) / p))) * 180.0/Pi

      val location = Location(lat, lon)

      val color = interpolateColor(colors, predictTemperature(temperatures, location))
      image(i) = Pixel(color.red, color.green, color.blue, 127)
    }

    Image(imgSize, imgSize, image)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {

    // ENHANCE parallel execution
    for(zoom <- 0 to 3) {
      for(x <- 0 to ((1<<zoom) - 1)) {
        for(y <- 0 to ((1<<zoom) - 1)) {
          yearlyData.foreach{ case(year, data) => generateImage(year, zoom, x, y, data)}
        }
      }
    }

  }

}
