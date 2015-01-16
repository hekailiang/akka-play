package org.example.message

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.example.message.TeacherProtocol.QuoteRequest

/**
 * Created by kailianghe on 1/8/15.
 */
// http://rerun.me/2014/09/19/akka-notes-actor-messaging-1/
object StudentSimulatorApp extends App {

  val actorSystem = ActorSystem("UniversityMessageSystem",
    ConfigFactory.load("StudentSimulatorApp.conf").withFallback(ConfigFactory.load()))

  val teacherActorRef = actorSystem.actorOf(Props[TeacherActor], "TeacherActor")

  //send a message to the Teacher Actor
  teacherActorRef ! QuoteRequest

  //Let's wait for a couple of seconds before we shut down the system
  Thread.sleep (2000)

  //Shut down the ActorSystem.
  actorSystem.shutdown()
}
