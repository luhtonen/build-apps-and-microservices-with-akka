package org.elu.scala.akka

import akka.actor.{ActorSystem, Props}

object Persistent extends App {
  import Counter._

  val system = ActorSystem("persistent-actors")

  val counter = system.actorOf(Props[Counter])

  counter ! Cmd(Increment(3))
  counter ! Cmd(Increment(5))
  counter ! Cmd(Decrement(3))
  counter ! "print"

  Thread.sleep(3000)
  system.terminate()
}
