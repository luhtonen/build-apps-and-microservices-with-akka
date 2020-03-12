package org.elu.scala.akka

import akka.actor.{ActorSystem, Props}

object BecomeHotswap extends App {
  import UserStorage._

  val system = ActorSystem("hotswap-become")

  val userStorage = system.actorOf(Props[UserStorage], "userStorage")

  userStorage ! Operation(DBOperation.Create, Some(User("Admin", "admin@test.com")))
  userStorage ! Connect
  userStorage ! Disconnect

  Thread.sleep(100)
  system.terminate()
}
