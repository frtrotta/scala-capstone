package observatory

import java.nio.file.{Files, Paths}

object Util {

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
}