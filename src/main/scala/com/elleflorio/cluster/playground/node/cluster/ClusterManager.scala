package com.elleflorio.cluster.playground.node.cluster

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.{Cluster, MemberStatus}
import com.elleflorio.cluster.playground.Server.system
import com.elleflorio.cluster.playground.node.cluster.ClusterManager.GetMembers

object ClusterManager {

  sealed trait ClusterMessage
  case object GetMembers extends ClusterMessage

  def props(nodeId: String) = Props(new ClusterManager(nodeId))
}

class ClusterManager(nodeId: String) extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)
  val listener: ActorRef = system.actorOf(ClusterListener.props(cluster), "clusterListener")

  override def receive: Receive = {
    case GetMembers => {
      sender() ! cluster.state.members.filter(_.status == MemberStatus.up)
        .map(_.address.toString)
        .toList
    }
  }
}