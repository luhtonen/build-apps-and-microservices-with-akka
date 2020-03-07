package org.elu.kotlin.akka

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Terminated
import akka.japi.pf.ReceiveBuilder

class Ares(private val athena: ActorRef) : AbstractActor() {
  override fun preStart() {
    context.watch(athena)
  }

  override fun postStop() {
    println("Ares postStop...")
  }

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(Any::class.java) { when(it) { is Terminated -> context.stop(self) } }
          .build()
}

class Athena : AbstractActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(String::class.java) {
            println("Athena received $it")
            context.stop(self)
          }
          .build()
}

fun main() {
  val system = ActorSystem.create("supervision")

  val athena = system.actorOf(Props.create(Athena::class.java), "athena")
  system.actorOf(Props.create(Ares::class.java, athena), "ares")

  println("starting...")
  Thread.sleep(100)
  athena.tell("hello", ActorRef.noSender())

  Thread.sleep(1000)
  println("stopping...")

  system.terminate()
}
