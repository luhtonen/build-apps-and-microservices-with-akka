package org.elu.kotlin.akka

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import kotlin.random.Random

class RouterGroup(val routees: List<String>) : AbstractActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(Work::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: Work) {
    println("I'm Router Group and I received a Work message...")
    context.actorSelection(routees[Random.nextInt(routees.size)]).forward(msg, context)
  }
}

fun main() {
  val system = ActorSystem.create("router-group")

  system.actorOf(Props.create(Worker::class.java), "w1")
  system.actorOf(Props.create(Worker::class.java), "w2")
  system.actorOf(Props.create(Worker::class.java), "w3")
  system.actorOf(Props.create(Worker::class.java), "w4")
  system.actorOf(Props.create(Worker::class.java), "w5")

  val workers = listOf(
    "/user/w1",
    "/user/w2",
    "/user/w3",
    "/user/w4",
    "/user/w5"
  )
  val routerGroup = system.actorOf(Props.create(RouterGroup::class.java, workers))

  routerGroup.tell(Work(), ActorRef.noSender())
  routerGroup.tell(Work(), ActorRef.noSender())
  routerGroup.tell(Work(), ActorRef.noSender())
  routerGroup.tell(Work(), ActorRef.noSender())

  Thread.sleep(100)
  system.terminate()
}
