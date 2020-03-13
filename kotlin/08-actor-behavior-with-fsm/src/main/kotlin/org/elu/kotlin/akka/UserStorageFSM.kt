package org.elu.kotlin.akka

import akka.actor.AbstractFSMWithStash

data class User(val username: String, val email: String)

// FSM state
sealed class State
object Connected : State()
object Disconnected : State()

// FSM data
sealed class Data
object EmptyData : Data()

sealed class DBOperation {
  override fun toString(): String = this.javaClass.simpleName
}
object Create : DBOperation()
object Update : DBOperation()
object Read : DBOperation()
object Delete : DBOperation()

sealed class Message
object Connect : Message()
object Disconnect : Message()
data class Operation(val op: DBOperation, val user: User?) : Message()

class UserStorageFSM : AbstractFSMWithStash<State, Data>() {
  init {
    // 1. define start with
    startWith(Disconnected, EmptyData)

    // 2. define states
    `when`(Disconnected,
           matchEvent(Connect::class.java,
                      EmptyData::class.java) { _: Connect, _: EmptyData ->
             println("UserStorage connected to DB")
             unstashAll()
             goTo(Connected).using(EmptyData)
           }
           .anyEvent { _: Any, _: Data ->
             stash()
             stay().using(EmptyData)
           })

    `when`(Connected,
           matchEvent(Disconnect::class.java,
                      EmptyData::class.java) { _: Disconnect, _: EmptyData ->
             println("UserStorage disconnected from DB")
             goTo(Disconnected).using(EmptyData)
           }
           .event(Operation::class.java,
                  EmptyData::class.java) { operation: Operation, _: Data ->
             println("UserStorage received ${operation.op} operation to do in user: ${operation.user}")
             stay().using(EmptyData)
           })

    // 3. initialise
    initialize()
  }
}