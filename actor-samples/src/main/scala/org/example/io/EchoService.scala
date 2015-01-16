package org.example.io

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}

/**
 * Created by kailianghe on 1/11/15.
 */
class EchoService(endPoint: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  IO(Tcp) ! Tcp.Bind(self, endPoint)

  def receive = {
    case Tcp.Connected(remote, _) =>
      log.debug("Remote address {} connected", remote)
      sender ! Tcp.Register(context.actorOf(EchoConnectionHandler.props(remote, sender)))
  }

}

object EchoService {
  def props(endPoint: InetSocketAddress): Props = {
    Props(new EchoService(endPoint))
  }
}
