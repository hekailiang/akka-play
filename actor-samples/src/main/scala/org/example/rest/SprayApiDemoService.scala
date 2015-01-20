package org.example.rest

import akka.actor.{Props, Actor}
import spray.routing.HttpService

/**
 * Created by kailianghe on 1/20/15.
 */

class SprayApiDemoServiceActor extends Actor with SprayApiDemoService {

  def actorRefFactory = context

  def receive = runRoute(sprayApiDemoRoute)
}

trait SprayApiDemoService extends HttpService {

  val sprayApiDemoRoute = pathPrefix("api") {
    path("ElevationService" / DoubleNumber / DoubleNumber) { (long, lat) =>
      get {
        requestContext =>
          val elevationService = actorRefFactory.actorOf(Props(new ElevationService(requestContext)))
          elevationService ! ElevationService.Process(long, lat)
      }
    } ~ path("TimezoneService" / DoubleNumber / DoubleNumber / Segment) { (long, lat, timestamp) =>
      get {
        requestContext =>
          val timezoneService = actorRefFactory.actorOf(Props(new TimezoneService(requestContext)))
          timezoneService ! TimezoneService.Process(long, lat, timestamp)
      }
    }
  }

}
