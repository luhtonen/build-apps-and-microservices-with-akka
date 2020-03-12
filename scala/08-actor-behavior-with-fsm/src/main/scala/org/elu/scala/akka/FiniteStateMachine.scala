package org.elu.scala.akka

import akka.actor.{ActorSystem, Props}

object FiniteStateMachine extends App {
  import UserStorageFSM._

  val system = ActorSystem("hotswap-FSM")

  val userStorage = system.actorOf(Props[UserStorageFSM], "userStorage-fsm")

  userStorage ! Operation(DBOperation.Create, User("Admin", "admin@test.com"))
  userStorage ! Connect
  userStorage ! Disconnect

  Thread.sleep(100)
  system.terminate()
}
