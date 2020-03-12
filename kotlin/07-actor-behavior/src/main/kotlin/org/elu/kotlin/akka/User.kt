package org.elu.kotlin.akka

import akka.actor.AbstractActorWithStash
import akka.japi.pf.ReceiveBuilder

data class User(val username: String, val email: String)

sealed class DBOperation
object Create : DBOperation()
object Update : DBOperation()
object Read : DBOperation()
object Delete : DBOperation()

sealed class Message
object Connect : Message()
object Disconnect : Message()
data class Operation(val dbOperation: DBOperation, val user: User?) : Message()

class UserStorage : AbstractActorWithStash() {

  override fun createReceive(): Receive = disconnected()

  private fun connected(): Receive =
      ReceiveBuilder()
          .match(Message::class.java, this::onConnected)
          .build()

  private fun onConnected(msg: Message) =
      when (msg) {
        is Disconnect -> {
          println("User storage disconnect from DB")
          context.unbecome()
        }
        is Operation -> {
          println("User storage receive ${msg.dbOperation} to do in user: ${msg.user}")
        }
        else -> {
          println("Shouldn't ended up here, message: $msg")
        }
      }

  private fun disconnected(): Receive =
      ReceiveBuilder()
          .match(Message::class.java, this::onDisconnected)
          .build()

  private fun onDisconnected(msg: Message) =
      when (msg) {
        is Connect -> {
          println("User storage connected to DB")
          unstashAll()
          context.become(connected())
        }
        else -> {
          // stash messages here
          stash()
        }
      }
}
