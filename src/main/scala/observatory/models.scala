package observatory

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int) {
  self =>
  def -(other: Color) = {
    Color(
      self.red - other.red,
      self.green - other.green,
      self.blue - other.blue
    )
  }

  def *(value: Double) = {
    Color(
      (self.red * value).round.toInt,
      (self.green * value).round.toInt,
      (self.blue * value).round.toInt
    )
  }

  def +(other: Color) = {
    Color(
      self.red + other.red,
      self.green + other.green,
      self.blue + other.blue
    )
  }
}

