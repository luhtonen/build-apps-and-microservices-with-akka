package org.elu.scala.akka

import akka.actor.{Actor, ActorIdentity, ActorRef, ActorSelection, Identify}

class Watcher extends Actor {
  var actorRef: ActorRef = _

  val selection: ActorSelection = context.actorSelection("/user/counter")

  selection ! Identify(None)

  override def receive: Receive = {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Actor Reference for counter is $ref")
    case ActorIdentity(_, None) =>
      println(s"Actor selection for actor doesn't live")
  }
}
