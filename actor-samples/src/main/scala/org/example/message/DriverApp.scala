package org.example.message

/**
 * Created by kailianghe on 1/9/15.
 */
import akka.actor.ActorSystem
import akka.actor.Props
import org.example.message.TeacherProtocol.InitSignal

object DriverApp extends App {

  //Initialize the ActorSystem
  val system = ActorSystem("UniversityMessageSystem")

  //create the teacher actor
  val teacherRef = system.actorOf(Props[TeacherActorWithReply], "teacherActor")

  //create the Student Actor - pass the teacher actorref as a constructor parameter to StudentActor
  val studentRef = system.actorOf(Props(new StudentDelayedActor(teacherRef)), "studentActor")

  //send a message to the Student Actor
  studentRef ! InitSignal

  //Let's wait for a couple of seconds before we shut down the system
  Thread.sleep(5001)

  //Shut down the ActorSystem.
  system.shutdown()
}