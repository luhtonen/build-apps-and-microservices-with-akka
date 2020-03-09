package org.elu.kotlin.akka

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RandomGroup

fun main() {
  val system = ActorSystem.create("random-router-group")

  system.actorOf(Props.create(Worker::class.java), "w1")
  system.actorOf(Props.create(Worker::class.java), "w2")
  system.actorOf(Props.create(Worker::class.java), "w3")

  val paths = listOf(
    "/user/w1",
    "/user/w2",
    "/user/w3"
  )

  val routerGroup = system.actorOf(RandomGroup(paths).props(), "random-router-group")

  repeat(4) {
    routerGroup.tell(Work(), ActorRef.noSender())
  }

  Thread.sleep(100)
  system.terminate()
}
