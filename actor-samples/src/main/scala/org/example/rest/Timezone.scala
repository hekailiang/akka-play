package org.example.rest

/**
 * Created by kailianghe on 1/20/15.
 */
import spray.json.{ JsonFormat, DefaultJsonProtocol }

case class Timezone(status: String, timeZoneId: String, timeZoneName: String)
case class GoogleTimezoneApiResult[T](status: String, timeZoneId: String, timeZoneName: String)

object TimezoneJsonProtocol extends DefaultJsonProtocol {
  implicit val timezoneFormat = jsonFormat3(Timezone)
  implicit def googleTimezoneApiResultFormat[T :JsonFormat] = jsonFormat3(GoogleTimezoneApiResult.apply[T])
}
