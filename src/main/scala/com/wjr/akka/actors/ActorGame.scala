package com.wjr.akka.actors

import akka.actor.{ActorRef, ActorSystem, Props}

/**
 *
 * @Package: com.wjr.akka.actors
 * @ClassName: ActorGame
 * @Author: 86157
 * @CreateTime: 2021/7/11 19:28
 * @Description:
 */
object ActorGame extends App {
    //创建ActorSystem
    private val actoryFactory : ActorSystem = ActorSystem("actoryFactory")
    //需要先创建BActor引用，因为 AActor 需要传入 BActorRef
    private val bActorRef : ActorRef = actoryFactory.actorOf(Props[BActor], "BActor")
    //创建 AActor 引用，因为有参数，所以使用 new
    private val aActorRef : ActorRef = actoryFactory.actorOf(Props (new AActor(bActorRef)), "AActor")

    //aActorchuzhoa
    aActorRef ! "start"


}
