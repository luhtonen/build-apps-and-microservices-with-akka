package example

import akka.actor.{Actor, ActorSystem, Props}
import example.MusicController.{Play, Stop}
import example.MusicPlayer.{StartMusic, StopMusic}

object MusicController {
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg

  def props = Props[MusicController]
}

class MusicController extends Actor {
  override def receive: Receive = {
    case Play =>
      println("Music started .....")
    case Stop =>
      println("Music stopped .....")
  }
}

object MusicPlayer {
  sealed trait PlayMsg
  case object StartMusic extends PlayMsg
  case object StopMusic extends PlayMsg
}

class MusicPlayer extends Actor {
  override def receive: Receive = {
    case StartMusic =>
//      val controller = context.actorOf(Props[MusicController], "musicController") // this maybe dangerous
      val controller = context.actorOf(MusicController.props, "musicController")
      controller ! Play
    case StopMusic =>
      println("I don't want to stop music")
    case _ =>
      println("Unknown message")
  }
}

object Creation extends App {
  val system = ActorSystem("creation")
  val player = system.actorOf(Props[MusicPlayer], "player")
  player ! StartMusic

  player ! StopMusic

  system.terminate()
}
