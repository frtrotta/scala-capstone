package observatory

import java.time.LocalDate
import scala.io.Source

/**
  * 1st milestone: data extraction
  */
object Extraction {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {

    def stationIdfromParts(parts: Array[String]): String = {
      val l = parts.length
      if (l >= 2)
        parts(0) + "," + parts(1)
      else if (l == 1)
        parts(0)
      else
        throw new Exception
    }

    def locationFromParts(parts: Array[String]): Option[Location] = {
      if (parts.length == 4)
        Option(Location(parts(2).toDouble, parts(3).toDouble))
      else
        None
    }


    def stationDatafromLine(line: String): (String, Option[Location]) = {

      val parts = line split ","

      assert(parts.length == 1 || parts.length == 2 || parts.length == 4, s"$line")

      val id = stationIdfromParts(parts)
      val loc = locationFromParts(parts)

      (id, loc)
    }

    val stations = (
      for {
        (id, Some(location)) <- Source.fromFile(getClass.getResource(stationsFile).getFile).getLines().map(stationDatafromLine(_))
      } yield (id, location)
      ).toMap

    def temperatureDatafromLine(line: String): (String, LocalDate, Option[Double]) = {
      val parts = line split ","
      val l = parts.length
      if (l == 4 || l == 5) {
        val month = parts(l - 3).toInt
        val day = parts(l - 2).toInt
        val temp = if (parts(l - 1) == "9999.9") None else Option(parts(l - 1).toDouble)
        (stationIdfromParts(parts), LocalDate.of(year, month, day), temp)
      }
      else
        throw new Exception
    }

    val temperatures = for {
      (id, date, Some(temperature)) <- Source.fromFile(getClass.getResource(temperaturesFile).getFile).getLines().map(temperatureDatafromLine(_))
    } yield (id, date, temperature)

    temperatures.map{case (id, date, temperature) => (date, stations(id), temperature)}.toIterable // TODO
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    ???
  }

}
