package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Interaction.tileLocation
import observatory.Visualization.{interpolateColor}

import scala.math.{Pi, atan, sinh}

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  /**
    * @param x X coordinate between 0 and 1
    * @param y Y coordinate between 0 and 1
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double
  ): Double = {
    d11*x*y+d01*(1-x)*y+d10*x*(1-y)+d00*(1-x)*(1-y)
  }

  def predictTemperature(grid: (Int, Int) => Double, loc: Location) = {

    val lat = loc.lat
    val lon = loc.lon

    val d00 = grid(lat.ceil.toInt, lon.floor.toInt)
    val d01 = grid(lat.floor.toInt, lon.floor.toInt)
    val d10 = grid(lat.ceil.toInt, lon.ceil.toInt)
    val d11 = grid(lat.floor.toInt, lon.ceil.toInt)

    bilinearInterpolation(lon - lon.floor, lat.ceil - lat, d00, d01, d10, d11)
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param zoom Zoom level of the tile to visualize
    * @param x X value of the tile to visualize
    * @param y Y value of the tile to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int,
    imgSize: Int = 256
  ): Image = {
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

      val loc = Location(lat, lon)

      val temperature = predictTemperature(grid, loc)

      val color = interpolateColor(colors, temperature)
      image(i) = Pixel(color.red, color.green, color.blue, 127)
    }

    Image(imgSize, imgSize, image)
  }
}
