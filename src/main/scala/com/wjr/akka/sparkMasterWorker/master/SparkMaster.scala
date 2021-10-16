package com.wjr.akka.sparkMasterWorker.master

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import com.wjr.akka.sparkMasterWorker.common.{HeartBeat, RegisterWorkerInfo, RemoveTimeOutWorker, StartTimeOutWorker, WorkerInfo}

import scala.collection.mutable

/**
 *
 * @Package: com.wjr.akka.sparkMasterWorker.master
 * @ClassName: SparkMaster
 * @Author: 86157
 * @CreateTime: 2021/7/14 9:55
 * @Description:
 */
class SparkMaster extends Actor {
    //定义一个HashMap，管理Workers
    var workers = mutable.HashMap[String, WorkerInfo]()

    override def receive : Receive = {
        case "start" => {
            println(self.path.name + " 启动了！！")
            //给自己发送检查worker心跳信息
            self ! StartTimeOutWorker
        }
        case RegisterWorkerInfo(id, cpu, ram) => {
            if (!workers.contains(id)) {
                //创建WorkerInfo
                val info = new WorkerInfo(id, cpu, ram)
                workers += ((id, info))
                sender() ! RegisterWorkerInfo
                println("服务器上的worker=" + workers)
                println(sender().path.name + " 注册成功！！")
            }

        } //接收到Worker的注册
        case HeartBeat(id) =>{//收到worker的心跳
            //更新对应的worker心跳时间
            //从workers中取出workerInfo
            val workerInfo = workers(id)
            workerInfo.lastHeartBeat = System.currentTimeMillis()
            println("master更新了 " +id + " 心跳时间。。。")
        }
        case StartTimeOutWorker =>{//开启定时任务，每隔9s执行一次，检查超时任务
            import scala.concurrent.duration._
            import context.dispatcher
            context.system.scheduler.schedule(0 millis,9000 millis,self,RemoveTimeOutWorker)
        }
        case RemoveTimeOutWorker =>{//检测worker心跳超时（now - lastHeartBeat），并从map中删除
            //首先将workers 的WorkerInfo取出
            val workerInfos = workers.values
            val nowTime = System.currentTimeMillis()
            //先把所有的超时workerInfo，删除即可
            workerInfos.filter(workerInfo => (nowTime-workerInfo.lastHeartBeat)>6000)//过滤超时6s的workInfo
                    .foreach(worker => workers.remove(worker.id))//删除worker

            println("当前剩余"+workers.size+"个worker存活！！")
        }
    }
}

object SparkMaster extends App {
    val masterHost = args(0)
    val masterPort = args(1).toInt
    val actorSystemName = args(2)
    val actorName = args(3)

    private val config : Config = ConfigFactory.parseString(
        s"""
           |akka.actor.provider="akka.remote.RemoteActorRefProvider"
           |akka.remote.netty.tcp.hostname=${masterHost}
           |akka.remote.netty.tcp.port=${masterPort}
            """.stripMargin)

    val master : ActorSystem = ActorSystem(actorSystemName, config)
    val sparkMasterActorRef : ActorRef = master.actorOf(Props [SparkMaster], actorName)
    //启动 master
    sparkMasterActorRef ! "start"

}