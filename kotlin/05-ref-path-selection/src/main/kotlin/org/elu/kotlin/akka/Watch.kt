package org.elu.kotlin.akka

import akka.actor.AbstractActor
import akka.actor.ActorIdentity
import akka.actor.ActorSelection
import akka.actor.ActorSystem
import akka.actor.Identify
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

class Watcher : AbstractActor() {
  private val selection: ActorSelection = context.actorSelection("/user/counter")

  init {
    selection.tell(Identify(null), self)
  }

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(ActorIdentity::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: ActorIdentity) {
    if (msg.ref() != null && msg.ref().isDefined) {
      println("Actor Reference for counter is ${msg.ref().get()}")
    } else {
      println("Actor selection for actor doesn't live")
    }
  }
}

fun main() {
  val system = ActorSystem.create("watch-actor-selection")

  system.actorOf(Props.create(Counter::class.java), "counter")
  system.actorOf(Props.create(Watcher::class.java), "watcher")

  Thread.sleep(1000)
  system.terminate()
}
