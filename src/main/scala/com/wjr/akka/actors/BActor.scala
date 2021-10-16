package com.wjr.akka.actors

import akka.actor.Actor

/**
 *
 * @Package: com.wjr.akka.actors
 * @ClassName: BActor
 * @Author: 86157
 * @CreateTime: 2021/7/11 19:28
 * @Description:
 */
class BActor extends Actor{
    var tricks = 0 // TODO: 招数
    override def receive : Receive = {
        case "我打" =>{
            tricks += 1
            println(s"BActor(乔峰):看我降龙十八掌 $tricks")
            //通过Sender() 可以获取发送消息的Actor的引用
            sender() ! "我打"
            if(tricks==10){
                sender() ! "我输了,再见"
                context.stop(self)//停止接收消息
                context.system.terminate()//退出
            }
            Thread.sleep(1000)

        }
        case _ =>
    }
}
