package com.wjr.akka.customerService.client

import akka.actor.TypedActor.{context, self}
import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.wjr.akka.customerService.common.{ClientMessage, ServerMessage}

import scala.io.StdIn

/**
 *
 * @Package: com.wjr.akka.customerService.client
 * @ClassName: Client1
 * @Author: 86157
 * @CreateTime: 2021/7/13 19:34
 * @Description:
 */
class Client2(serverHost : String, serverPort : Int) extends Actor {
    //定义一个Service的Ref
    var serverActorRef : ActorSelection = _

    //在Actor中，有一个PreStart方法，他会在此Actor运行前执行
    //通常将初始化工作，放在PreStart方法中
    override def preStart() : Unit = {
        serverActorRef = context.actorSelection(s"akka.tcp://Server@${serverHost}:${serverPort}/user/WJRService")
        println("serverActorRef=" + serverActorRef)
    }

    override def receive : Receive = {
        case "start" => {
            println("client2")
            println("客户端2 start，可以咨询问题了!")
        }
        case msg : String => {
            //发给服务器（客户）
            serverActorRef ! ClientMessage(msg) //使用了ClientMessage case class 的apply方法
        }
        //接收到服务端回复
        case ServerMessage(msg) => {
            println("收到客服回复：" + msg)
        }
    }
}

object Client2 extends App {

    val (clientHost, clientPort, serverHost, serverPort) = ("127.0.0.1", 9980, "127.0.0.1", 19999)
    val config = ConfigFactory.parseString(
        s"""
           |akka.actor.provider="akka.remote.RemoteActorRefProvider"
           |akka.remote.netty.tcp.hostname=$clientHost
           |akka.remote.netty.tcp.port=$clientPort
        """.stripMargin)
    val clientActorSystem = ActorSystem("client", config)
    //创建Client1 实例和Actor引用
    private val client2 : ActorRef = clientActorSystem.actorOf(Props(new Client2(serverHost, serverPort)), "ArF")

    client2 ! "start"

    //客户端发送消息给服务器
    val loop = true
    while (loop) {
        println("请输入要咨询的问题")
        val msg = StdIn.readLine()
        if (msg.contains("exit")) {
            context.stop(self)
            context.system.terminate()
        }
        client2 ! msg
    }
}