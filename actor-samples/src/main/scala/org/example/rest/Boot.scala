package org.example.rest

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http

/**
 * Created by kailianghe on 1/20/15.
 */
// https://gagnechris.wordpress.com/2013/09/15/building-restful-apis-with-scala-using-spray/
object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("spray-api-service", ConfigFactory.load("SprayApiDemo.conf").
    withFallback(ConfigFactory.load()))
  val log = Logging(system, getClass)

  // create and start our service actor
  val service = system.actorOf(Props[SprayApiDemoServiceActor], "spray-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)

}
