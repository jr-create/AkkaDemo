package com.wjr.akka.customerService.common

import akka.actor.{ActorRef, ActorSelection}
import akka.actor.TypedActor.context

/**
 *
 * @Package: com.wjr.akka.customerService.common
 * @ClassName: MessageProtocol
 * @Author: 86157
 * @CreateTime: 2021/7/13 20:34
 * @Description:
 */
class MessageProtocol {
}
case class ClientMessage(msg:String)
case class ServerMessage(msg:String)
