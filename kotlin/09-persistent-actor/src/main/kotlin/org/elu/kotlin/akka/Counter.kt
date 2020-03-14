package org.elu.kotlin.akka

import akka.japi.pf.ReceiveBuilder
import akka.persistence.AbstractPersistentActor
import akka.persistence.Recovery
import akka.persistence.RecoveryCompleted
import akka.persistence.SaveSnapshotFailure
import akka.persistence.SaveSnapshotSuccess
import akka.persistence.SnapshotOffer
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

interface MySerialisable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = Increment::class, name = "increment"),
  JsonSubTypes.Type(value = Decrement::class, name = "decrement"))
sealed class Operation(open val count: Int)
data class Increment(override val count: Int) : Operation(count)
data class Decrement(override val count: Int) : Operation(count)

data class Cmd(val op: Operation) : MySerialisable
data class Evt(val op: Operation) : MySerialisable

data class State(val count: Int) : MySerialisable

class Counter : AbstractPersistentActor() {
  override fun persistenceId(): String = "counter-example"

  var state: State = State(count = 0)

  private fun updateState(evt: Evt) {
    state = when (evt.op) {
      is Increment -> State(count = state.count + evt.op.count)
      is Decrement -> State(count = state.count - evt.op.count)
    }
    takeSnapshot()
  }

  override fun createReceiveRecover(): Receive =
      ReceiveBuilder()
          .match(Evt::class.java) {
            println("Counter receive $it on recovery mode")
            updateState(it)
          }
          .match(SnapshotOffer::class.java) {
            val snapshot = it.snapshot()
            println("Counter receive snapshot with data: $snapshot on recovery mode")
            state = snapshot as State
          }
          .match(RecoveryCompleted::class.java) { println("Recovery completed and now I'll switch to receiving mode") }
          .build()

  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(Cmd::class.java) { cmd: Cmd ->
            println("Counter receive $cmd")
            persist(Evt(cmd.op)) { updateState(it) }
          }
          .matchEquals("print") { println("The current state of counter is $state") }
          .match(SaveSnapshotSuccess::class.java) { println("save snapshot succeed") }
          .match(SaveSnapshotFailure::class.java) { failure: SaveSnapshotFailure -> println("save snapshot failed and failure is ${failure.cause()}") }
          .build()

  private fun takeSnapshot() {
    if (state.count % 5 == 0) {
      saveSnapshot(state)
    }
  }

  // override fun recovery(): Recovery = Recovery.none()
}
