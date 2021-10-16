package com.wjr.akka.customerService


/**
 *
 * @Package: com.wjr.akka.customerService
 * @ClassName: Test
 * @Author: 86157
 * @CreateTime: 2021/7/13 19:38
 * @Description:
 */
object Test extends App {
    /**
     * stripMargin:是以 “|”作为出来连接符，将每一行需要固定对齐。
     */
    println(
            s"""
               |akka.actor.provider="akka.remote.RemoteActorRefProvider"
               |akka.remote.netty.tcp.hostname=123
               |akka.remote.netty.tcp.port=123
        """.stripMargin('|'))

}
