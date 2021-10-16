package com.wjr.akka.actors

import akka.actor.{Actor, ActorRef}

/**
 *
 * @Package: com.wjr.akka.actors
 * @ClassName: AActor
 * @Author: 86157
 * @CreateTime: 2021/7/11 19:27
 * @Description:
 */
class AActor(actorRef : ActorRef) extends Actor {
    val bActorRef : ActorRef = actorRef
    var tricks = 0 // TODO: 招数
    override def receive : Receive = {
        case "start" => {
            println("AActor出招了\nstart ok")
            self ! "我打"
        }
        case "我打" => {
            tricks+=1
            //给BActor发送消息
            //需要持有BActor的引用（BActorRef）
            println(s"AActor(黄飞鸿):看我佛山无影脚 $tricks")
            bActorRef ! "我打" //给BActor发送消息

            if (tricks == 10){
                sender() ! "我输了,再见"
                context.stop(self)//停止接收消息

            }
            Thread.sleep(1000)
        }
        case _ =>

    }
}
