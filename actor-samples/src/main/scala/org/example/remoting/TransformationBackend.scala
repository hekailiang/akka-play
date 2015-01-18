package org.example.remoting

import akka.actor.{RootActorPath, Actor, ActorSystem, Props}
import akka.cluster.{MemberStatus, Member, Cluster}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import com.typesafe.config.ConfigFactory
import org.example.remoting.RemotingProtocol._

/**
 * Created by kailianghe on 1/18/15.
 */
object TransformationBackend {

  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load("AkkaRemoting.conf")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[TransformationBackend], name = "backend")
  }
}

class TransformationBackend extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp  re-subscribe when restart
  /**
   * When the actor first starts up, it subscribes itself to the cluster, telling the cluster to send it
   * CurrentClusterState and MemberUp events:
   */
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case TransformationJob(text) => sender() ! TransformationResult(s"$self reply to $sender() with '${text.toUpperCase}'")
    /**
     * A snapshot of the full state, akka.cluster.ClusterEvent.CurrentClusterState, is sent to the subscriber
     * as the first message, followed by events for incremental updates. [Only send at first time, so newly joined
     * node can catch up what happened before. At this case, Frontend node can be discovered by Backend node, if
     * Frontend node is started before Backend node, or new Backend has joined the cluster.]
     */
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
}
