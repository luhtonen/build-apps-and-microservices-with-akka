package org.elu.scala.akka

import akka.actor.{ActorSystem, PoisonPill, Props}

object ActorPath extends App {
  val system = ActorSystem("Actor-Paths")

  val counter1 = system.actorOf(Props[Counter], "Counter")
  println(s"Actor reference for counter1: $counter1")

  val counterSelection1 = system.actorSelection("counter")
  println(s"Actor selection for counterSelection1: $counterSelection1")

  counter1 ! PoisonPill
  Thread.sleep(100)

  val counter2 = system.actorOf(Props[Counter], "counter")
  println(s"Actor reference for counter2: $counter2")

  val counterSelection2 = system.actorSelection("counter")
  println(s"Actor selection for counterSelection2: $counterSelection2")

  Thread.sleep(1000)
  system.terminate()
}

object Watch extends App {
  val system = ActorSystem("watch-actor-selection")

  val counter = system.actorOf(Props[Counter], "counter")
  val watcher = system.actorOf(Props[Watcher], "watcher")

  Thread.sleep(1000)
  system.terminate()
}
