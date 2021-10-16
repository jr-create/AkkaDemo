package com.wjr.akka.customerService.util

import akka.actor.ActorContext

/**
 *
 * @Package: com.wjr.akka.customerService.util
 * @ClassName: ActorUtil
 * @Author: 86157
 * @CreateTime: 2021/7/13 23:00
 * @Description:
 */
class ActorUtil {
    def test(context: ActorContext,message : Array[String], maybeTuple : (String, Any)) = {
        context.actorSelection(s"akka.tcp://client@${maybeTuple._1}:${maybeTuple._2}/user/${message(0)}")
    }

}
