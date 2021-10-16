package com.wjr.akka.customerService.service

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.wjr.akka.customerService.common.{ClientMessage, MessageProtocol, ServerMessage}
import com.wjr.akka.customerService.service.WJRService.config
import com.wjr.akka.customerService.util.ActorUtil

import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}

/**
 *
 * @Package: com.wjr.akka.customerService.service
 * @ClassName: WJRService
 * @Author: 86157
 * @CreateTime: 2021/7/13 19:23
 * @Description:
 */
class WJRService extends Actor {
    //Map(ActorName,（Host,Port）)
    private val map = Map [String, Tuple2[String, Int]]()

    override def receive : Receive = {
        case "start" => println("客服已经开始工作了。。。。")
        // 如果接收到 Client 的 Message 消息
        case ClientMessage(msg) => {
            if (!map.contains(sender().path.name)) {
                val senderPath = sender().path
                println("map="+senderPath.address.host.get+"\t"+senderPath.address.port.get)
                map.put(senderPath.name, (senderPath.address.host.get, senderPath.address.port.get))
            }
            msg match {
                case "exit" =>{
                    context.stop(self)
                    context.system.terminate()
                }
                case "大数据学费" => {
                    println(sender().path.name + "询问：大数据学费")
                    sender() ! ServerMessage("35000RMB")
                }
                case "学校地址" => {
                    println(sender().path.name + "询问：学校地址")
                    sender() ! ServerMessage("地球中国")
                }
                case "学习什么技术" => {
                    println(sender().path.name + "询问：学习什么技术")
                    sender() ! ServerMessage("大数据，java，Scala")
                }
                case _ => {//通过@访问信息
                    println(sender().path.name + "询问：" + msg)
                    if (msg.contains("@")) {
                        val intToString = msg.split("@")(1)
                        val message = intToString.split(" ") //ActorName message

                        val maybeTuple = map.getOrElse(message(0), (null, null)) //获取Map(ActorName,（Host,Port）)
                        if (maybeTuple._1 != null) { //判断map中是否有这个ActorName
                            println(s"akka.tcp://client@${maybeTuple._1}:${maybeTuple._2}/user/${message(0)}")
                            val actorRef = new ActorUtil().test(context,message, maybeTuple)
                            println("actorRef="+actorRef)
                            actorRef ! ServerMessage(sender().path.name +"给你发送："+ message(1)) //给目标发送message
                            sender() ! ServerMessage("发送给了" + message(0)) //给发送者返回提示
                        }else{
                            sender() ! ServerMessage("没有这个客户")
                        }
                    } else {
                        sender() ! ServerMessage("听不懂你在说什么")
                    }
                }
            }
        }

    }



}

//主程序 - 入口
object WJRService extends App {


    val host = "127.0.0.1" //服务端ip地址
    val port = 19999
    //创建config对象,指定协议类型，监听的ip和端口
    val config = ConfigFactory.parseString(
        s"""
           |akka.actor.provider="akka.remote.RemoteActorRefProvider"
           |akka.remote.netty.tcp.hostname=$host
           |akka.remote.netty.tcp.port=$port
        """.stripMargin)

    //创建ActorySystem，name相当于url，用于客户端定位服务器,
    private val serverActorSystem : ActorSystem = ActorSystem("Server", config)
    //创建Actor
    private val wjrServer : ActorRef = serverActorSystem.actorOf(Props [WJRService], "WJRService")

    wjrServer ! "start"
}

