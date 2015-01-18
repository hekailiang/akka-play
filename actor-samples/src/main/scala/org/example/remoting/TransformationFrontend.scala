package org.example.remoting

import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.example.remoting.RemotingProtocol._
import scala.concurrent.duration._

/**
 * Created by kailianghe on 1/18/15.
 */
// http://tersesystems.com/2014/06/25/akka-clustering/
/**
 * Akka remoting works by saying to the actor system either “I want you to create an actor on this remote host”:
 * val ref = system.actorOf(FooActor.props.withDeploy(Deploy(scope = RemoteScope(address))))
 *
 * or “I want a reference to an existing actor on the remote host”:
 * val remoteFooActor = context.actorSelection("akka.tcp://actorSystemName@10.0.0.1:2552/user/fooActor")
 *
 * After calling the actor, messages are sent to the remote server using Protocol Buffers for serialization, and
 * reconstituted on the other end.
 *
 * Clustering allows you to create an actor somewhere on a cluster consisting of nodes which all share the same
 * actor system, without knowing exactly which node it is on. Other machines can join and leave the cluster at run time.
 */
object TransformationFrontend {

  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load("AkkaRemoting.conf")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    val frontend = system.actorOf(Props[TransformationFrontend], name = "frontend")

    val counter = new AtomicInteger
    import system.dispatcher
    import akka.pattern.ask
    system.scheduler.schedule(2 seconds, 4 seconds) {
      implicit val timeout = Timeout(5 seconds)
      (frontend ? TransformationJob("hello-" + counter.incrementAndGet())) onSuccess {
        case result => println(result)
      }
    }
  }
}

class TransformationFrontend extends Actor {
  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  def receive = {
    case job: TransformationJob if backends.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later", job)

    case job: TransformationJob =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward job

    case BackendRegistration if !backends.contains(sender()) =>
      context watch sender()
      backends = backends :+ sender()

    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }

}