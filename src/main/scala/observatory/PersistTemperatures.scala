package observatory

import java.io.{FileOutputStream, ObjectOutputStream}
import java.nio.file.{Files, Paths}

import observatory.Extraction.{locateTemperatures, locationYearlyAverageRecords}

/**
  * Created by francesco on 23/04/17.
  */
object PersistTemperatures extends App {
  val dir = "target/temperatures"
  val p = Paths.get(dir)
  if (Files.notExists(p)) {
    Files.createDirectories(p)
  }

  for (year <- 1975 to 2015) {
    val fname = s"$dir/$year.dat"
    print(s"Persisting $year temperatures to file $fname ...")
    val records = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(records).toList
    val fos = new FileOutputStream(fname)
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(temperatures)
    oos.close()
    println(" Done")
  }
}
