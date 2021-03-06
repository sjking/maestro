//package com.adendamedia
//
//import akka.testkit.TestKit
//import akka.testkit.TestActorRef
//import akka.testkit.TestProbe
//import akka.actor.ActorSystem
//import akka.actor.Actor
//import akka.actor.ActorRefFactory
//import com.adendamedia.EventBus._
//import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
//import scala.concurrent.duration._
//
//class SamplerTest extends TestKit(ActorSystem("SamplerTest"))
//  with WordSpecLike with BeforeAndAfterAll with MustMatchers {
//  implicit val ec = system.dispatcher
//
//  override def afterAll(): Unit = {
//    system.terminate()
//  }
//
//  "Pattern Sampler" must {
//    "ask Kubernetes to scale up if it reaches the minimum threshold for the configured sample period" in {
//      import Sampler._
//      import com.adendamedia.kubernetes.Kubernetes._
//
//      val threshold: Int = 10
//      val maxValue: Int = 20
//
//      val eventBus = TestActorRef(new Actor{
//        def receive = {
//          case GetPatternSample =>
//            sender ! threshold
//        }
//      })
//
//      val probe = TestProbe()
//      val k8sMaker = (_: ActorRefFactory) => probe.ref
//
//      val sampler = system.actorOf(Sampler.props(eventBus, k8sMaker, threshold, maxValue))
//
//      sampler ! SamplePattern
//
//      probe.expectMsg(ScaleUp)
//    }
//  }
//
//  "Pattern Sampler" must {
//    "not ask Kubernetes to scale up if it reaches the minimum threshold for the configured sample period" in {
//      import Sampler._
//
//      val threshold: Int = 10
//      val maxValue: Int = 20
//
//      val eventBus = TestActorRef(new Actor{
//        def receive = {
//          case GetPatternSample =>
//            sender ! (threshold - 1)
//        }
//      })
//
//      val probe = TestProbe()
//      val k8sMaker = (_: ActorRefFactory) => probe.ref
//
//      val sampler = system.actorOf(Sampler.props(eventBus, k8sMaker, threshold, maxValue))
//
//      sampler ! SamplePattern
//
//      probe.expectNoMsg(100 milliseconds)
//    }
//  }
//
//  "Pattern Sampler" must {
//    "ask Kubernetes to scale up if it reaches the minimum threshold for the configured sample period when counter rolls over" in {
//      import Sampler._
//      import com.adendamedia.kubernetes.Kubernetes._
//
//      val threshold: Int = 1
//      val maxValue: Int = 19
//
//      val eventBus = TestActorRef(new Actor{
//        def receive = {
//          case GetPatternSample =>
//            sender ! 10
//        }
//      })
//
//      val probe = TestProbe()
//      val k8sMaker = (_: ActorRefFactory) => probe.ref
//
//      val sampler = system.actorOf(Sampler.props(eventBus, k8sMaker, threshold, maxValue))
//
//      sampler ! SamplePattern
//      sampler ! SamplePattern
//
//      probe.expectMsg(ScaleUp)
//    }
//  }
//}
