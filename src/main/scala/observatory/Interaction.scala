package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import java.lang.Math.{PI, atan, sinh}

import observatory.Visualization.{interpolateColor, predictTemperature}
import scala.math.{pow}

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
    val p = pow(2,zoom)
    val lon = x / p * 360 - 180
    val lat = atan(sinh(PI - y / p * 2*PI)) * 180/PI
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
    val SEcorner = tileLocation(zoom, y+1, y+1)
    val delta = (SEcorner - NWcorner) / imgSize

    for (i <- (0 to (imgSize*imgSize)-1).par) {
      val row = i / imgSize
      val col = i % imgSize
      val location = (Location(row, col) ** delta) + NWcorner
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

    for(zoom <- 0 to 3) {
      for(x <- 0 to (pow(2,zoom).toInt - 1)) {
        for(y <- 0 to (pow(2, zoom).toInt - 1)) {
          yearlyData.foreach{ case(year, data) => generateImage(year, zoom, x, y, data)}
        }
      }
    }

  }

}
