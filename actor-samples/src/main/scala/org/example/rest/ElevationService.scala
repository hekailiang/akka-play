package org.example.rest

/**
 * Created by kailianghe on 1/20/15.
 */
import akka.actor.Actor
import akka.event.Logging
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.routing.RequestContext

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ElevationService {
  case class Process(long: Double, lat: Double)
}

class ElevationService(requestContext: RequestContext) extends Actor {

  import org.example.rest.ElevationService._

  implicit val system = context.system
  import system.dispatcher
  val log = Logging(system, getClass)

  def receive = {
    case Process(long,lat) =>
      process(long,lat)
      context.stop(self)
  }

  def process(long: Double, lat: Double) = {

    log.info("Requesting elevation long: {}, lat: {}", long, lat)

    import org.example.rest.ElevationJsonProtocol._
    import spray.httpx.SprayJsonSupport._
    // build a request transformation pipeline through a function concatenation operation ~>, which return a new function, the input parameter
    // is HttpRequest and return Future of deserialized object
    val pipeline : HttpRequest => Future[GoogleElevationApiResult[Elevation]] = sendReceive ~> unmarshal[GoogleElevationApiResult[Elevation]]

    // invoke function pipeline with a HttpRequest which is to call google web api and hold a promise of future return result
    val responseFuture = pipeline {
      Get(s"http://maps.googleapis.com/maps/api/elevation/json?locations=$long,$lat&sensor=false")
    }

    // continue transform the future deserialized object asynchronizely
    responseFuture onComplete {
      case Success(GoogleElevationApiResult(_, Elevation(_, elevation) :: _)) =>
        log.info("The elevation is: {} m", elevation)
        requestContext.complete(elevation.toString)

      case Success(_) => requestContext.complete("Unrecognized result")

      case Failure(error) =>
        requestContext.complete(error)
    }
  }
}
