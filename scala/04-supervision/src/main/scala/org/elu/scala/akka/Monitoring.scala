package org.elu.scala.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}

class Ares(athena: ActorRef) extends Actor {
  override def preStart(): Unit = {
    context.watch(athena)
  }

  override def postStop(): Unit = {
    println("Ares postStop...")
  }

  override def receive: Receive = {
    case Terminated =>
      context.stop(self)
  }
}

class Athena extends Actor {
  override def receive: Receive = {
    case msg =>
      println(s"Athena received $msg")
      context.stop(self)
  }
}

object Monitoring extends App {
  val system = ActorSystem("monitoring")

  val athena = system.actorOf(Props[Athena], "athena")
  val ares = system.actorOf(Props(new Ares(athena)), "ares")

  println("starting...")
  Thread.sleep(100)
  athena ! "hello"

  Thread.sleep(100)
  println("stopping...")

  system.terminate()
}
