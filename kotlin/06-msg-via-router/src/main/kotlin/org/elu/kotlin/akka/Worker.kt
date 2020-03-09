package org.elu.kotlin.akka

import akka.actor.AbstractActor
import akka.japi.pf.ReceiveBuilder

class Work

class Worker : AbstractActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(Work::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: Work) {
    println("I received Work message and my ActorRef: $self")
  }
}
