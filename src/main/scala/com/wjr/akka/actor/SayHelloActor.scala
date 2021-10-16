package com.wjr.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
 *
 * @Package: com.wjr.akka.actor
 * @ClassName: SayHelloActor
 * @Author: 86157
 * @CreateTime: 2021/7/11 17:35
 * @Description:
 */
//当我们继承了Actor后，就是一个Actor，重写 核心方法 receive
class SayHelloActor extends Actor {
    /** 说明
     * 1、receive方法，会被Actor的MailBox（实现了Runnable接口）调用
     * 2、当该Actor的 MailBox 接收消息时，就会调用 receive 方法
     *
     * @return type Receive = PartialFunction[Any, Unit] （偏函数）
     *         表示 可以接收 任意类型，没有返回值的函数
     */
    override def receive : Receive = {
        case "exit" => {
            println("接收到exit指令，退出系统")
            context.stop(self) //停止当前的Actor的MailBox（即停止接收消息）
            context.system.terminate() //退出系统
        }
        case string : String => println("（收到:" + string + "回应：" + string + " too）")
        case _ => println("匹配不到")
    }
}

object SayHelloActorDemo {
    //1、先创建要给 ActorSystem （用来创建Actor模型）
    private val actoryFactory = ActorSystem("actoryFactory")
    /** 2、创建一个 Actor（模型） 的同时，返回Actor的 ActorRef（用来发送消息）
     * （1）Props [SayHelloActor] 创建了一个 SayHelloActor 实例，使用了反射机制
     * （2）"sayHelloActor" Actor取名
     * （3）sayHelloActorRef : ActorRef  就是 Props [SayHelloActor] 的 ActorRef（引用）
     * （4）创建的 SayHelloActor 实例 后，被 ActorSystem 接管
     */
    private val sayHelloActorRef : ActorRef = actoryFactory.actorOf(Props [SayHelloActor], "sayHelloActor")

    def main(args : Array[String]) : Unit = {

        /** 给SayHelloActor 发消息（邮箱）
         * 有序的
         * 给谁发送  格式：谁 ! 消息内容
         */
        sayHelloActorRef ! "hello"
        sayHelloActorRef ! "ok"
        sayHelloActorRef ! 12
        //如果退出Actor模型
        sayHelloActorRef ! "exit"


    }

}
