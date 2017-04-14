package observatory

import java.nio.file.{Files, Paths}

import Interaction.generateTiles
import Visualization2.visualizeGrid
import Extraction.{locateTemperatures, locationYearlyAverageRecords}
import Manipulation.makeGrid

object Main extends App {
  val absoluteYears = 1975 to 2015
  val imgSize = 256

  val absoluteColorScale = List(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  )

  val deviationColorScale = List(
    (7.0, Color(0, 0, 0)),
    (4.0, Color(255, 0, 0)),
    (2.0, Color(255, 255, 0)),
    (0.0, Color(255, 255, 255)),
    (-2.0, Color(0, 255, 255)),
    (-7.0, Color(0, 0, 255))
  )

  def millisToHMS(start: Long, stop: Long) = {
    val delta = stop - start
    val h = (delta / 1000) / 3600
    val m = ((delta / 1000) - (h * 3600)) / 60
    val s = ((delta / 1000) - (h * 3600) - (m * 60))
    (h, m, s)
  }

  def createDir(dir: String, year: Int, zoom: Int) = {
    val p = Paths.get(s"target/$dir/$year/$zoom")
    if (Files.notExists(p)) {
      Files.createDirectories(p)
    }
  }

  def tileGeneration(
                      dir: String,
                      colorScale: Iterable[(Double, Color)],
                      year: Int,
                      zoom: Int,
                      x: Int,
                      y: Int,
                      grid: (Int, Int) => Double
                    ) = {
    createDir(dir, year, zoom)
    val f = new java.io.File(s"target/$dir/$year/$zoom/$x-$y.png")
    val start = System.currentTimeMillis()
    visualizeGrid(grid, colorScale, zoom, x, y, imgSize).output(f)
    val stop = System.currentTimeMillis()
    val (h, m, s) = millisToHMS(start, stop)
    println(s"***\tProcessing tile $zoom,$x,$y for $dir COMPLETED. It took $h:$m:$s.")
  }

  def absoluteTileGeneration(year: Int, zoom: Int, x: Int, y: Int, grid: (Int, Int) => Double) = {
    tileGeneration("temperatures", absoluteColorScale, year, zoom, x, y, grid)
  }

  def deviationTileGenerations(year: Int, zoom: Int, x: Int, y: Int, grid: (Int, Int) => Double) = {
    tileGeneration("deviations", deviationColorScale, year, zoom, x, y, grid)
  }

  def makeYearlyGrid(year: Int) = {
    val start = System.currentTimeMillis()
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(records)
    val grid = makeGrid(temperatures)
    val stop = System.currentTimeMillis()
    val (h, m, s) = millisToHMS(start, stop)
    println(s"***\tComputing grid for year $year COMPLETED. It took $h:$m:$s.")
    (year, grid)
  }

  println("*** Computing yearly grids STARTED")
  val absoluteYearlyData = absoluteYears.map(makeYearlyGrid)
  println("*** Computing yearly grids COMPLETED")

  println("\n\n*** Computing tiles for absolute temperatures STARTED")
  generateTiles(absoluteYearlyData, absoluteTileGeneration)
  println("*** Computing tiles for absolute temperatures COMPLETED")
}
