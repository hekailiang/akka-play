package org.example.io

import java.net.InetSocketAddress

import akka.actor.ActorSystem

import scala.io.StdIn

/**
 * Created by kailianghe on 1/11/15.
 */
object EchoServiceApp extends App {
  val system = ActorSystem("echo-service-system")
  val endpoint = new InetSocketAddress("localhost", 9977)
  system.actorOf(EchoService.props(endpoint))

  StdIn.readLine(f"Hit ENTER to exit ...%n")
  system.shutdown()
  system.awaitTermination()
}
