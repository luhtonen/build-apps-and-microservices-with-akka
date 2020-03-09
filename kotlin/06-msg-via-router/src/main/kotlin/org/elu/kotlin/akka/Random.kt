package org.elu.kotlin.akka

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.FromConfig

fun main() {
  val system = ActorSystem.create("random-router")

  val routerPool = system.actorOf(FromConfig().props(Props.create(Worker::class.java)), "random-router-pool")

  repeat(5) {
    routerPool.tell(Work(), ActorRef.noSender())
  }

  Thread.sleep(100)
  system.terminate()
}
