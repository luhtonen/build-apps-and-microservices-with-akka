package org.elu.kotlin.akka

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import akka.pattern.Patterns
import akka.util.Timeout
import scala.concurrent.Await
import java.util.concurrent.TimeUnit

data class User(val username: String,
                val email: String)

sealed class RecorderMsg
data class NewUser(val user: User) : RecorderMsg()

sealed class CheckerMsg
data class CheckUser(val user: User) : CheckerMsg()

sealed class CheckerResponse
data class BlackUser(val user: User) : CheckerResponse()
data class WhiteUser(val user: User) : CheckerResponse()

sealed class StorageMsg
data class AddUser(val user: User) : StorageMsg()

class Storage(private val users: MutableList<User> = mutableListOf()) : AbstractActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(AddUser::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: StorageMsg) =
      when (msg) {
        is AddUser -> {
          users.add(msg.user)
          println("Storage: ${msg.user} added")
        }
      }
}

class Checker(private val blacklist: List<User> = listOf(User("Adam", "adam@mail.com"))) : AbstractActor() {
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(CheckerMsg::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: CheckerMsg) =
      when (msg) {
        is CheckUser -> if (blacklist.contains(msg.user)) {
          println("Checker: ${msg.user} is in blacklist")
          sender.tell(BlackUser(msg.user), ActorRef.noSender())
        } else {
          println("Checker: ${msg.user} is not in blacklist")
          sender.tell(WhiteUser(msg.user), ActorRef.noSender())
        }
      }
}

class Recorder(private val checker: ActorRef, private val storage: ActorRef) : AbstractActor() {
  private val timeout = Timeout(5, TimeUnit.SECONDS)
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(RecorderMsg::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: RecorderMsg) =
      when (msg) {
        is NewUser -> {
          val future = Patterns.ask(checker, CheckUser(msg.user), timeout)
          when (val result = Await.result(future, timeout.duration()) as CheckerResponse) {
            is BlackUser -> {
              println("Recorder: ${result.user} is in blacklist")
            }
            is WhiteUser -> {
              storage.tell(AddUser(result.user), ActorRef.noSender())
            }
          }
        }
      }

  companion object {
    fun props(checker: ActorRef, storage: ActorRef): Props = Props.create(Recorder::class.java, checker, storage)
  }
}

fun main() {
  val system = ActorSystem.create("talk-to-actor")

  // Create the 'checker' actor
  val checker = system.actorOf(Props.create(Checker::class.java), "checker")
  // Create the 'storage' actor
  val storage = system.actorOf(Props.create(Storage::class.java), "storage")
  // Create the 'recorder' actor
  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  //send NewUser Message to Recorder
  recorder.tell(NewUser(User("Jon", "jon@packt.com")), ActorRef.noSender())

  Thread.sleep(100)

  //send NewUser Message to Recorder with black listed user
  recorder.tell(NewUser(User("Adam", "adam@mail.com")), ActorRef.noSender())

  Thread.sleep(100)

  //shutdown system
  system.terminate()
}
