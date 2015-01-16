package org.example.backpressure

import akka.actor.{DeadLetter, Props, ActorSystem}
import com.typesafe.config.ConfigFactory

/**
 * Created by kailianghe on 1/12/15.
 */
object BackPressureApp extends App {
  case object Ping

  val sys = ActorSystem("testSys", ConfigFactory.load("BackPressureApp.conf").withFallback(ConfigFactory.load()))
  val slow = sys.actorOf(Props[SlowReceiver].withMailbox("bounded-mailbox"), "slow")
  val fast = sys.actorOf(Props(classOf[FastSender], slow), "fast")
  val watcher = sys.actorOf(Props(classOf[Watcher], slow), "watcher")
  sys.eventStream.subscribe(watcher, classOf[DeadLetter])

  fast ! Ping
}
