package org.example.message

import akka.actor.{Props, ActorSystem}
import akka.testkit.{EventFilter, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import org.example.message.TeacherProtocol.{InitSignal, QuoteRequest}
import org.scalatest.{MustMatchers, WordSpecLike, BeforeAndAfterAll}

/**
 * Created by kailianghe on 1/9/15.
 */
class TeacherTest extends TestKit(ActorSystem("UniversityMessageSystem", ConfigFactory.parseString(
  """
    |akka {
    |  loggers = ["akka.testkit.TestEventListener"]
    |  test{
    |     filter-leeway = 7s
    |  }
    |}
  """.stripMargin)))
with WordSpecLike
with MustMatchers
with BeforeAndAfterAll {

  //1. Sends message to the Print Actor. Not even a testcase actually
  "A teacher" must {
    "print a quote when a QuoteRequest message is sent" in {

      val teacherRef = TestActorRef[TeacherActor]
      teacherRef ! QuoteRequest
    }
  }

  //2. Sends message to the Log Actor. Again, not a testcase per se
  "A teacher with ActorLogging" must {

    "log a quote when a QuoteRequest message is sent" in {

      val teacherRef = TestActorRef[TeacherActor]
      teacherRef ! QuoteRequest
    }

    //3. Asserts the internal State of the Log Actor.
    "have a quote list of size 4" in {
      val teacherRef = TestActorRef[TeacherActor]
      teacherRef.underlyingActor.quotes must have size(4)
      teacherRef.underlyingActor.quoteList must have size(4)
    }

    //4. Verifying log messages from eventStream
    "be verifiable via EventFilter in response to a QuoteRequest that is sent" in {

      val teacherRef = TestActorRef[TeacherActor]
      EventFilter.info(pattern = "QuoteResponse*", occurrences = 1) intercept {
        teacherRef ! QuoteRequest
      }
    }

    "A delayed student" must {

      "fire the QuoteRequest after 5 seconds when an InitSignal is sent to it" in {
        val teacherRef = system.actorOf(Props[TeacherActorWithReply], "teacherActorDelayed")
        val studentRef = system.actorOf(Props(new StudentDelayedActor(teacherRef)), "studentDelayedActor")

        EventFilter.info(start="Printing from Student Actor", occurrences=5).intercept{
          studentRef ! InitSignal
        }
      }

    }

  }

  override def afterAll() {
    super.afterAll()
    system.shutdown()
  }

}
