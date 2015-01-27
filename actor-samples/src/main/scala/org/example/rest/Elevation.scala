package org.example.rest

import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
 * Created by kailianghe on 1/20/15.
 */
case class Elevation(location: Location, elevation: Double)
case class Location(lat: Double, lng: Double)
case class GoogleElevationApiResult[T](status: String, results: List[T])

object ElevationJsonProtocol extends DefaultJsonProtocol {
  implicit val locationFormat = jsonFormat2(Location)  // also can use def here
  implicit val elevationFormat = jsonFormat2(Elevation)

  implicit def googleElevationApiResultFormat[T : JsonFormat] = jsonFormat2(GoogleElevationApiResult[T])
}
