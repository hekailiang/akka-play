package org.example.json

import spray.json._

/**
 * Created by kailianghe on 1/23/15.
 */
// simple example grabbed from https://github.com/spray/spray-json
case class Color(name: String, red: Int, green: Int, blue: Int)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(Color)
}

object SimpleJsonApp extends App {
  import MyJsonProtocol._

  val color = Color("CadetBlue", 95, 158, 160)
  val json = color.toJson
  println(json)
  println(json.convertTo[Color])
}
