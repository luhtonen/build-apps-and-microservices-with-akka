package org.elu.scala.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.elu.scala.akka.Checker.{BlackUser, CheckUser, WhiteUser}
import org.elu.scala.akka.Recorder.NewUser
import org.elu.scala.akka.Storage.AddUser

import scala.concurrent.duration._
import scala.language.postfixOps

case class User(username: String, email: String)

object Recorder {
  sealed trait RecorderMsg
  case class NewUser(user: User) extends RecorderMsg

  def props(checker: ActorRef, storage: ActorRef): Props =
    Props(new Recorder(checker, storage))
}

object Checker {
  sealed trait CheckerMsg
  case class CheckUser(user: User) extends CheckerMsg

  sealed trait CheckerResponse
  case class BlackUser(user: User) extends CheckerResponse
  case class WhiteUser(user: User) extends CheckerResponse
}

object Storage {
  sealed trait StorageMsg
  case class AddUser(user: User) extends StorageMsg
}

class Storage extends Actor {
  var users = List.empty[User]

  override def receive: Receive = {
    case AddUser(user) =>
      println(s"Storage: $user added")
      users = user :: users
  }
}

class Checker extends Actor {
  val blacklist = List(
    User("Adam", "adam@mail.com")
  )

  override def receive: Receive = {
    case CheckUser(user) if blacklist.contains(user) =>
      println(s"Checker: $user is in blacklist")
      sender() ! BlackUser(user)
    case CheckUser(user) =>
      println(s"Checker: $user is not in blacklist")
      sender() ! WhiteUser(user)
  }
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout: Timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case NewUser(user) =>
      checker ? CheckUser(user) map {
        case WhiteUser(user) =>
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"Recorder: $user is in blacklist")
      }
  }
}

object TalkToActor extends App {
  val system = ActorSystem("talk-to-actor")

  // Create the 'checker' actor
  val checker = system.actorOf(Props[Checker], "checker")
  // Create the 'storage' actor
  val storage = system.actorOf(Props[Storage], "storage")
  // Create the 'recorder' actor
  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  //send NewUser Message to Recorder
  recorder ! Recorder.NewUser(User("Jon", "jon@packt.com"))

  Thread.sleep(100)

  //send NewUser Message to Recorder with black listed user
  recorder ! Recorder.NewUser(User("Adam", "adam@mail.com"))

  Thread.sleep(100)

  //shutdown system
  system.terminate()
}
