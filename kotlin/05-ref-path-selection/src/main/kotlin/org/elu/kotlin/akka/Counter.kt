package org.elu.kotlin.akka

import akka.actor.AbstractActor
import akka.japi.pf.ReceiveBuilder

sealed class Msg
data class Inc(val num: Int) : Msg()
data class Dec(val num: Int) : Msg()

class Counter : AbstractActor() {
  private var count = 0
  override fun createReceive(): Receive =
      ReceiveBuilder()
          .match(Msg::class.java, this::onMessage)
          .build()

  private fun onMessage(msg: Msg) =
      when(msg) {
        is Inc -> count += msg.num
        is Dec -> count -= msg.num
      }
}
