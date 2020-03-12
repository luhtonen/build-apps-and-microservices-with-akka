package org.elu.scala.akka

import akka.actor.{Actor, Stash}

case class User(username: String, email: String)

object UserStorage {
  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }

  case object Connect
  case object Disconnect
  case class Operation(dBOperation: DBOperation, user: Option[User])
}

class UserStorage extends Actor with Stash {
  import org.elu.scala.akka.UserStorage._

  override def receive: Receive = disconnected

  def connected: Receive = {
    case Disconnect =>
      println("User storage disconnect from DB")
      context.unbecome()
    case Operation(op, user) =>
      println(s"User storage receive $op to do in user: $user")
  }

  def disconnected: Receive = {
    case Connect =>
      println("User storage connected to DB")
      unstashAll()
      context.become(connected)
    case _ =>
      stash()
  }
}
