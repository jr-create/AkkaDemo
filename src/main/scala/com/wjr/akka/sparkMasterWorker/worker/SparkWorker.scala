package com.wjr.akka.sparkMasterWorker.worker

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.wjr.akka.sparkMasterWorker.common.{HeartBeat, RegisterWorkerInfo, SendHeartBeat}


import java.util.UUID
import scala.util.Random

/**
 *
 * @Package: com.wjr.akka.sparkMasterWorker.worker
 * @ClassName: SparkWorker
 * @Author: 86157
 * @CreateTime: 2021/7/14 10:01
 * @Description:
 */
class SparkWorker(masterActorSystemName : String, masterActorName:String, masterHost : String, masterPort : Int) extends Actor {
    //masterPorxy是Master的代理 即：引用Ref
    var masterPorxy : ActorSelection = _
    val masterURL = s"akka.tcp://${masterActorSystemName}@${masterHost}:${masterPort}/user/${masterActorName}"
    val id = UUID.randomUUID().toString

    //初始化
    override def preStart() : Unit = {

        masterPorxy = context.actorSelection(masterURL)
        println(masterPorxy)
    }
    override def receive : Receive = {
        case "start" => {
            println(self.path.name + " 启动了！！")
            //向master发出注册消息，需要编写协议
            masterPorxy ! RegisterWorkerInfo(id, (Random.nextInt(12) + 3), Random.nextInt(16 * 1024) + 3)
        }
        case RegisterWorkerInfo => { //收到master注册成功消息
            println(s"${self.path.name}=" + id + " 注册成功")
            //注册成功后，就定义一个定时器，每隔一定时间，发送SendHeartBeat给自己
            /** 下面代码说明
             * 1、0 millis 表示：立即触发
             * 2、3000 millis 表示：每隔 3s 执行一次
             * 3、self 表示：发送给自己
             * 4、SendHeartBeat 表示：发送的内容
             */
            import scala.concurrent.duration._
            import context.dispatcher
            context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)
        }
        case SendHeartBeat => { //收到向master发送心跳指令
            println(s"${self.path.name}=" + id + " 给master发送心跳")
            masterPorxy ! HeartBeat(id)
        }

    }
}

object SparkWorker {
    def main(args : Array[String]) : Unit = {
        val workerHost = args(0)
        val workerPort = args(1).toInt
        val workerActorSystemName = args(2)
        val workerName = args(3)

        val masterHost = args(4)
        val masterPort = args(5).toInt
        val masterActorSystemName = args(6)
        val masterActorName = args(7)


        val config = ConfigFactory.parseString(
            s"""
               |akka.actor.provider="akka.remote.RemoteActorRefProvider"
               |akka.remote.netty.tcp.hostname=${workerHost}
               |akka.remote.netty.tcp.port=${workerPort}
            """.stripMargin)

        val workerActorSystem : ActorSystem = ActorSystem(workerActorSystemName, config)
        val worker0ActorRef : ActorRef = workerActorSystem.actorOf(
            Props(new SparkWorker(masterActorSystemName, masterActorName, masterHost, masterPort)), workerName)
        worker0ActorRef ! "start"
    }

}
