package com.adendamedia.kubernetes

import akka.actor._
import org.slf4j.LoggerFactory
import com.github.kliewkliew.cornucopia.actors.CornucopiaSource._

object CornucopiaTask {
  def props(cornucopiaRef: ActorRef, k8sController: ActorRef) = Props(new CornucopiaTask(cornucopiaRef, k8sController))

  trait Task
  case class AddMasterTask(ip: String) extends Task
  case class AddSlaveTask(ip: String) extends Task
}

class CornucopiaTask(cornucopiaRef: ActorRef, k8sController: ActorRef) extends Actor {
  import CornucopiaTask._
  import Kubernetes._

  private val logger = LoggerFactory.getLogger(this.getClass)

  private def buildRedisUri(ip: String) = "redis://" + ip

  def receive = {
    case AddMasterTask(ip) =>
      logger.info(s"Telling Cornucopia to add master node with IP '$ip'")
      cornucopiaRef ! Task("+master", buildRedisUri(ip))
    case AddSlaveTask(ip) =>
      logger.info(s"Telling Cornucopia to add slave node with IP '$ip'")
      cornucopiaRef ! Task("+slave", buildRedisUri(ip))
    case Right(nodeType: String) =>
      logger.info(s"Successfully added redis $nodeType node to cluster, telling Kubernetes controller")
      val msg = if (nodeType == "master") "Successfully added master redis node and resharded cluster"
                else "Successfully added slave redis node"
      k8sController ! ScaleUpSuccess(msg)
    case Left(nodeType: String) =>
      logger.error(s"Failed trying to add redis $nodeType node to cluster, telling Kubernetes controller")
      // TO-DO: throw exception and implement some type of supervision strategy
  }
}