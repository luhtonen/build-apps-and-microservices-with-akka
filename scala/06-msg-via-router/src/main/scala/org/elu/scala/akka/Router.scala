package org.elu.scala.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.elu.scala.akka.Worker.Work

class Router extends Actor {
  var routees: List[ActorRef] = _


  override def preStart(): Unit = {
    routees = List.fill(5) {
      context.actorOf(Props[Worker])
    }
  }

  override def receive: Receive = {
    case msg: Work =>
      println(s"I'm Router and I received a message...")
      routees(util.Random.nextInt(routees.size)) forward msg
  }
}

object Router extends App {
  val system = ActorSystem("router")

  val router = system.actorOf(Props[Router])

  router ! Work()
  router ! Work()
  router ! Work()
  router ! Work()

  Thread.sleep(100)
  system.terminate()
}

class RouterGroup(routees: List[String]) extends Actor {
  override def receive: Receive = {
    case msg: Work =>
      println(s"I'm Router Group and I received a Work message...")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward msg
  }
}

object RouterGroup extends App {
  val system = ActorSystem("router-group")

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")
  system.actorOf(Props[Worker], "w4")
  system.actorOf(Props[Worker], "w5")

  val workers = List(
    "/user/w1",
    "/user/w2",
    "/user/w3",
    "/user/w4",
    "/user/w5"
  )

  val routerGroup = system.actorOf(Props(classOf[RouterGroup], workers))

  routerGroup ! Work()
  routerGroup ! Work()
  routerGroup ! Work()

  Thread.sleep(100)
  system.terminate()
}
