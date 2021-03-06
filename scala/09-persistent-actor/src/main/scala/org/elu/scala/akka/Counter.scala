package org.elu.scala.akka

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, Recovery, RecoveryCompleted, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

trait MySerialisable

object Counter {
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes(
    Array(
      new JsonSubTypes.Type(value = classOf[Increment], name = "increment"),
      new JsonSubTypes.Type(value = classOf[Decrement], name = "decrement")))
  sealed trait Operation {
    val count: Int
  }
  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  case class Cmd(op: Operation) extends MySerialisable
  case class Evt(op: Operation) extends MySerialisable

  case class State(count: Int) extends MySerialisable
}

class Counter extends PersistentActor with ActorLogging {
  import Counter._

  println("Starting .............................")

  override def persistenceId: String = "counter-example"

  var state: State = State(count = 0)

  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(count)) =>
      state = State(count = state.count + count)
      takeSnapshot
    case Evt(Decrement(count)) =>
      state = State(count = state.count - count)
      takeSnapshot
  }

  override def receiveRecover: Receive = {
    case evt: Evt =>
      println(s"Counter receive $evt on recovery mode")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"Counter receive snapshot with data: $snapshot on recovery mode")
      state = snapshot
    case RecoveryCompleted =>
      println("Recovery completed and now I'll switch to receiving mode")
  }

  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"Counter receive $cmd")
      persist(Evt(op)) { evt =>
        updateState(evt)
      }
    case "print" =>
      println(s"The current state of counter is $state")
    case SaveSnapshotSuccess(metadata) =>
      println("save snapshot succeed")
    case SaveSnapshotFailure(metadata, reason) =>
      println(s"save snapshot failed and failure is $reason")
  }

  def takeSnapshot = {
    if (state.count % 5 == 0) {
      saveSnapshot(state)
    }
  }

  // disable recovery
  //override def recovery: Recovery = Recovery.none
}
